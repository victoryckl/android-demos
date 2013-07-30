@echo off

set adb_p=tool\adb.exe
echo %adb_p%

@echo ?ȴ?С??��?? ...
%adb_p% wait-for-device
echo ?豸��?ӳɹ?
echo ????systemΪ?ɶ?д ...
%adb_p% remount
call :PRINT_MSG %errorlevel% ????system

echo ????libwebcore.so ...
%adb_p% push data\libfor-test.so /system/lib/
call :PRINT_MSG %errorlevel% ????libfor-test.so

echo ????????...
%adb_p% reboot
pause
GOTO :EOF

:PRINT_MSG
if %1 equ 0 (
echo %2 ?ɹ?
)else (
echo %2 ʧ?ܣ??????????˳?
pause
exit
)
GOTO :EOF
