# Zebra Machine Learning part


## Introduction
In this Machine learning part of Zebra project has been done in  3 ways.
1. **Artificial Neural Network**
2. **Logistic Regression**
3. **Legacy algorithms**( Random Forest / Support vector machines(SVM) / Decision Forest)       

The `colab` folder contains all python files that need to be run using [Google colab](https://colab.research.google.com/#create=true) to generate `model.tflite` file.

### 1. Artificial Neural Network (ANN)
The files responsible for generating model using ANN algo are:
1. `AnnZebraFreefall.ipynb`
2. `AnnZebraImpact.ipynb`

### 2. Logistic Regression
The file responsible for generating model using Logistic Regression algo is:
1. `Zebra_Logistic_Regrasion_Keras.ipynb`

### 3. Legacy algorithms
This `Rendom Forest`, `Support vector machines(SVM)`,  `Decision Forest` algorithms workers on `Sklearn` package.       

And till today *( i.e. 22nd sept, 2021)* this algorithms doesn't have support in `TensorFlow APIs`. And according to our RND `TensorFlow` is the only one which can build model for` mobile` devices like `Android`.  

>So as of now we are using `Artificial Neural Network` & `Logistic Regression` algorithms are only way to build our model using `Keras` & `TensorFlow APIs`.


## APIs
This following APIs has been used mostly to generate model for Zebra app using machine learning. 
So its good to have prior knowledge on this libraries in python.

1. Pandas
2. numpy
3. Tensorflow
4. Keras
5. Sklearn
6. matplotlib.pyplot
7. seaborn

# TODO
## Folder structure
```
python
├── DeviceAbuseDataImpact
|       ├── FF03XP_2017_08_02_17_00_13  - prefall.csv
|       ├── ...
|       └── UT05XXC_2017_09_04_16_52_46  - prefall.csv
|
├── DeviceAbuseDataFall
|       ├── DSXXXX_2017_11_06_09_15_41  - preimpact.csv
|       ├── ...
|       └── WSXXXX_2017_11_01_18_55_37  - preimpact.csv
|
├── img
|   ├── DADeskSlam.png
|   ├── DAFall.png
|   ├── DAFloorSlam.png
|   ├── DAOThrow.png
|   ├── DAPocketSlip.png
|   ├── DASitFall.png   
|   ├── DATableFall.png
|   ├── DAThrowCatch.png
|   ├── DAUThrow.png
|   ├── DAWallSlam.png
|   └── Unknown.png
|
├── prefallClassifier_rf.pkl
├── prefallClassifier_svm.pkl
├── preimpactClassifier_rf.pkl
├── preimpactClassifier_svm.pkl
|
├── training_impact.py
├── training_fall.py
├── ZebraDeviceAbuseServerTest.py
|
├── SampleDataFile.csv      (data file)*
├── output                  (generated)*
|   └── SampleDataFile
|       ├── SampleDataFile-desc.txt
|       ├── SampleDataFile-dtsv.png
|       ├── SampleDataFile-ff.png
|       ├── SampleDataFile-fin.htm
|       ├── SampleDataFile-fin.txt
|       ├── SampleDataFile-iin.htm
|       ├── SampleDataFile-iin.txt
|       ├── SampleDataFile-im.png
|       ├── SampleDataFile-tsv.png
|       ├── SampleDataFile-xyz.png
|       └── SampleDataFile-zone.png
|
├── .gitignore
└── README.md
```
## Model generation
#### How to generate model
Use this comment to generate models. Model will be generated as `.pkl` file.
```
> python training_impact.py
> python training_fall.py
```

#### Important Note
>1. Need to have `training_impact.py` *file* and `DeviceAbuseDataImpact` *folder* that contains `.csv` *data files* must be at the same location.
>2. Need to have `training_fall.py` *file* and `DeviceAbuseDataFall` *folder* that contains `.csv` *data files* must be at the same location.
## How to run
To run open commend prompt(cmd) in this current directory. Then execute this below commend.
`python_filename.py` followed by a `space` followed by `data.csv`
>For an example

```
> python ZebraDeviceAbuseServerTest.py "SampleDataFile.csv"
```
## Output
Find the output in cmd itself and in inside `./output/data` folder.