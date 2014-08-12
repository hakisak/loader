==================================
Xito BootStrap
==================================

The Bootstrap application is a small platform launching environment. It provides 
an easy way to distribute an application over a network and to deploy your 
application environment as a set of standalone services. 

Some of the major functionality of BootStrap is:

- Small Size 250k Launcher
- Secure Environment with Security Prompts
- Default Native Look and Feel 
- Distributed Service Framework
- Application VM sharing (launch multiple apps in the same VM)
- Automatic caching of network applications
- Win32 standalone native executable
- MacOS X standalone .app executable

BootStrap requires at least Java 1.5 is recommended. Your application 
may also have its own requirements.

Note: Native Linux i386 launcher is not currently available 
but is being considered.


Installation
===================================
Unzip the BootStrap distribution: bootstrap-[version].zip into a directory 
of your choice.

To start the application using java command, cd to the distribution 
directory and type:

java -jar boot.jar

or

javaw -jar boot.jar

If you do not CD to the distribution directory you need to specify the boot 
directory as a command line argument. For example if you installed 
bootstrap in c:\testApp and you want to start the application from a different 
directory use:

java -jar c:\testApp\boot.jar -bootdir c:\testApp


WIN32 Execution
===================================
BootStrap includes a bootstrap.exe and bootstrap_console.exe to launch the 
boot environment. The boot.jar and boot.properties files must exist in the 
same directory as the exe's.  Once you configure BootStrap to launch your 
applications you can rename bootstrap.exe and bootstrap_console.exe to what 
ever you like example: foo.exe

=======================================================
Configuring Your Application to Launch with BootStrap
=======================================================
BootStrap is designed to launch an application or set of applications as 
services. These applications may or maynot have a GUI. In order to configure 
BootStrap to launch your app or application services do the following:

For more detailed information see the BootStrap Developers Guide located at http://xito.sourceforge.net

Step 1 - boot.properties
========================

Modify the boot.properties file.  Specify app.name to be the name of your 
application example:

app.name = MyCoolApp

You can also specify an app.icon this is a url to an image file that will be 
used for all GUI Frames in place of the Java Icon. The URL can be relative to
boot.jar if you prefer. Example:

app.icon = MyCoolApp.png  

BootStrap will default to using the Native Look and Feel of the platform. If 
you would like bootstrap to Default to the Java CrossPlatform Look and Feel 
specify the following in the boot.properties:

nativeLAF = false

Step 2 - service configuration
========================
Rename the sample.srv file to the name of your app example mycoolapp.srv. 
Modify the file and Change the settings to reference your application. 
A sample is provided below:

<service>
   <name>com.mycool.MyCoolApp</name>
   <display-name>My Cool Application</display-name>
   <desc>This is my Cool Application</desc>
   <version>1.0.0</version>
   <service-cls>com.mycool.MyCoolAppMainClass</service-cls>
   <append-to-classpath>true</append-to-classpath>
   <classpath>
      <lib path="mycoolapp.jar"/>
   </classpath>
</service>

Note: The lib paths can be full URLs or relative to the location of the 
service file.

Step 3 - boot_services.xml configuration
=========================
Specify that your Service should be booted by the BootStrap. To do this modify 
the boot_services.xml file and specify the following:

<services>
   <service>
      <name>com.mycool.MyCoolApp</name>
      <display-name>My Cool Application</display-name>
      <href>mycoolapp.srv</href>
   </service>
</services>

Note: the href for the service file location can be an absolute URL or relative
to the boot_service.xml file.


That is all there is to it. Now you can launch your application by starting 
the bootstrap with the commands mentioned in the Installation section.


LICENSE Notes
===================================
Bootstrap is written as part of the Xito project and is licensed under the 
Apache License 2.0

For more information visit http://xito.sourceforge.net



