package com.alumnus.zebra.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.room.Room;

import com.alumnus.zebra.db.AppDatabase;
import com.alumnus.zebra.db.entity.AccLogEntity;
import com.alumnus.zebra.db.entity.CsvFileLogEntity;
import com.alumnus.zebra.machineLearning.DataAnalysis;
import com.alumnus.zebra.machineLearning.pojo.TensorFlowModelInput;
import com.alumnus.zebra.pojo.AccelerationNumericData;
import com.alumnus.zebra.pojo.AccelerationStringData;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.alumnus.zebra.utils.Constant.DATABASE_NAME;

@Deprecated
public class ExportFile {
    private static final String TAG = "ExportFile";

    /**
     * @param context    context required for file saving
     * @param exportType folder name will be create based on this params (1. manualLog, 2. autoLog, 3. onPowerConnect)
     */
    public static void exportDataIntoCSVFile(Context context, String exportType) {
        exportType = "data";
        String finalExportType = exportType;
        Runnable runnable = () -> {
            long delete_upto_time_stamp = System.currentTimeMillis();
            File exportDir;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) // Saves file inside root/ZebraApp
                exportDir = new File(Environment.getExternalStorageDirectory(), "ZebraApp/" + finalExportType); // Working in API 29 i.e. Android 10 and lower
            else // Save file inside root/Android/data/com.alumnus.zebra/files/ZebraApp
                exportDir = new File(context.getExternalFilesDir("ZebraApp"), finalExportType); // Working in API 30 i.e. Android 11 and higher
            if (!exportDir.exists()) {
                if (!exportDir.mkdirs()) {
                    Log.e(TAG, "Error in mkdirs");
                    return;
                }
            }
            AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
            while (db.accLogDao().getCount() > Constant.DATA_CHUNK_SIZE) {
                List<AccLogEntity> accLogEntities = db.accLogDao().getAll();
                if (accLogEntities.size() == 0) {
                    Log.w(TAG, "No data available in database");
                    return;
                }
                File file = new File(exportDir, DateFormatter.getTimeStampFileName(System.currentTimeMillis()) + ".csv");
                try {
                    if (!file.createNewFile()) {
                        Log.e(TAG, "Error in createNewFile");
                        return;
                    }
                    db.csvFileLogDao().insert(new CsvFileLogEntity(file.getName(), accLogEntities.size(), 0));
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    csvWrite.writeNext(new String[]{"TS", "X", "Y", "Z"});
                    for (AccLogEntity accLogEntity : accLogEntities) {
                        //Which column you want to export
                        String[] arrStr = {String.valueOf(accLogEntity.getTs()), String.valueOf(accLogEntity.getX()), String.valueOf(accLogEntity.getY()), String.valueOf(accLogEntity.getZ())};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    Log.d(TAG, "Data Exported");
                    Thread.sleep(300);
                    db.accLogDao().deleteAll(delete_upto_time_stamp);
                    MediaScannerConnection.scanFile(context,
                            new String[]{file.toString()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i(TAG, "onScanCompleted: path: " + path);
                                    Log.v(TAG, "onScanCompleted: Uri: " + uri);
                                    try {
                                        //TODO have issue in next line with Android >= Q or R .
                                        // When ever files saving into com.alumnus.zebra folder
                                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                                        readCSVData(inputStream, context);                  // If you need to read the whole file row by row


                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    if (db.csvFileLogDao().getTotalRecordOfAllCSVFile() > 18000 && db.csvFileLogDao().getCsvFileCount() > 2) {//432000
                        DeleteFile.deleteFile(context, finalExportType);
                    }
                } catch (Exception sqlEx) {
                    Log.e(TAG, sqlEx.getMessage(), sqlEx);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @param is      InputStream
     * @param context Context
     */
    private static void readCSVData(InputStream is, Context context) {
        // Read the raw csv file

        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        ArrayList<AccelerationStringData> accelerations = new ArrayList<>();
        ArrayList<AccelerationNumericData> accelerationsDataList = new ArrayList<>();
        // Initialization
        String line = "";

        // Initialization
        try {
            // Step over headers
            String header = reader.readLine();
            String[] headertokens = header.split(",");
            // Read the data
            AccelerationStringData acceleration1 = new AccelerationStringData(headertokens[0].replace("\"", ""),
                    headertokens[1].replace("\"", ""),
                    headertokens[2].replace("\"", ""),
                    headertokens[3].replace("\"", ""));
            accelerations.add(acceleration1);
            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, "Line: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                // Read the data
                AccelerationStringData acceleration = new AccelerationStringData(tokens[0].replace("\"", ""),
                        tokens[1].replace("\"", ""),
                        tokens[2].replace("\"", ""),
                        tokens[3].replace("\"", ""));
                accelerations.add(acceleration);
                AccelerationNumericData accelerationData = new AccelerationNumericData(Long.parseLong(tokens[0].replace("\"", "")),
                        Float.parseFloat(tokens[1].replace("\"", "")),
                        Float.parseFloat(tokens[2].replace("\"", "")),
                        Float.parseFloat(tokens[3].replace("\"", "")));
                accelerationsDataList.add(accelerationData);
            }
            TensorFlowModelInput result = new DataAnalysis().startEventAnalysis(accelerationsDataList, context, null);
            //New approach
            /*ArrayList<Double> tsvList = new ArrayList<>();
            for (AccelerationData accelerationsData : accelerationsDataList) {
                tsvList.add(Calculator.INSTANCE.calculateTSV(accelerationsData.x, accelerationsData.y, accelerationsData.z));
            }
            if (DataAnalyzer.INSTANCE.hasEvent(tsvList)) {
                String result = new MachineLearning().CalculateTSV(accelerationsDataList, context, null);
            }*/

            //detectPlusNoise.noiseZones.size();
        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);

            // Prints throwable details
            e.printStackTrace();
        }
    }
}
