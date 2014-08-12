===================================
Xito Launcher Service
===================================

The Xito Launcher Service provides added functionality for launching Java and other 
applications in the Xito BootStrap. Xito Launcher service can be used by other applications
to provide mechanisms to easily launch applications over the internet.

Installation
===================
Obtain the Launcher release labeled launcher-xx-xx-xx.zip from the Xito website and unzip the file 
into a directory of your choice. Then launch the sample launcher application by using the command

java -jar boot.jar -bootdir test_boot_dir

This will launch all the services listed in the boot_services.xml file located in the test_boot_dir.
To customize the launcher for use in your application simple use the test_boot_dir as a starting point. 
And create your own services or applications to be launched from the launcher service.

For more information see the BootStrap Developers Guide that is distributed with Xito BootStrap at:
http://xito.sourceforge.net