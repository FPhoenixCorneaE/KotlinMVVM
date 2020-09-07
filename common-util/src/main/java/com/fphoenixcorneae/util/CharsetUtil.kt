package com.fphoenixcorneae.util

import com.fphoenixcorneae.util.FileUtil.Companion.readFile2String
import com.fphoenixcorneae.util.FileUtil.Companion.writeFileFromString
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.UnsupportedCharsetException

/**
 * 字符集工具类
 */
object CharsetUtil {
    /**
     * ISO-8859-1
     */
    const val ISO_8859_1 = "ISO-8859-1"
    /**
     * UTF-8
     */
    const val UTF_8 = "UTF-8"
    /**
     * GBK
     */
    const val GBK = "GBK"
    /**
     * ISO-8859-1
     */
    val CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1
    /**
     * UTF-8
     */
    val CHARSET_UTF_8 = StandardCharsets.UTF_8
    /**
     * GBK
     */
    val CHARSET_GBK = Charset.forName(GBK)

    /**
     * 转换为Charset对象
     *
     * @param charsetName 字符集，为空则返回默认字符集
     * @return Charset
     * @throws UnsupportedCharsetException 编码不支持
     */
    @Throws(UnsupportedCharsetException::class)
    fun charset(charsetName: String): Charset {
        return if (charsetName.isBlank()) Charset.defaultCharset() else Charset.forName(
            charsetName
        )
    }

    /**
     * 转换字符串的字符集编码
     *
     * @param source      字符串
     * @param srcCharset  源字符集，默认ISO-8859-1
     * @param destCharset 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    fun convert(
        source: String,
        srcCharset: String?,
        destCharset: String?
    ): String {
        return convert(
            source,
            Charset.forName(srcCharset),
            Charset.forName(destCharset)
        )
    }

    /**
     * 转换字符串的字符集编码<br></br>
     * 当以错误的编码读取为字符串时，打印字符串将出现乱码。<br></br>
     * 此方法用于纠正因读取使用编码错误导致的乱码问题。<br></br>
     * 例如，在Servlet请求中客户端用GBK编码了请求参数，我们使用UTF-8读取到的是乱码，此时，使用此方法即可还原原编码的内容
     * <pre>
     * 客户端 -》 GBK编码 -》 Servlet容器 -》 UTF-8解码 -》 乱码
     * 乱码 -》 UTF-8编码 -》 GBK解码 -》 正确内容
    </pre> *
     *
     * @param source      字符串
     * @param srcCharset  源字符集，默认ISO-8859-1
     * @param destCharset 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    fun convert(
        source: String,
        srcCharset: Charset?,
        destCharset: Charset?
    ): String {
        var srcCharset = srcCharset
        var destCharset = destCharset
        if (null == srcCharset) {
            srcCharset = StandardCharsets.ISO_8859_1
        }
        if (null == destCharset) {
            destCharset = StandardCharsets.UTF_8
        }
        return if (source.isBlank() || srcCharset == destCharset) {
            source
        } else String(source.toByteArray(srcCharset!!), destCharset!!)
    }

    /**
     * 转换文件编码<br></br>
     * 此方法用于转换文件编码，读取的文件实际编码必须与指定的srcCharset编码一致，否则导致乱码
     *
     * @param file        文件
     * @param srcCharset  原文件的编码，必须与文件内容的编码保持一致
     * @param destCharset 转码后的编码
     * @return 被转换编码的文件
     * @since 3.1.0
     */
    fun convert(
        file: File?,
        srcCharset: Charset,
        destCharset: Charset
    ): File? {
        val str = readFile2String(file, srcCharset.name())
        writeFileFromString(file, str, false, destCharset.name())
        return file
    }

    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 [CharsetUtil.defaultCharsetName]
     *
     * @return 系统字符集编码
     * @see CharsetUtil.defaultCharsetName
     * @since 3.1.2
     */
    fun systemCharsetName(): String {
        return systemCharset().name()
    }

    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 [CharsetUtil.defaultCharsetName]
     *
     * @return 系统字符集编码
     * @see CharsetUtil.defaultCharsetName
     * @since 3.1.2
     */
    fun systemCharset(): Charset {
        return if (isWindows) CHARSET_GBK else defaultCharset()
    }

    private val isWindows: Boolean
        private get() = CharUtil.BACKSLASH == File.separatorChar

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    fun defaultCharsetName(): String {
        return defaultCharset().name()
    }

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    @JvmStatic
    fun defaultCharset(): Charset {
        return Charset.defaultCharset()
    }
}