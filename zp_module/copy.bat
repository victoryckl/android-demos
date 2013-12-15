adb shell mount
adb shell mount -o remount /dev/block/mtdblock0 /system
adb shell rm /system/app/Email.*
adb shell df
adb push .\product\libZPClient.so /system/lib/
adb push .\product\libZPService.so /system/lib/ 
adb push .\product\svctest /system/bin/
adb push .\product\zpserver /system/bin/
adb shell chmod 777 /system/bin/*
adb shell
pause