========================
README
========================

Instructions to build win32 bootstrap EXE


1. Download and install Borland Compiler BC 5.5

2. Modify the system path and add c:\Borland\BC55\bin to the path

3. Create a new System ENV Variable called BCCDIR 
   and set it to c:\Borland\BC55\

4. Copy the bcc32.cfg.txt file to c:\Borland\BC55 Directory 
   and rename it to bcc32.cfg

5. Edit the bcc32.cfg file you just copied and insure that 
   the include and lib directories are set correctly

You should now be able to build the win32 executable using:

make all


