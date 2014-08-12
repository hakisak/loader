#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <windows.h>
#include "utility.c"

/**
 *
 * Main Application EexeDirntry
 *
 */
int main(int argc, char *argv[], char **envp) {

   JavaVMOption options[2];
   JavaVMInitArgs vm_args;
   JavaVM *jvm;
   JNIEnv *env;
   long result;
   jmethodID mid;
   jfieldID fid;
   jobject jobj;
   jclass cls;
   jthrowable exp;
   jstring jstr;
   jobjectArray args;
   int i, asize;
   long (*pcreateVM)();
   char vmPath[_MAX_PATH];
   BOOL foundLibPath = FALSE;
   BOOL baseDirDefined = FALSE;
   DWORD dwCurrentDirBufLen = _MAX_PATH;
   char userDir[_MAX_PATH];
   char* baseDir;
   char currentDir[_MAX_PATH];
   char exeDir[_MAX_PATH];
   char homeDir[_MAX_PATH];
   char classpathOption[2048];
   char* mainClass = "netx/jnlp/runtime/Boot13";

   //Print Out Netx Version
   printf("Netx native launcher version 0.5\n\n");

   //Get the User Profile Directory
   GetEnvironmentVariable("USERPROFILE", userDir, _MAX_PATH);

   //Process Args for Native Launcher
   for (i=1;i<argc;i++) {
      if (lstrcmp(argv[i], "-vmlib") == 0) {
         if (i+1 <= argc) {
            lstrcpy(vmPath, argv[i+1]);
            printf("Using vmlibPath: %s\n", vmPath);
            foundLibPath = TRUE;
            break;
         }
      }
      if (lstrcmp(_argv[i], "-basedir") == 0) {
         baseDirDefined = TRUE;
      }
   }

   //Find the Default VM Path from the Windows Registry or use the one specfied on -vmlib argument
   if (foundLibPath == FALSE) {
      foundLibPath = getDefaultVMLibPath(vmPath);
   }

   if (foundLibPath == FALSE) {
      printf("Error: Default Java VM Library not found: \n\tReinstall the JDK/JRE or use the \n\t-vmlib to specify the jvm libary to use.\n\n", vmPath);
      exit (-1);
   }

   //Get the function pointer for the JNI_CreateJavaVM function in the library specified
   pcreateVM = getJNI_CreateJavaVM(vmPath);
   if (pcreateVM == NULL) {
      printf("Error: Unable to connect to VM library: %s, \n\tReinstall the JDK/JRE or use the \n\t-vmlib to specify the jvm libary to use.\n\n", vmPath);
      exit (-1);
   }

   //Get the Current Directory and Directory of the EXE
   dwCurrentDirBufLen = GetCurrentDirectory(dwCurrentDirBufLen, currentDir);
   getExeDirectory(exeDir, homeDir);

   //setup classpath
   lstrcpy(classpathOption, "-Djava.class.path=");
   lstrcat(classpathOption, exeDir);
   lstrcat(classpathOption, "netx.jar;");
   lstrcat(classpathOption, homeDir);
   lstrcat(classpathOption, "lib\\netx.jar");

   //Setup Options for the VM Instance
   options[0].optionString = classpathOption;
   //options[0].optionString = lstrcat("-Djava.class.path=", netxJarPath);
   vm_args.version = JNI_VERSION_1_2;
   vm_args.options = options;
   vm_args.nOptions = 1;
   vm_args.ignoreUnrecognized = JNI_FALSE;

   //Create the VM Instance by calling the function pointed to by pcreateVM
   result = (*pcreateVM)(&jvm,(void **)&env, &vm_args);

   //There was a JNI Error
   if (result == JNI_ERR ) {
      fprintf(stderr, "Error: JNI Error invoking the JVM\n\n");
      exit (-1);
   }

   //Find the Main Class for Netx
   cls = (*env)->FindClass(env, mainClass);
   exp = (*env)->ExceptionOccurred(env);
   if ( exp != NULL ) {
      fprintf(stderr, "Error: Can't find main class:%s\n", mainClass);
      (*env)->ExceptionDescribe(env);
      exit (-1);
   }
   (*env)->ExceptionClear(env);

   //Get the main Method in the Class
   mid = (*env)->GetStaticMethodID(env, cls, "main", "([Ljava/lang/String;)V");
   exp = (*env)->ExceptionOccurred(env);
   if (mid == 0 || exp != NULL) {
      fprintf(stderr, "Error: Can't find main method in class:%s\n", mainClass);
      (*env)->ExceptionDescribe(env);
      exit(1);
   }
   (*env)->ExceptionClear(env);

   //Setup arguments for Main
   if (baseDirDefined == TRUE) {
      args = (*env)->NewObjectArray(env, argc-1, (*env)->FindClass(env, "java/lang/String"), NULL);
   }
   else {
      args = (*env)->NewObjectArray(env, argc+1, (*env)->FindClass(env, "java/lang/String"), NULL);

      //place default baseDir at end of Args
      jstr = (*env)->NewStringUTF(env, "-basedir");
      (*env)->SetObjectArrayElement(env, args, argc-1, jstr);
      baseDir = lstrcat(userDir, "\\.netx");
      CreateDirectory(baseDir, NULL);
      jstr = (*env)->NewStringUTF(env, baseDir);
      (*env)->SetObjectArrayElement(env, args, argc, jstr);
   }

   //Setup arguments for Main
   for (i=1;i<argc;i++) {
      jstr = (*env)->NewStringUTF(env, argv[i]);
      (*env)->SetObjectArrayElement(env, args, i-1, jstr);
   }

   exp = (*env)->ExceptionOccurred(env);
   if (args == 0 || exp != NULL) {
      fprintf(stderr, "Error: Creating main arguments\n");
      (*env)->ExceptionDescribe(env);
      exit(-1);
   }
   (*env)->ExceptionClear(env);

   (*env)->CallStaticVoidMethod(env, cls, mid, args);
   exp = (*env)->ExceptionOccurred(env);
   if (exp != NULL) {
      fprintf(stderr, "Error: Executing main method\n");
      (*env)->ExceptionDescribe(env);
      exit(-1);
   }
   (*env)->ExceptionClear(env);

   (*jvm)->DestroyJavaVM(jvm);

   return(0);
}



