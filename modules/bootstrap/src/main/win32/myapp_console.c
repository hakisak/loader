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
   int i, asize, mb_result;
   long (*pcreateVM)();
   char vmPath[_MAX_PATH];
   BOOL foundLibPath = FALSE;
   BOOL baseDirDefined = FALSE;
   BOOL jnlpURLDefined = FALSE;
   char* jnlp;
   DWORD dwCurrentDirBufLen = _MAX_PATH;
   char userDir[_MAX_PATH];
   char* baseDir;
   char currentDir[_MAX_PATH];
   char exeDir[_MAX_PATH];
   char appName[_MAX_PATH];
   char classpathOption[2048];
   char* mainClass = "org/xito/boot/Boot";
   char mb_message[1024];
   char* msg;

   //Get the User Profile Directory
   GetEnvironmentVariable("USERPROFILE", userDir, _MAX_PATH);

   //Get the exeDirectory and AppName
   getExeDirectory(exeDir, appName);

   //Process Args for Native Launcher
   for (i=1;i<_argc;i++) {
      if (lstrcmp(_argv[i], "-vmlib") == 0) {
         if (i+1 <= _argc) {
            lstrcpy(vmPath, _argv[i+1]);
            foundLibPath = TRUE;
            break;
         }
      }
      /*
      if (lstrcmp(_argv[i], "-basedir") == 0) {
         baseDirDefined = TRUE;
      }
      if (lstrcmp(_argv[i], "-jnlp") == 0) {
         jnlpURLDefined = TRUE;
      }
     */
   }

   //Find the Default VM Path from the Windows Registry or use the one specfied on -vmlib argument
   if (foundLibPath == FALSE) {
      foundLibPath = getDefaultVMLibPath(vmPath);
   }

   if (foundLibPath == FALSE) {
      sprintf(mb_message, "Default Java VM Library not found: \n\nReinstall the JDK/JRE or use the \n-vmlib to specify the jvm libary to use.");
      mb_result = showError(TRUE, appName, mb_message);
      exit(-1);
   }

   //Get the function pointer for the JNI_CreateJavaVM function in the library specified
   pcreateVM = getJNI_CreateJavaVM(vmPath);
   if (pcreateVM == NULL) {
      sprintf(mb_message, "Unable to connect to VM library: %s, \n\nReinstall the JDK/JRE or use the \n-vmlib to specify the jvm libary to use.", vmPath);
      mb_result = showError(TRUE, appName, mb_message);
      exit(-1);
   }

   //Get the Current Directory and Directory of the EXE
   dwCurrentDirBufLen = GetCurrentDirectory(dwCurrentDirBufLen, currentDir);
   

   //setup classpath
   lstrcpy(classpathOption, "-Djava.class.path=");
   lstrcat(classpathOption, exeDir);
   lstrcat(classpathOption, "boot.jar;");
   lstrcat(classpathOption, "lib\\boot.jar");

   //Setup Options for the VM Instance
   options[0].optionString = classpathOption;
   vm_args.version = JNI_VERSION_1_2;
   vm_args.options = options;
   vm_args.nOptions = 1;
   vm_args.ignoreUnrecognized = JNI_FALSE;

   //Create the VM Instance by calling the function pointed to by pcreateVM
   result = (*pcreateVM)(&jvm, (void **)&env, &vm_args);

   //There was a JNI Error
   if (result == JNI_ERR ) {
      mb_result = showError(TRUE, appName, "JNI Error invoking the JVM\n\n");
      exit(-1);
   }

   //Find the Main Class for JNLPBoot
   cls = (*env)->FindClass(env, mainClass);
   exp = (*env)->ExceptionOccurred(env);
   if ( exp != NULL ) {
      sprintf(mb_message, "Can't find main class:%s\n\n", mainClass);
      mb_result = showError(TRUE, appName, mb_message);
      (*env)->ExceptionDescribe(env);
      exit(-1);
   }
   (*env)->ExceptionClear(env);

   //Get the main Method in the Class
   mid = (*env)->GetStaticMethodID(env, cls, "main", "([Ljava/lang/String;)V");
   exp = (*env)->ExceptionOccurred(env);
   if (mid == 0 || exp != NULL) {
      sprintf(mb_message, "Can't find main method in class:%s\n", mainClass);
      mb_result = showError(TRUE, appName, mb_message);
      (*env)->ExceptionDescribe(env);
      exit(1);
   }
   (*env)->ExceptionClear(env);

   //Setup arguments for Boot
   args = (*env)->NewObjectArray(env, 4, (*env)->FindClass(env, "java/lang/String"), NULL);
   jstr = (*env)->NewStringUTF(env, "-bootdir");
   (*env)->SetObjectArrayElement(env, args, 0, jstr);
   jstr = (*env)->NewStringUTF(env, exeDir);
   (*env)->SetObjectArrayElement(env, args, 1, jstr);

   jstr = (*env)->NewStringUTF(env, "-appname");
   (*env)->SetObjectArrayElement(env, args, 2, jstr);
   jstr = (*env)->NewStringUTF(env, appName);
   (*env)->SetObjectArrayElement(env, args, 3, jstr);

   //Call main with args
   (*env)->CallStaticVoidMethod(env, cls, mid, args);
   exp = (*env)->ExceptionOccurred(env);
   if (exp != NULL) {
      //cls = (*env)->GetObjectClass(env, exp); 
      //mid = (*env)->GetMethodID(env, cls, "printStackTrace", "()V");
      //(*env)->CallVoidMethod(env, exp, mid);
  
      msg = "Exception thrown from main method\n";
      mb_result = showError(TRUE, appName, msg);
      exit(-1);
   }
   (*env)->ExceptionClear(env);
   (*jvm)->DestroyJavaVM(jvm);

   return(0);
}



