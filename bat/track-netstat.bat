@echo off
set count=0
:LOOP
echo -------------------^> %count%
set /a count=%count%+1
adb shell busybox netstat -t | grep CLOSE_WAIT
ping -n 3 0.0.0.0 > nul
goto LOOP