==================================
Xito Application Manager
==================================

The Application Manager is a simple to use Network Application Manager which provides the following functionality.

Some of the major functionality of Xito App Manager is:

- Manages and Launches Web Based Java Applets
- Manages and Launches Web Based JNLP Applications
- Manages and Launches Local or Remote Java Applications
- Manages and Launches Local Native Applications
- Manages and Launches Web Sites in your default Browser
- Optionally launches JNLP Applications using Java Web Start
- Optionally launches Applications in the Same VM increasing performance and reducing memory requirements
- Provides Full Security Manager for execution of untrusted code
- Allows Drag and Drop Application URLs from Web Browser

Xito Application manager is based on the following Services: 

- Xito BootStrap
- Xito Launcher Service (JNLP, Applet, Java Apps etc)
- Xito Preference Service
- Xito XML Document Service
- Xito Control Panel
- Xito Splash Screen Service
- Xito JDIC Wraps the JDIC (Java Desktop Integration Components from java.net into an Xito Service)

Installation
===================================
Unzip the AppManager distribution: appmanager-[version].zip into a directory of your choice.

To start the application using java command, cd to the distribution directory and type:

java -jar boot.jar

or

javaw -jar boot.jar

If you do not CD to the distribution directory you need to specify the boot directory as a command line argument. For example if you installed app manager in c:\xito and you want to start the application from a different directory use:

java -jar c:\testApp\boot.jar -bootdir c:\xito

For more information visit http://xito.sourceforge.net

LICENSE Notes
===================================
Xito App Manager is written as part of the Xito project and is licensed under the 
COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0

For more information visit http://xito.sourceforge.net


