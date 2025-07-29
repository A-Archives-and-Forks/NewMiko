package im.mingxi.miko.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AESUtils {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val IV_SIZE = 16 // 128-bit IV
    private const val KEY_SIZE = 256 // 256-bit key

    /**
     * 生成随机密钥并返回Base64字符串
     */
    fun generateKeyString(): String {
        val key = ByteArray(KEY_SIZE / 8) // 256-bit = 32字节
        SecureRandom().nextBytes(key)
        return Base64.encodeToString(key, Base64.DEFAULT)
    }

    /**
     * 从Base64字符串创建密钥
     */
    private fun getKeyFromString(keyString: String): SecretKey {
        val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    /**
     * 加密
     * @param plainText 明文
     * @param keyString Base64编码的密钥字符串
     * @return Base64编码的加密结果（包含IV）
     */
    fun encrypt(plainText: String, keyString: String): String {
        val key = getKeyFromString(keyString)
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv) // 生成随机IV
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        // 组合 IV + 加密数据
        val combined = iv + encrypted

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    /**
     * 解密
     * @param encryptedText Base64编码的加密文本（包含IV）
     * @param keyString Base64编码的密钥字符串
     * @return 解密后的原始字符串
     */
    fun decrypt(encryptedText: String, keyString: String): String {
        val key = getKeyFromString(keyString)
        val combined = Base64.decode(encryptedText, Base64.DEFAULT)
        // 分离 IV 和加密数据
        val iv = combined.copyOfRange(0, IV_SIZE)
        val encryptedData = combined.copyOfRange(IV_SIZE, combined.size)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decrypted = cipher.doFinal(encryptedData)
        return String(decrypted, Charsets.UTF_8)
    }
}