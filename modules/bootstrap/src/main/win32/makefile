CPP     = bcc32
LINKER  = ilink32

app:
    $(CPP) +${BCCDIR}\bcc32.cfg -W myapp.c
    #$(CPP) +${BCCDIR}\bcc32.cfg -W netxw.c -c
    #${LINKER} -Lc:\borland\bcc55\Lib c0w32.obj netxw.obj, netxw.exe,, import32.lib cw32.lib,, netx.res

app_console:
    $(CPP) +${BCCDIR}\bcc32.cfg -WC myapp_console.c
    #${LINKER} -Lc:\borland\bcc55\Lib c0x32.obj pod_console.obj, pod_console.exe,, IMPORT32.LIB CW32.LIB,, pod.res
    #$(CPP) +${BCCDIR}\bcc32.cfg -W netxw.c -c
    #${LINKER} -Lc:\borland\bcc55\Lib c0w32.obj netxw.obj, netxw.exe,, import32.lib cw32.lib,, netx.res

all: app app_console

clean:
    del *.obj
    del *.exe
    del *.tds

