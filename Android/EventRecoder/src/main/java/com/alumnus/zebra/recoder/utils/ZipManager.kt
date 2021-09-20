package com.alumnus.zebra.recoder.utils

import android.content.Context
import android.util.Log
import com.alumnus.zebra.recoder.utils.FolderFiles.createFolder
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Zip & UnZip files
 *
 * @author Arnab Kundu
 */
class ZipManager {
    var BUFFER = 100000 // TODO variable according to buffer size

    /**
     * Zip files
     * ** How to use **
     *
     *
     * String[] s = new String[2]; // Array of files
     * s[0] = inputPath + "/image.jpg";
     * s[1] = inputPath + "/textfile.txt"; // /sdcard/ZipDemo/textfile.txt
     *
     * ZipManager zipManager = new ZipManager();
     * zipManager.zip(s, inputPath + inputFile); // first parameter is d files second parameter is zip file name & path
     *
     *
     * @param _files      Array of files to be zipped
     * @param zipFileName Provide a name for the zip file which will be generated
     */
    fun zip(context: Context, _files: Array<String>, zipFileName: String = DateFormatter.getTimeStampFileName(System.currentTimeMillis())): String {
        val pathOfZebraFolder: String = createFolder(context, "zipFiles")
        try {
            var origin: BufferedInputStream? = null
            val dest = FileOutputStream("$pathOfZebraFolder/$zipFileName.zip")
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)
            for (i in _files.indices) {
                Log.v("Compress", "Adding: " + _files[i])
                val fi = FileInputStream(_files[i])
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1))
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
            out.close()
            return "$pathOfZebraFolder/$zipFileName.zip"
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * ZipManager zipManager = new ZipManager();
     * zipManager.unzip(inputPath + inputFile, outputPath);
     *
     * @param _zipFile        Fully qualified filename. i.e path + filename
     * @param _targetLocation Folder location where files will be saved after extraction
     * TODO Method not utilized yet. Need to implement later based on unzipping requirement
     */
    fun unzip(_zipFile: String?, _targetLocation: String) {

        //create target location folder if not exist
        //TODO dirChecker(_targetLocatioan);
        try {
            val fin = FileInputStream(_zipFile)
            val zin = ZipInputStream(fin)
            var ze: ZipEntry? = null
            while (zin.nextEntry.also { ze = it } != null) {

                //create dir if required while unzipping
                if (ze!!.isDirectory) {
                    //TODO dirChecker(ze.getName());
                } else {
                    val fout = FileOutputStream(_targetLocation + ze!!.name)
                    var c = zin.read()
                    while (c != -1) {
                        fout.write(c)
                        c = zin.read()
                    }
                    zin.closeEntry()
                    fout.close()
                }
            }
            zin.close()
        } catch (e: Exception) {
            println(e)
        }
    }
}