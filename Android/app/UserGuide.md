# User Guide

## Installation
Get the apk into your device. User can copy it from pc or directly download in android device.
Then locate the apk in storage. It will be available in download folder if downloaded in android device.
Flow below steps to Install
1. Tap on `Zebra -v1.0.15-debug.apk` to install.
2. Then tap on `Install` button and wait.
3. Next tap on `Open` button. 

>Note: If app is not installed in this process then or else next step can be skipped
## Resolve debug apk installation problems
1. Allow app installation from unknown resource in settings.       
2. GooglePlayProtect also stops to install debug build. To fix that follow below steps.
    1. On your Android phone or tablet, open the Google Play Store app .
    2. Tap Menu Play Protect Settings .
    3.  Turn Scan apps with Play Protect on or off.
## App overview / UI elements
At the initial point this app will ask for storage permission to save accelerometer data in CSV file. 
Then in dashboard this app has 4 buttons.
1. Accelerometer
2. Service
3. Export Data
4. Database

#### Accelerometer
In this screen we have two graphs
1. Accelerometer data of x,y,z directions
2. Calculated TSV value from x,y,z value

#### Service
This screen is for start the service for recording accelerometer data into database. We have 3 different options for frequency. 
1. Low ~5/sec
2. Mid ~15/sec
3. High ~50/sec
Then we have a button `Start Lifetime service` to start the service. How ever the service will also start on Power disconnect and Reboot.

#### Export Data
Tap on this button to export accelerometer data in a CSV file. Then saved file location will be show in Toast massage. We have taken the storage permission for this at the beginning of the app.

#### Database
This screen is to just verify the accelerometer data recoding is happening in background service. It have Two buttons.
1. Get all event
2. Last timestamp

## Extras
This are some info about accelerometer data collecting service.
1. ~~This service will stop on Power connect.~~
2. ~~And service will start again on Power disconnect or Reboot device.~~
3. Accelerometer data will be exported on Power connect in `ZebraApp/data` folder.

## Exported data & logs
1. All the accelerometer **data** recorded in database will be **exported** in a `.csv` file with current timestamp **whenever** device will be connected in AC power for **charging**. 
2. Along with this a `log.txt` file will also generate after analysis the data in `.csv` file.

## Zip csv files
1. All saved csv files can be zipped using `Archive csv files` button in `Database Screen`
2. No zip file will be saved if any csv file is unavailable in `csvFlies` folder 

## Android device Folder structure 
The exported `folder/file` structure will be something like this.

```
sdcard
├── ...
└── ZebraApp
        ├── csvData
        |     └── 2021, Apr-30 Time-13 12 25.csv
        ├── logs
        |     └── log-2021, Apr-30 Time-13 12 26.txt
        └──  zipFiles
              └── 2021, Jun-29 Time-13 12 26.zip

```