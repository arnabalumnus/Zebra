# -*- coding: utf-8 -*-
"""
Created on Thu Oct 26 14:10:45 2017

@author: Souvik
"""
import pandas as pd
import re
import os

def preProcess(rowCount):
    trainingDataFolder = "DeviceAbuseDataFall\\"

    dirPath = os.path.join(os.getcwd(), trainingDataFolder)
    files = [f for f in os.listdir(dirPath) if os.path.isfile(os.path.join(dirPath, f))]

    fileList = []
    finalDF = pd.DataFrame()
    
    for file in files:
        # Importing the dataset
        orgData = pd.read_csv(dirPath + file)
        data = pd.DataFrame(orgData.iloc[:, 1:4])

        print('Current File :', file, '# Rowcount :', data.shape[0])       
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
#print(finalDF.info())
#finalDF.to_csv('prefallFinalDF.csv')

X_train = finalDF.iloc[:, 0:len(finalDF.columns)-1].values
y_train = finalDF.iloc[:, len(finalDF.columns)-1].values
   
# Fitting SVM to the Training set
# Linear Support Vector Classification(SVC)
# Support Vector Machine(SVM)
from sklearn.svm import SVC
svm_classifier = SVC(kernel = 'linear', random_state = 0)
svm_classifier.fit(X_train, y_train)

import joblib
joblib.dump(svm_classifier, 'prefallClassifier_svm.pkl', compress=9) 

# Fitting RF to the Training set
from sklearn.ensemble import RandomForestClassifier
rf_classifier = RandomForestClassifier(n_estimators = 100, criterion = 'entropy', random_state = 0)
rf_classifier.fit(X_train, y_train)

joblib.dump(rf_classifier, 'prefallClassifier_rf.pkl', compress=9)







