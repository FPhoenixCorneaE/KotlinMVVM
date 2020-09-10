package com.fphoenixcorneae.ext.algorithm

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 算法类型：用于指定生成AES的密钥
 */
private const val KEY_ALGORITHM = "AES"

/**
 * 加密器类型:加密算法为AES,加密模式为CFB,补码方式为NoPadding
 */
private const val AES_CFB_NOPADDING = "AES/CFB/NoPadding"

/**
 * AES 加密
 */
fun ByteArray.aesEncrypt(
    key: ByteArray,
    iv: ByteArray,
    cipherAlgorithm: String = AES_CFB_NOPADDING
): ByteArray {
    val cipher = initCipher(Cipher.ENCRYPT_MODE, key, iv, cipherAlgorithm)
    return cipher.doFinal(this)
}

/**
 * AES 解密
 */
fun ByteArray.aesDecrypt(
    key: ByteArray,
    iv: ByteArray,
    cipherAlgorithm: String = AES_CFB_NOPADDING
): ByteArray {
    val cipher = initCipher(Cipher.DECRYPT_MODE, key, iv, cipherAlgorithm)
    return cipher.doFinal(this)
}

fun File.aesEncrypt(key: ByteArray, iv: ByteArray, destFilePath: String): File? {
    return handleFile(Cipher.ENCRYPT_MODE, key, iv, path, destFilePath)
}

fun File.aesDecrypt(key: ByteArray, iv: ByteArray, destFilePath: String): File? {
    return handleFile(Cipher.DECRYPT_MODE, key, iv, path, destFilePath)
}

fun initAESKey(size: Int = 256): ByteArray {
    val kg = KeyGenerator.getInstance(KEY_ALGORITHM)
    kg.init(size)
    return kg.generateKey().encoded
}

/**
 * 构造密钥
 */
private fun toKey(key: ByteArray): SecretKey = SecretKeySpec(key, KEY_ALGORITHM)

private fun initCipher(mode: Int, key: ByteArray, iv: ByteArray, cipherAlgorithm: String): Cipher {
    val k = toKey(key)
    val cipher = Cipher.getInstance(cipherAlgorithm)
    val cipherAlgorithm = cipherAlgorithm.toUpperCase(Locale.getDefault())
    if (cipherAlgorithm.contains("CFB") || cipherAlgorithm.contains("CBC")
        || cipherAlgorithm.contains("CTR")
    )
        cipher.init(mode, k, IvParameterSpec(iv))
    else
        cipher.init(mode, k)
    return cipher
}

private fun handleFile(
    mode: Int,
    key: ByteArray,
    iv: ByteArray,
    sourceFilePath: String,
    destFilePath: String
): File? {
    val sourceFile = File(sourceFilePath)
    val destFile = File(destFilePath)

    if (sourceFile.exists() && sourceFile.isFile) {
        if (destFile.parentFile?.exists() == false) {
            destFile.parentFile?.mkdirs()
        }
        destFile.createNewFile()

        val inputStream = FileInputStream(sourceFile)
        val outputStream = FileOutputStream(destFile)
        val cipher = initCipher(mode, key, iv, AES_CFB_NOPADDING)
        val cin = CipherInputStream(inputStream, cipher)

        try {
            val b = ByteArray(1024)
            var read: Int
            do {
                read = cin.read(b)
                if (read > 0)
                    outputStream.write(b, 0, read)
            } while (read > 0)

            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cin.close()
            inputStream.close()
            outputStream.close()
        }
        return destFile
    }
    return null
}

