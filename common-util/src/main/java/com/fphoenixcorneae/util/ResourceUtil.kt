package com.fphoenixcorneae.util

import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @desc: 资源工具类
 */
class ResourceUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    /**
     * Get raw text file, ui/raw/text
     */
    fun getRawText(@RawRes resId: Int): String? {
        try {
            val inputReader = InputStreamReader(getRaw(resId))
            val bufReader = BufferedReader(inputReader)
            var line: String? = null
            val result = StringBuilder()
            while ({ line = bufReader.readLine(); line }() != null) {
                result.append(line)
            }
            return result.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {

        fun getLayoutId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "layout",
                ContextUtil.context.packageName
            )
        }

        fun getStringId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "string",
                ContextUtil.context.packageName
            )
        }

        fun getDrawableId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "drawable",
                ContextUtil.context.packageName
            )
        }

        fun getMipmapId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "mipmap",
                ContextUtil.context.packageName
            )
        }

        fun getStyleId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "style",
                ContextUtil.context.packageName
            )
        }

        fun getId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "id",
                ContextUtil.context.packageName
            )
        }

        fun getColorId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "color",
                ContextUtil.context.packageName
            )
        }

        fun getArrayId(name: String): Int {
            return ContextUtil.context.resources.getIdentifier(
                name, "array",
                ContextUtil.context.packageName
            )
        }

        /**
         * Get raw file, ui/raw/file
         */
        fun getRaw(@RawRes resId: Int): InputStream {
            return ContextUtil.context.resources.openRawResource(resId)
        }

        /**
         * Get raw file descriptor, ui/raw/file. This function only works for resources that are stored in the package as
         * uncompressed data, which typically includes things like mp3 files and png images.
         */
        fun getRawFd(@RawRes resId: Int): AssetFileDescriptor {
            return ContextUtil.context.resources.openRawResourceFd(resId)
        }

        /**
         * Get xml file, ui/xml/file
         */
        fun getXml(@XmlRes resId: Int): XmlResourceParser {
            return ContextUtil.context.resources.getXml(resId)
        }

        /**
         * Get drawable, ui/drawable/file
         */
        fun getDrawable(@DrawableRes resId: Int): Drawable? {
            return ContextCompat.getDrawable(ContextUtil.context, resId)
        }

        /**
         * Get string, ui/values/__picker_strings.xml
         */
        fun getString(@StringRes resId: Int): String {
            return ContextUtil.context.resources.getString(resId)
        }

        /**
         * Get string array, ui/values/__picker_strings.xml
         */
        fun getStringArray(@ArrayRes resId: Int): Array<String> {
            return ContextUtil.context.resources.getStringArray(resId)
        }

        /**
         * Get int array, ui/values/__picker_strings.xml
         */
        fun getIntArray(@ArrayRes resId: Int): IntArray {
            return ContextUtil.context.resources.getIntArray(resId)
        }

        /**
         * Get color, ui/values/__picker_colors.xml
         */
        fun getColor(@ColorRes resId: Int): Int {
            return ContextCompat.getColor(ContextUtil.context, resId)
        }

        /**
         * Get font
         */
        fun getFont(@FontRes resId: Int): Typeface? {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    ContextUtil.context.resources.getFont(resId)
                }
                else -> {
                    ResourcesCompat.getFont(ContextUtil.context, resId)
                }
            }
        }

        /**
         * Get color state list, ui/values/__picker_colors.xml
         */
        fun getColorStateList(@ColorRes resId: Int): ColorStateList? {
            return ContextCompat.getColorStateList(ContextUtil.context, resId)
        }

        /**
         * Get dimension, ui/values/dimens.xml
         *
         * @return View dimension value multiplied by the appropriate metric.
         * 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float
         */
        fun getDimension(@DimenRes resId: Int): Float {
            return ContextUtil.context.resources.getDimension(resId)
        }

        /**
         * Get dimension, ui/values/dimens.xml
         *
         * @return View dimension value multiplied by the appropriate metric and truncated to integer pixels.
         * 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int
         */
        fun getDimensionPixelOffset(@DimenRes resId: Int): Int {
            return ContextUtil.context.resources.getDimensionPixelOffset(resId)
        }

        /**
         * Get dimension, ui/values/dimens.xml
         *
         * @return View dimension value multiplied by the appropriate metric and truncated to integer pixels.
         * 不管写的是dp还是sp还是px,都会乘以density.
         */
        fun getDimensionPixelSize(@DimenRes resId: Int): Int {
            return ContextUtil.context.resources.getDimensionPixelSize(resId)
        }
    }
}