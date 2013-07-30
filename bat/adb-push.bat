@echo off

set adb_p=tool\adb.exe
echo %adb_p%

@echo 等待小机连接 ...
%adb_p% wait-for-device
echo 设备连接成功
echo 挂载system为可读写 ...
%adb_p% remount
call :PRINT_MSG %errorlevel% 挂载system

echo 升级libwebcore.so ...
%adb_p% push data\libwebcore.so /system/lib/
call :PRINT_MSG %errorlevel% 升级libwebcore.so

echo 重启机器...
%adb_p% reboot
pause
GOTO :EOF

:PRINT_MSG
if %1 equ 0 (
echo %2 成功
)else (
echo %2 失败，按任意键退出
pause
exit
)
GOTO :EOF