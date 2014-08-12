=======================================
Xito ReadMe
=======================================

Xito provides a platform for Java based Rich Internet Applications.
This includes several modules that can be used together or independantly

To build individual modules goto modules/[name] and use ant build


Generating a new Temporary Certificate:

keystore password is: keypass

keytool -keystore xito.keystore -alias xito -list
keytool -keystore xito.keystore -alias xito -delete
keytool -keystore xito.keystore -alias xito -genkey

Enter Xito for First and Last Name, Just Press Enter for everything else.
After updating the temporary certificate you will want to change the trust cert
serial number in appmanager/resources/network_deploy/boot.properties