package com.fphoenixcorneae.util.encryption

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES对称加密:
 * Advanced Encryption Standard，高级数据加密标准，AES算法可以有效抵制针对DES的攻击算法，对称加密算法
 */
object AesCryptUtil {
    private const val AES_MODE = "AES/CBC/PKCS7Padding"
    private const val CHARSET = "UTF-8"
    private const val CIPHER = "AES"
    private const val HASH_ALGORITHM = "SHA-256"
    private val IV_BYTES = byteArrayOf(
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00
    )

    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    private fun generateKey(password: String): SecretKeySpec? {
        return try {
            val digest =
                MessageDigest.getInstance(HASH_ALGORITHM)
            val bytes = password.toByteArray(charset(CHARSET))
            digest.update(bytes, 0, bytes.size)
            val key = digest.digest()
            SecretKeySpec(key, CIPHER)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Encrypt and encode message using 256-bit AES with key generated from password.
     *
     * @param password used to generated key
     * @param message  the thing you want to encrypt assumed String UTF-8
     * @return Base64 encoded CipherText
     */
    @JvmStatic
    fun encrypt(password: String, message: String): String? {
        return try {
            val key = generateKey(password)
            val cipherText = encrypt(
                key,
                IV_BYTES,
                message.toByteArray(charset(CHARSET))
            )
            //NO_WRAP is important as was getting \n at the end
            Base64.encodeToString(cipherText, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     */
    @JvmStatic
    fun encrypt(
        key: SecretKeySpec?,
        iv: ByteArray?,
        message: ByteArray?
    ): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            cipher.doFinal(message)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password                used to generated key
     * @param base64EncodedCipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     */
    @JvmStatic
    fun decrypt(password: String, base64EncodedCipherText: String?): String? {
        return try {
            val key = generateKey(password)
            val decodedCipherText =
                Base64.decode(base64EncodedCipherText, Base64.NO_WRAP)
            val decryptedBytes =
                decrypt(
                    key,
                    IV_BYTES,
                    decodedCipherText
                )
            String(decryptedBytes!!, charset(CHARSET))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     */
    @JvmStatic
    fun decrypt(
        key: SecretKeySpec?,
        iv: ByteArray?,
        decodedCipherText: ByteArray?
    ): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            cipher.doFinal(decodedCipherText)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}