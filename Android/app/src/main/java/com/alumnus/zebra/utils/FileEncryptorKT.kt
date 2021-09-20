package com.alumnus.zebra.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * File Encryption & Decryption
 *
 * @author Arnab
 */
object FileEncryptorKT {
    const val READ_WRITE_BLOCK_BUFFER = 1024
    const val ALGO_IMAGE_ENCRYPTOR = "AES/CBC/PKCS5Padding"
    const val ALGO_SECRET_KEY = "AES"

    /**
     * Encrypt file
     *
     * @param keyStr            Key is a String of length 16            (i.e. 128 bit)
     * @param specStr           SpecStr is also a String of length 16.  (i.e. 128 bit)
     * @param inputStream       InputStream of the file to be encrypted (Input File)
     * @param outputStream      OutputStream of the encrypted file.     (Output File)
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IOException::class
    )
    fun encryptToFile(keyStr: String, specStr: String, inputStream: InputStream, outputStream: OutputStream) {
        var out = outputStream
        try {
            val iv = IvParameterSpec(specStr.toByteArray(charset("UTF-8")))
            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")), ALGO_SECRET_KEY)
            val c = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR)
            c.init(Cipher.ENCRYPT_MODE, keySpec, iv)
            out = CipherOutputStream(out, c)
            var count = 0
            val buffer = ByteArray(READ_WRITE_BLOCK_BUFFER)
            while (inputStream.read(buffer).also { count = it } > 0) {
                out.write(buffer, 0, count)
            }
        } finally {
            out.close()
        }
    }


    /**
     * Decrypt file
     *
     * @param keyStr            Key is a String of length 16            (i.e. 128 bit)
     * @param specStr           SpecStr is also a String of length 16.  (i.e. 128 bit)
     * @param inputStream       InputStream of the file to be decrypted (Input File)
     * @param outputStream      OutputStream of the decrypted file.     (Output File)
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IOException::class
    )
    fun decryptToFile(keyStr: String, specStr: String, inputStream: InputStream, outputStream: OutputStream) {
        var out = outputStream
        try {
            val iv = IvParameterSpec(specStr.toByteArray(charset("UTF-8")))
            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")), ALGO_SECRET_KEY)
            val c = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR)
            c.init(Cipher.DECRYPT_MODE, keySpec, iv)
            out = CipherOutputStream(out, c)
            var count = 0
            val buffer = ByteArray(READ_WRITE_BLOCK_BUFFER)
            while (inputStream.read(buffer).also { count = it } > 0) {
                out.write(buffer, 0, count)
            }
        } finally {
            out.close()
        }
    }
}