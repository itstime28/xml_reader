#Creates Android virtual device
android create avd -n sample-test -t 17

#launch AVD
emulator -avd sample-tesr

#install app
adb wait-for-device install sample-app.apk

#launch app
adb wait-for-device shell am start -a android.intent.action.MAIN -n package-name/class-name

#take a screenshot
adb shell screencap -p /sdcard/screenshot.jpg

#pull the screenshot to machine
adb pull /sdcard/screenshot.jpg

#uninstall app
adb shell pm uninstall package-name

#kill avd or adb emu kill
adb kill-server
