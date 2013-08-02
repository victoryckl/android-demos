::usge: trackmem.bat com.android.browser
@echo off
:LOOP
adb shell procrank | grep %1
goto LOOP