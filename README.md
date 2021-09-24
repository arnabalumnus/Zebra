# Zebra device abuse analysis

### Introduction
This solution helps the Zebra product owner to understand the abuses of their devices by customers. 

To build up this solution we have used **Accelerometer sensor** which is a hardware component & mostly available in all 
android devices. We are collecting data from this sensor output. Then we have some **machine learning** models which are help 
to predict the event occur with that particular device.

This Project basically split into 2 part 
1. Android 
2. python

##### 1. Android
In this android part we have data recording from the sensor. Then save and analysis the data with the help of 
long calculation and TensorFlowLite model.

##### 2. Python 
In the python part we have trained the TensorFlowLite model with some pre-recorded data  of different types of event.

### Reference
For more info & reference please flow the corresponding README available in both [Android](./Android) & [Python](./python) package.

>**Note:** *Go through the README First for better understanding of the project.*
