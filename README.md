# elfyz-data-mobile-logger

This is (so far) a simple application for reading data from several health sensors (via Bluetooth or ANT+), storing them in local SQLite database and uploading them to remote EEGbase portal.

Supported sensors:
* BT blood pressure monitor - [FORA P30 Plus FC](https://www.evito.cz/eshop/product/62)
* BT glucose meter - [FORA Diamond Mini FC](https://www.evito.cz/eshop/product/31)
* ANT+ weight scale - e.g. [eVito SL](https://www.evito.cz/eshop/product/3)
* ANT+ heart rate monitor - e.g. [Garmin Premium heart rate strap](https://www.evito.cz/eshop/product/7)

## Developing in Eclipse ADK

Eclipse IDE doesn't support android libraries very well. To correctly attach MPAndroidChart library (used for charts in data analysis) follow the 4th usage option described on [library page](https://github.com/PhilJay/MPAndroidChart). That is clone the repository and add it as a library to this project via Project -> Properties -> Android -> Libraries -> Add.

## Running on real device

There are several services needed to be installed so make sure you have all of these:

* [ANT Radio Service](https://play.google.com/store/apps/details?id=com.dsi.ant.service.socket)
* [ANT+ Plugins Service](https://play.google.com/store/apps/details?id=com.dsi.ant.plugins.antplus)

If your device doesn't have hardware support for ANT+ and you want to use [USB ANT Stick](http://www.thisisant.com/directory/usb-ant-stick) with USB OTG, you have to install also this service:

* [ANT USB Service](https://play.google.com/store/apps/details?id=com.dsi.ant.usbservice)

You can also download service's APKs from ANT SDKs from [here](http://www.thisisant.com/developer/resources/downloads/) and install them manually with [ADB](http://developer.android.com/tools/help/adb.html).

## Running on Android emulator

1. Run Virtual Device in Android emulator.
2. Install all three services mentioned above (via ADB).
3. Download [ANT Android Emulator Bridge Tool](http://www.thisisant.com/developer/resources/downloads/)
4. Install ANT Emulator Config APK packaged with the bridge toool.
5. Connect ANT USB Stick to your computer.
6. Run the bridge tool, select TCP port and USB with connected ANT Stick and hit the **Connect to USB** button. (You should see *Connected to...* and *Listening for emulator...* messages. If not some programs like *eVito service*, *FORA Health Care Management System* or another bridge tool may be already using the ANT Stick)
7. In emulator open ANT Emulator Config APK and connect to localhost (127.0.0.1) and selected TCP port.
8. Now you can run this app.
