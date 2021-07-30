package com.alumnus.zebra.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alumnus.zebra.R
import com.alumnus.zebra.utils.EncryptManager
import java.io.File

class DecryptActivity : AppCompatActivity() {
    private val TAG = "DecryptActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)


        val uri = intent.data // Get File data from Intent
        val uriPath = File(uri!!.path!!)
        val fileName = uriPath.name // Get File name from uri
        Log.i(TAG, "FileName: $fileName")

        val actualFilePath = "/storage/self/primary/${uriPath.toString().split(":")[1]}"
        Log.i(TAG, "FilePath: $actualFilePath")
        // try {
        // val inputStream = contentResolver.openInputStream(uri) // Convert received intent data into InputStream.

        EncryptManager.saveDecryptedFile(this, actualFilePath, "myDecryptedFile", EncryptManager.FileType.zip)
        Toast.makeText(this, "File decrypted at:\n $actualFilePath", Toast.LENGTH_LONG).show()
        finish()
    }
}