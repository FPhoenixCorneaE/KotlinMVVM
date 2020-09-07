package com.fphoenixcorneae.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.palette.graphics.Palette

/**
 * 颜色工具类
 */
class ColorUtil private constructor() {
    companion object {
        /**
         * Returns a color associated with a particular resource ID.
         *
         * @param id The desired resource identifier.
         * @return a color associated with a particular resource ID
         */
        fun getColor(@ColorRes id: Int): Int {
            return ResourceUtil.getColor(id)
        }

        /**
         * Set the alpha component of `color` to be `alpha`.
         *
         * @param color The color.
         * @param alpha Alpha component \([0..255]\) of the color.
         * @return the `color` with `alpha` component
         */
        fun setAlphaComponent(
            @ColorInt color: Int,
            @IntRange(from = 0x00, to = 0xFF) alpha: Int
        ): Int {
            return color and 0x00ffffff or (alpha shl 24)
        }

        /**
         * Set the alpha component of `color` to be `alpha`.
         *
         * @param color The color.
         * @param alpha Alpha component \([0..1]\) of the color.
         * @return the `color` with `alpha` component
         */
        fun setAlphaComponent(
            @ColorInt color: Int,
            @FloatRange(from = 0.0, to = 1.0) alpha: Float
        ): Int {
            return color and 0x00ffffff or ((alpha * 255.0f + 0.5f).toInt() shl 24)
        }

        /**
         * Set the red component of `color` to be `red`.
         *
         * @param color The color.
         * @param red   Red component \([0..255]\) of the color.
         * @return the `color` with `red` component
         */
        fun setRedComponent(
            @ColorInt color: Int,
            @IntRange(from = 0x00, to = 0xFF) red: Int
        ): Int {
            return color and -0xff0001 or (red shl 16)
        }

        /**
         * Set the red component of `color` to be `red`.
         *
         * @param color The color.
         * @param red   Red component \([0..1]\) of the color.
         * @return the `color` with `red` component
         */
        fun setRedComponent(
            @ColorInt color: Int,
            @FloatRange(from = 0.0, to = 1.0) red: Float
        ): Int {
            return color and -0xff0001 or ((red * 255.0f + 0.5f).toInt() shl 16)
        }

        /**
         * Set the green component of `color` to be `green`.
         *
         * @param color The color.
         * @param green Green component \([0..255]\) of the color.
         * @return the `color` with `green` component
         */
        fun setGreenComponent(
            @ColorInt color: Int,
            @IntRange(from = 0x00, to = 0xFF) green: Int
        ): Int {
            return color and -0xff01 or (green shl 8)
        }

        /**
         * Set the green component of `color` to be `green`.
         *
         * @param color The color.
         * @param green Green component \([0..1]\) of the color.
         * @return the `color` with `green` component
         */
        fun setGreenComponent(
            @ColorInt color: Int,
            @FloatRange(from = 0.0, to = 1.0) green: Float
        ): Int {
            return color and -0xff01 or ((green * 255.0f + 0.5f).toInt() shl 8)
        }

        /**
         * Set the blue component of `color` to be `blue`.
         *
         * @param color The color.
         * @param blue  Blue component \([0..255]\) of the color.
         * @return the `color` with `blue` component
         */
        fun setBlueComponent(
            @ColorInt color: Int,
            @IntRange(from = 0x00, to = 0xFF) blue: Int
        ): Int {
            return color and -0x100 or blue
        }

        /**
         * Set the blue component of `color` to be `blue`.
         *
         * @param color The color.
         * @param blue  Blue component \([0..1]\) of the color.
         * @return the `color` with `blue` component
         */
        fun setBlueComponent(
            @ColorInt color: Int,
            @FloatRange(from = 0.0, to = 1.0) blue: Float
        ): Int {
            return color and -0x100 or (blue * 255.0f + 0.5f).toInt()
        }

        /**
         * Color-string to color-int.
         *
         * Supported formats are:
         *
         *
         *  * `#RRGGBB`
         *  * `#AARRGGBB`
         *
         *
         *
         * The following names are also accepted: `red`, `blue`,
         * `green`, `black`, `white`, `gray`,
         * `cyan`, `magenta`, `yellow`, `lightgray`,
         * `darkgray`, `grey`, `lightgrey`, `darkgrey`,
         * `aqua`, `fuchsia`, `lime`, `maroon`,
         * `navy`, `olive`, `purple`, `silver`,
         * and `teal`.
         *
         * @param colorString The color-string.
         * @return color-int
         * @throws IllegalArgumentException The string cannot be parsed.
         */
        fun string2Int(colorString: String): Int {
            return Color.parseColor(colorString)
        }

        /**
         * Color-int to color-string.
         *
         * @param colorInt The color-int.
         * @return color-string
         */
        fun int2RgbString(@ColorInt colorInt: Int): String {
            var colorInt = colorInt
            colorInt = colorInt and 0x00ffffff
            var color = Integer.toHexString(colorInt)
            while (color.length < 6) {
                color = "0$color"
            }
            return "#$color"
        }

        /**
         * Color-int to color-string.
         *
         * @param colorInt The color-int.
         * @return color-string
         */
        fun int2ArgbString(@ColorInt colorInt: Int): String {
            var color = Integer.toHexString(colorInt)
            while (color.length < 6) {
                color = "0$color"
            }
            while (color.length < 8) {
                color = "f$color"
            }
            return "#$color"
        }

        /**
         * 根据图片提取颜色
         */
        fun getColorFromBitmap(bitmap: Bitmap?): Int {
            return when {
                bitmap != null -> {
                    val p: Palette = Palette.from(bitmap).generate()
                    val darkMuted = p.darkMutedSwatch
                    val darkVibrant = p.darkVibrantSwatch
                    darkMuted?.rgb ?: darkVibrant?.rgb ?: Color.TRANSPARENT
                }
                else -> {
                    Color.TRANSPARENT
                }
            }
        }

        /**
         * Return the random color.
         *
         * @return the random color
         */
        val randomColor: Int
            get() = getRandomColor(true)

        /**
         * Return the random color.
         *
         * @param supportAlpha True to support alpha, false otherwise.
         * @return the random color
         */
        fun getRandomColor(supportAlpha: Boolean): Int {
            val high =
                when {
                    supportAlpha -> (Math.random() * 0x100).toInt() shl 24
                    else -> -0x1000000
                }
            return high or (Math.random() * 0x1000000).toInt()
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}