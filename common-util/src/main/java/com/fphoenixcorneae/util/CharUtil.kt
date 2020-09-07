package com.fphoenixcorneae.util

/**
 * 字符工具类
 */
object CharUtil {
    const val SPACE = ' '
    const val TAB = '	'
    const val DOT = '.'
    const val SLASH = '/'
    const val BACKSLASH = '\\'
    const val CR = '\r'
    const val LF = '\n'
    const val UNDERLINE = '_'
    const val DASHED = '-'
    const val COMMA = ','
    const val DELIM_START = '{'
    const val DELIM_END = '}'
    const val BRACKET_START = '['
    const val BRACKET_END = ']'
    const val COLON = ':'
    const val DOUBLE_QUOTES = '"'
    const val SINGLE_QUOTE = '\''
    const val AMP = '&'
    /**
     * 是否为ASCII字符，ASCII字符位于0~127之间
     *
     * <pre>
     * CharUtil.isAscii('a')  = true
     * CharUtil.isAscii('A')  = true
     * CharUtil.isAscii('3')  = true
     * CharUtil.isAscii('-')  = true
     * CharUtil.isAscii('\n') = true
     * CharUtil.isAscii('') = false
    </pre> *
     *
     * @param ch 被检查的字符处
     * @return true表示为ASCII字符，ASCII字符位于0~127之间
     */
    fun isAscii(ch: Char): Boolean {
        return ch.toInt() < 128
    }

    /**
     * 是否为可见ASCII字符，可见字符位于32~126之间
     *
     * <pre>
     * CharUtil.isAsciiPrintable('a')  = true
     * CharUtil.isAsciiPrintable('A')  = true
     * CharUtil.isAsciiPrintable('3')  = true
     * CharUtil.isAsciiPrintable('-')  = true
     * CharUtil.isAsciiPrintable('\n') = false
     * CharUtil.isAsciiPrintable('') = false
    </pre> *
     *
     * @param ch 被检查的字符处
     * @return true表示为ASCII可见字符，可见字符位于32~126之间
     */
    fun isAsciiPrintable(ch: Char): Boolean {
        return ch.toInt() in 32..126
    }

    /**
     * 是否为ASCII控制符（不可见字符），控制符位于0~31和127
     *
     * <pre>
     * CharUtil.isAsciiControl('a')  = false
     * CharUtil.isAsciiControl('A')  = false
     * CharUtil.isAsciiControl('3')  = false
     * CharUtil.isAsciiControl('-')  = false
     * CharUtil.isAsciiControl('\n') = true
     * CharUtil.isAsciiControl('') = false
    </pre> *
     *
     * @param ch 被检查的字符
     * @return true表示为控制符，控制符位于0~31和127
     */
    fun isAsciiControl(ch: Char): Boolean {
        return ch.toInt() < 32 || ch.toInt() == 127
    }

    /**
     * 判断是否为字母（包括大写字母和小写字母）<br></br>
     * 字母包括A~Z和a~z
     *
     * <pre>
     * CharUtil.isLetter('a')  = true
     * CharUtil.isLetter('A')  = true
     * CharUtil.isLetter('3')  = false
     * CharUtil.isLetter('-')  = false
     * CharUtil.isLetter('\n') = false
     * CharUtil.isLetter('') = false
    </pre> *
     *
     * @param ch 被检查的字符
     * @return true表示为字母（包括大写字母和小写字母）字母包括A~Z和a~z
     */
    fun isLetter(ch: Char): Boolean {
        return isLetterUpper(ch) || isLetterLower(ch)
    }

    /**
     *
     *
     * 判断是否为大写字母，大写字母包括A~Z
     *
     *
     * <pre>
     * CharUtil.isLetterUpper('a')  = false
     * CharUtil.isLetterUpper('A')  = true
     * CharUtil.isLetterUpper('3')  = false
     * CharUtil.isLetterUpper('-')  = false
     * CharUtil.isLetterUpper('\n') = false
     * CharUtil.isLetterUpper('') = false
    </pre> *
     *
     * @param ch 被检查的字符
     * @return true表示为大写字母，大写字母包括A~Z
     */
    fun isLetterUpper(ch: Char): Boolean {
        return ch in 'A'..'Z'
    }

    /**
     *
     *
     * 检查字符是否为小写字母，小写字母指a~z
     *
     *
     * <pre>
     * CharUtil.isLetterLower('a')  = true
     * CharUtil.isLetterLower('A')  = false
     * CharUtil.isLetterLower('3')  = false
     * CharUtil.isLetterLower('-')  = false
     * CharUtil.isLetterLower('\n') = false
     * CharUtil.isLetterLower('') = false
    </pre> *
     *
     * @param ch 被检查的字符
     * @return true表示为小写字母，小写字母指a~z
     */
    fun isLetterLower(ch: Char): Boolean {
        return ch in 'a'..'z'
    }

    /**
     *
     *
     * 检查是否为数字字符，数字字符指0~9
     *
     *
     * <pre>
     * CharUtil.isNumber('a')  = false
     * CharUtil.isNumber('A')  = false
     * CharUtil.isNumber('3')  = true
     * CharUtil.isNumber('-')  = false
     * CharUtil.isNumber('\n') = false
     * CharUtil.isNumber('') = false
    </pre> *
     *
     * @param ch 被检查的字符
     * @return true表示为数字字符，数字字符指0~9
     */
    fun isNumber(ch: Char): Boolean {
        return ch in '0'..'9'
    }

    /**
     * 是否为16进制规范的字符，判断是否为如下字符
     * <pre>
     * 1. 0~9
     * 2. a~f
     * 4. A~F
    </pre> *
     *
     * @param c 字符
     * @return 是否为16进制规范的字符
     * @since 4.1.5
     */
    fun isHexChar(c: Char): Boolean {
        return isNumber(c) || c in 'a'..'f' || c in 'A'..'F'
    }

    /**
     * 是否为字符或数字，包括A~Z、a~z、0~9
     *
     * CharUtil.isLetterOrNumber('a')  = true
     * CharUtil.isLetterOrNumber('A')  = true
     * CharUtil.isLetterOrNumber('3')  = true
     * CharUtil.isLetterOrNumber('-')  = false
     * CharUtil.isLetterOrNumber('\n') = false
     * CharUtil.isLetterOrNumber('') = false
     *
     * @param ch 被检查的字符
     * @return true 表示为字符或数字，包括A~Z、a~z、0~9
     */
    fun isLetterOrNumber(ch: Char): Boolean {
        return isLetter(ch) || isNumber(ch)
    }

    /**
     * 字符转为字符串<br></br>
     * 如果为ASCII字符，使用缓存
     *
     * @param c 字符
     * @return 字符串
     */
    fun toString(c: Char): String {
        return c.toString()
    }

    /**
     * 给定类名是否为字符类，字符类包括：
     *
     * <pre>
     * Character.class
     * char.class
    </pre> *
     *
     * @param clazz 被检查的类
     * @return true表示为字符类
     */
    fun isCharClass(clazz: Class<*>): Boolean {
        return clazz == Char::class.java || clazz == Char::class.javaPrimitiveType
    }

    /**
     * 给定对象对应的类是否为字符类，字符类包括：
     *
     * <pre>
     * Character.class
     * char.class
    </pre> *
     *
     * @param value 被检查的对象
     * @return true 表示为字符类
     */
    fun isChar(value: Any): Boolean {
        return value is Char || value.javaClass == Char::class.javaPrimitiveType
    }

    /**
     * 是否空白符<br></br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br></br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character.isWhitespace
     * @see Character.isSpaceChar
     */
    fun isBlankChar(c: Char): Boolean {
        return isBlankChar(c.toInt())
    }

    /**
     * 是否空白符<br></br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br></br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character.isWhitespace
     * @see Character.isSpaceChar
     */
    fun isBlankChar(c: Int): Boolean {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff'.toInt() || c == '\u202a'.toInt()
    }

    /**
     * 判断是否为emoji表情符<br></br>
     *
     * @param c 字符
     * @return 是否为emoji
     */
    fun isEmoji(c: Char): Boolean {
        return !(c.toInt() == 0x0
                || c.toInt() == 0x9
                || c.toInt() == 0xA
                || c.toInt() == 0xD
                || c.toInt() in 0x20..0xD7FF
                || c.toInt() in 0xE000..0xFFFD
                || c.toInt() in 0x10000..0x10FFFF)
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br></br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    fun isFileSeparator(c: Char): Boolean {
        return SLASH == c || BACKSLASH == c
    }

    /**
     * 比较两个字符是否相同
     *
     * @param c1         字符1
     * @param c2         字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     */
    fun equals(c1: Char, c2: Char, ignoreCase: Boolean): Boolean {
        return if (ignoreCase) {
            Character.toLowerCase(c1) == Character.toLowerCase(c2)
        } else c1 == c2
    }
}