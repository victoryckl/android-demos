#!/system/bin/sh

chmod 777 /data/local/*
/data/local/monkey_test.sh 2>&1 | tee /mnt/sdcard/monekylog.txt &
logcat 2>&1 | tee /mnt/sdcard/logcat.txt &

