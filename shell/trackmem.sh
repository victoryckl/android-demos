#!/bin/bash

while true; do
adb shell procrank | grep $1
sleep 1
done

# usge:
# chmod 775 trackmem.sh
# ./trackmem.sh com.android.browser
