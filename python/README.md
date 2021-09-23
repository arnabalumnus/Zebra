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
>DataFile Path:  
   
### 2. Logistic Regression
The file responsible for generating model using Logistic Regression algo is:
1. `Zebra_Logistic_Regrasion_Keras.ipynb`

### 3. Legacy algorithms(Not is use)
This `Rendom Forest`, `Support vector machines(SVM)`,  `Decision Forest` algorithms workers on `Sklearn` package.       

And till today *( i.e. 22nd sept, 2021)* this algorithms doesn't have support in `TensorFlow APIs`. And according to our RND `TensorFlow` is the only one which can build model for` mobile` devices like `Android`.  

##### How to generate Legacy model 
Use this comment to generate models. Model will be generated as `.pkl` file.
```
> python training_impact.py
> python training_fall.py
```
##### Important Note
1. Need to have `training_impact.py` *file* and `DeviceAbuseDataImpact` *folder* that contains `.csv` *data files* must be at the same location.
2. Need to have `training_fall.py` *file* and `DeviceAbuseDataFall` *folder* that contains `.csv` *data files* must be at the same location.
##### How to run
To run open commend prompt(cmd) in this current directory. Then execute this below commend.
`python_filename.py` followed by a `space` followed by `data.csv`
```
For an example:
> python ZebraDeviceAbuseServerTest.py "SampleDataFile.csv"
```
##### Output
Find the output in cmd itself and in inside `./output/data` folder.

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


## Software requirement

#### To Run in cloud
1. The best option to run `.ipynb` file in cloud is [Google Colab](https://colab.research.google.com/#create=true).
And all `.ipynb` files configured in such a way that it have the best compatibility with [Google Colab](https://colab.research.google.com/#create=true).
> Files available in colab folder should be run using Google colab

#### To Run in local
1. Need to have [Python](https://www.python.org/downloads/) installed in system.
2. Need to install some python package/module using `pip install` commend.
3. Python files(`.py`) can be run using commend line interface (CMD)
4. To run `.ipynb` files in local, **[JupiterNotebook](https://jupyter.org/)** is one of the option. And along with that **[Anaconda](https://www.anaconda.com/)** installation is also required.
5. You can use [PyCharm](https://www.jetbrains.com/pycharm/download/#section=windows) IDE as well. But project is not prepared with that compatible structure.
> Files available in python_legacy folder should be run in local by using CMD


## Folder structure
```
python
├── colab
|     ├── AnnZebraFreefall.ipynb
|     ├── AnnZebraImpact.ipynb
|     ├── VisualizeDataset.ipynb
|     ├── Zebra_Data_Frame_genarator.ipynb
|     └── Zebra_Logistic_Regrasion_Keras.ipynb
|
├── eventData_LogisticRegration
|      └── DeviceAbuseData.zip
|
├── eventDataFiltered
|     ├── DeviceAbuseDataFall.zip
|     └── DeviceAbuseDataImpact.zip
|
├── eventDataRaw
|     ├── DeviceAbuseDataFallAll.zip
|     └── DeviceAbuseDataImpactAll.zip
|
├── python_helper_files
|       └── ...
|
├── python_legacy
|       |
|       ├── img
|       |   ├── DADeskSlam.png
|       |   ├── DAFall.png
|       |   ├── DAFloorSlam.png
|       |   ├── DAOThrow.png
|       |   ├── DAPocketSlip.png
|       |   ├── DASitFall.png   
|       |   ├── DATableFall.png
|       |   ├── DAThrowCatch.png
|       |   ├── DAUThrow.png
|       |   ├── DAWallSlam.png
|       |   └── Unknown.png
|       |
|       ├── predict_fall.py
|       ├── predict_impact.py
|       ├── prefallClassifier_rf.pkl
|       ├── prefallClassifier_svm.pkl
|       ├── preimpactClassifier_rf.pkl
|       ├── preimpactClassifier_svm.pkl
|       ├── run_all_prefall_data_files.bat
|       ├── run_all_preimpact_data_files.bat
|       ├── training_fall.py
|       ├── training_impact.py
|       ├── ZebraDeviceAbuseServerTest.py
|       |
|       └── output                  (output will generate at run time)*
|               └── SampleDataFile
|                   ├── SampleDataFile-desc.txt
|                   ├── SampleDataFile-dtsv.png
|                   ├── SampleDataFile-ff.png
|                   ├── SampleDataFile-fin.htm
|                   ├── SampleDataFile-fin.txt
|                   ├── SampleDataFile-iin.htm
|                   ├── SampleDataFile-iin.txt
|                   ├── SampleDataFile-im.png
|                   ├── SampleDataFile-tsv.png
|                   ├── SampleDataFile-xyz.png
|                   └── SampleDataFile-zone.png
|
|
├── tfliteModel
|       ├── neural_network_fall_data_model.tflite
|       ├── neural_network_impact_data_model.tflite
|       ├── zebra_ds_model.tflite
|       ├── zebra_ff_model.tflite
|       ├── zebra_fs_model.tflite
|       ├── zebra_hf_model.tflite
|       ├── zebra_ot_model.tflite
|       ├── zebra_pf_model.tflite
|       ├── zebra_ut_model.tflite
|       └── zebra_ws_model.tflite
|
├── .gitignore
└── README.md
```

