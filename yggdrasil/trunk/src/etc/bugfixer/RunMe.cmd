@echo off
echo =============================================
echo FIXING BUGS (character set and splash screen)
echo =============================================
call ant -Dos.name=win32.win32.x86 
call ant -Dos.name=win32.win32.x86_64
echo.
echo +++ DONE +++
