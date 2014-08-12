========================================================
JDesktop Integration Components (JDIC) 
https://jdic.dev.java.net/
========================================================
License: LGPL

This project creates an Xito Service out of JDIC. 
build jdic-20061102-bin-crossplatform

There was problems running this on Mac OS X so I have recompiled the original source
for Mac OS X. The source code is under jdic-20061102-bin-crossplatform/complete_src

The file:

complete_src/jdic/src/mac_os_x/native/jni/GNUMakefile was using the
/Developer/SDKs/MacOSX10.4u.sdk

This was changed to:

/Developer/SDKs/MacOSX10.5.sdk
on lines 23, 25 of the GNUMakefile

