package com.alumnus.zebra.utils

import android.content.Context
import java.io.*

/**
 * EncryptManager saves encrypted and decrypted files in SD Card
 *
 * How to use
 * - Log.d(TAG, "Encrypted filename: ${EncryptManager.saveEncryptedFile(context = this, inputFilePath = "your_file_path")}")
 * - Log.d(TAG, "Decrypted filename: ${EncryptManager.saveDecryptedFile(context = this, encryptedFilePath = "your_file_path", outputFileExtension = ".zip")}")
 */
object EncryptManager {

    /**
     * Saves the encrypted file in the path
     * '/storage/self/primary/Android/obb/com.alumnus.zebra/your_file_name.enc'
     *
     * @param context                  Context needed to save file
     * @param inputFilePath            Path of input file
     * @param outputFileName           Name of the output encrypted file to be saved
     * @return filename
     */
    fun saveEncryptedFile(context: Context, inputFilePath: String, outputFileName: String = System.currentTimeMillis().toString()): String {
        try {
            //val inputStream: InputStream = FileInputStream("/storage/self/primary/ZebraApp/zipFiles/2021, Jul-22 Time-12 23 41.zip")
            val inputStream: InputStream = FileInputStream(inputFilePath)
            val file = File(context.obbDir!!.path)
            val outputFileEnc = File("${file.absolutePath}/$outputFileName.enc")
            FileEncryptorKT.encryptToFile(
                    keyStr = "keyLength16digit",
                    specStr = "keySizeMustBe16-",
                    inputStream,
                    FileOutputStream(outputFileEnc)
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputFileName
    }

    /**
     * Saves the Decrypted file in the path
     * '/storage/self/primary/Android/obb/com.alumnus.zebra/your_filename.csv'
     *
     * @param context                   Context needed to save file
     * @param encryptedFilePath         Path of input file
     * @param outputFileName            Name of the file to be saved
     * @param outputFileExtension       Output file extension like as(.csv, .zip, .pdf, .txt etc)
     * @return filename
     */
    fun saveDecryptedFile(context: Context, encryptedFilePath: String, outputFileName: String = System.currentTimeMillis().toString(), outputFileExtension: FileType): String {
        try {
            //val inputStream: InputStream = FileInputStream("/storage/self/primary/Android/obb/com.alumnus.zebra/1627455071182.enc")
            val inputStream: InputStream = FileInputStream(encryptedFilePath)
            val file = File(context.obbDir!!.path)
            val outputFileEnc = File("${file.absolutePath}/$outputFileName.${outputFileExtension.name}")
            FileEncryptorKT.decryptToFile(
                    keyStr = "keyLength16digit",
                    specStr = "keySizeMustBe16-",
                    inputStream,
                    FileOutputStream(outputFileEnc)
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputFileName
    }

    enum class FileType {
        zip, csv
    }
}