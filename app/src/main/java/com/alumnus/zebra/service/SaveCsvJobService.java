package com.alumnus.zebra.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.room.Room;

import com.alumnus.zebra.db.AppDatabase;
import com.alumnus.zebra.db.entity.AccLogEntity;
import com.alumnus.zebra.utils.DateFormatter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * https://codinginflow.com/tutorials/android/jobscheduler
 */
@Deprecated
public class SaveCsvJobService extends JobService {
    private static final String TAG = "SaveCsvJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    /**
     * Export records into a .csv file
     * Delete old records that already exported into csv file
     * @param params
     */
    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File exportDir;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    exportDir = new File(getExternalFilesDir("ZebraApp"), "autoLog"); // Working in API 30 i.e. Android 11 and higher
                else
                    exportDir = new File(Environment.getExternalStorageDirectory(), "ZebraApp/autoLog"); // Working in API 29 i.e. Android 10 and lower
                if (!exportDir.exists()) {
                    if(!exportDir.mkdirs()){
                        Log.e(TAG, "Error in mkdirs" );
                        return;
                    }
                }

                File file = new File(exportDir, DateFormatter.getTimeStampFileName(System.currentTimeMillis()) + ".csv");
                try {
                    if(!file.createNewFile()){
                        Log.e(TAG, "Error in createNewFile" );
                        return;
                    }
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();

                    List<AccLogEntity> accLogEntities = db.accLogDao().getAll();
                    csvWrite.writeNext(new String[]{"TS", "X", "Y", "Z"});
                    for (AccLogEntity accLogEntity : accLogEntities) {
                        //Which column you want to export
                        String[] arrStr = {String.valueOf(accLogEntity.ts), String.valueOf(accLogEntity.x), String.valueOf(accLogEntity.y), String.valueOf(accLogEntity.z)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    Log.e(TAG, "exportData: Data Exported");
                    /**
                     * DELETE OLD RECORDS
                     */
                    db.accLogDao().deleteAll(System.currentTimeMillis());
                    Log.e(TAG, "Old records DELETED");
                } catch (Exception sqlEx) {
                    Log.e(TAG, sqlEx.getMessage(), sqlEx);
                }
                jobFinished(params, true);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}