#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <windows.h>


/**
 * Get the Default VM Lib path specified in the Windows Registery
 * @param vmlibpath will contain the path found after the call is complete
 * @return TRUE if the vmlibpath is found in the registry FALSE otherwise
 */
BOOL getDefaultVMLibPath(char* vmlibpath)
{
   
   HKEY hKey;           //Handle to JRE Key
   HKEY hVersionKey;    //Handle to a specific version Key
   char* jreKey = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
   char* jreVersionKey;  //Key name for a specific version
   char version[32];             //Holds the Current JRE Version after reading it from the registry
   DWORD dwVersionBufLen=32;
   char runtimelib[255];         //Holds the fule file name to the runtime lib after reading it from the registry
   DWORD dwruntimelibBufLen=255;
   LONG lRet; //Return success / error from reading the registry

   //Get the Current Default Version of the JRE
   lRet = RegOpenKeyEx(
                      HKEY_LOCAL_MACHINE,
                      jreKey,
                      0,
                      KEY_QUERY_VALUE,
                      &hKey );

   if (lRet != ERROR_SUCCESS ) {
      return (FALSE);
   }

   //Get CurrentVersion Value
   lRet = RegQueryValueEx( hKey, "CurrentVersion", NULL, NULL,
                           (LPBYTE) version, &dwVersionBufLen);

   if (lRet != ERROR_SUCCESS )
   {
      RegCloseKey(hKey);
      return (FALSE);
   }

   //Close the Key the Current Default VM
   RegCloseKey(hKey);

   //Get the Version specific Key
   jreVersionKey = lstrcat(jreKey, "\\");
   jreVersionKey = lstrcat(jreVersionKey, version);

   //Get the RunTime lib path
   lRet = RegOpenKeyEx(
                      HKEY_LOCAL_MACHINE,
                      jreVersionKey,
                      0,
                      KEY_QUERY_VALUE,
                      &hVersionKey);

   if (lRet != ERROR_SUCCESS )
   {
      return (FALSE);
   }

   //Get the runtime libary for this VM Version
   lRet = RegQueryValueEx( hVersionKey, "RuntimeLib", NULL, NULL,
                           (LPBYTE) runtimelib, &dwruntimelibBufLen);

   if (lRet != ERROR_SUCCESS )
   {
      RegCloseKey(hVersionKey);
      return (FALSE);
   }

   //vmlibpath = currentVMLibPath;
   lstrcpy(vmlibpath, runtimelib);

   //Close the specific VM Version Key
   RegCloseKey(hVersionKey);

   return (TRUE);
}

/**
 * Get the Function pointer to the JNI CreateJavaVM function in the specified library
 * @param vmlibpath absolute path to the jvm.dll used to create the VM Instance
 * @return null if the Library is not Found
 */
void *getJNI_CreateJavaVM(char* vmlibpath)
{
   HINSTANCE hVM = LoadLibrary(vmlibpath);
   if (hVM == NULL)
   {
      return (NULL);
   }
   return (GetProcAddress(hVM, "JNI_CreateJavaVM"));
}

/**
 * Get the Directory the executing exe is residing in
 * param full exe path in, exe Dir out
 * param application name out
 */
void getExeDirectory(char* exeDir, char* appName)
{
   DWORD dwModulePath = _MAX_PATH;
   char tempExeDir[_MAX_PATH];
   char tempAppName[_MAX_PATH];
   char modulePath[_MAX_PATH];
   char drive[_MAX_DRIVE];
   char dir[_MAX_DIR];
   char fname[_MAX_FNAME];
   char ext[_MAX_EXT];

   dwModulePath = GetModuleFileName( NULL, modulePath, dwModulePath);

   _splitpath(modulePath, drive, dir, fname, ext);
   lstrcpy(tempExeDir, drive);
   lstrcat(tempExeDir, dir);
   lstrcpy(exeDir, tempExeDir);
   lstrcpy(appName, fname);
}

/**
 * Show a Message Box or print a message based on the console flag
 *
 */
int showError(BOOL console, char* title, char* message) {
    if(console == TRUE) {
        fprintf(stdout, "%s: %s\n", title, message);
        return;
    }
    else {
        return MessageBox(NULL, message, title, MB_ICONERROR + MB_OK);
    }
}

