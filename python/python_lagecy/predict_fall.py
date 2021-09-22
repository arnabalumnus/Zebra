# -*- coding: utf-8 -*-
"""
Created on Thu Oct 26 16:45:52 2017

@author: Souvik
"""
import numpy as np
import pandas as pd
import re

def preProcess(rowCount):
    dirPath = "DeviceAbuseDataFall\\"

    from os import listdir
    from os.path import isfile, join
    files = [f for f in listdir(dirPath) if isfile(join(dirPath, f))]

    fileList = []
    finalDF = pd.DataFrame()
    
    for file in files:
        print('Current File :', file)
        # Importing the dataset
        orgData = pd.read_csv(dirPath + file)
        data = pd.DataFrame(orgData.iloc[:, 1:4])
       
        if data.shape[0] > rowCount:
            fileList.append(file)
            start = 0
            end = rowCount
           
            count = 0
            for i in range(start, end):
                temp = pd.DataFrame(data.iloc[i:i+1, 0:3])
                temp.columns = ['X'+str(i), 'Y'+str(i), 'Z'+str(i)]
                temp = temp.reset_index(drop=True)
               
                if count == 0:
                    tempDF = temp
                    count = 1
                else:
                    tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
                   
            finalDF = finalDF.append(tempDF)
        elif (data.shape[0] >= (rowCount / 2)):
            fileList.append(file)
            diff = rowCount - data.shape[0]
            start = 0
            end = rowCount
            count = 0
            countDown = rowCount - data.shape[0]

            for i in range(start, end):
                if (countDown > 0):
                    temp = pd.DataFrame(data.iloc[0:1, 0:3])
                    countDown -= 1
                else:
                    temp = pd.DataFrame(data.iloc[(i - diff):(i + 1 - diff), 0:3])

                temp.columns = ['X'+str(i), 'Y'+str(i), 'Z'+str(i)]
                temp = temp.reset_index(drop=True)
               
                if count == 0:
                    tempDF = temp
                    count = 1
                else:
                    tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
                   
            finalDF = finalDF.append(tempDF)
        else:
            print("No. of rows is less than", (rowCount / 2))
           
    fileList = [i.split('_', 1)[0] for i in fileList]
    fileList = [re.sub("\d+", "", i) for i in fileList]
    actualOutput = pd.DataFrame([fileName[:4] for fileName in fileList])
    actualOutput.columns = ["actualOutput"]
    
    finalDF = finalDF.reset_index(drop=True)
    finalDF = pd.concat([finalDF, actualOutput], axis=1)
    
    return finalDF
     
finalDF = preProcess(200)

X_test = finalDF.iloc[:, 0:len(finalDF.columns)-1].values
y_test = finalDF.iloc[:, len(finalDF.columns)-1].values

import joblib
svm_classifier = joblib.load('prefallClassifier_svm.pkl')

# Predicting the Test set results
y_pred = svm_classifier.predict(X_test)

# Making the Confusion Matrix
from sklearn.metrics import confusion_matrix
cm_svm = confusion_matrix(y_test, y_pred)

result = {'y_test' : y_test, 'y_pred' : y_pred}

#pd.DataFrame(result)

np.sum(y_test == y_pred)

import joblib
rf_classifier = joblib.load('prefallClassifier_rf.pkl')

# Predicting the Test set results
y_pred = rf_classifier.predict(X_test)

# Making the Confusion Matrix
from sklearn.metrics import confusion_matrix
cm_rf = confusion_matrix(y_test, y_pred)
print(cm_rf)

result = {'y_test' : y_test, 'y_pred' : y_pred}

#pd.DataFrame(result)

np.sum(y_test == y_pred)


