package com.alumnus.zebra.ui.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alumnus.zebra.R
import com.alumnus.zebra.utils.EncryptManager
import kotlinx.android.synthetic.main.activity_decrypt.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class DecryptActivity : AppCompatActivity() {
    private val TAG = "DecryptActivity"

    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)

        uri = intent.data!! // Get File data from Intent
        val uriPath: File = File(uri.path!!)
        val fileName = uriPath.name // Get File name from uri
        Log.i(TAG, "InputFileName: $fileName")
    }

    fun decryptButtonClick(view: View) {
        val inputStream: InputStream = contentResolver.openInputStream(uri)!! // Convert received intent data into InputStream.
        Log.d(TAG, "inputStream: $inputStream")
        CoroutineScope(Dispatchers.IO).launch {
            EncryptManager.saveDecryptedFile(
                    context = this@DecryptActivity,
                    inputStream = inputStream,
                    outputFileName = et_decrypt_filename.text.toString(),
                    outputFileExtension = EncryptManager.FileType.zip)
        }
        finish()
    }
}