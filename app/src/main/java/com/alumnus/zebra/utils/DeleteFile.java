package com.alumnus.zebra.utils;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.alumnus.zebra.db.AppDatabase;

import java.io.File;

import static com.alumnus.zebra.utils.Constant.DATABASE_NAME;

@Deprecated
public class DeleteFile {

    private static final String TAG = "ExportFile";
    private static final String FILE_PATH_PREFIX = "/storage/emulated/0/ZebraApp/";

    public static void deleteFile(Context context, String exportType) {
        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
        String fileName = db.csvFileLogDao().getOldestCSVFile();
        String path = FILE_PATH_PREFIX + exportType + "/" + fileName;
        File fileDelete = new File(path);
        db.csvFileLogDao().deleteCSVRecord(fileName);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                Log.i(TAG, "file Deleted :" + fileName);
            } else {
                Log.i(TAG, "file not Deleted :" + fileName);
            }
        }
    }
}
