package com.fphoenixcorneae.util

import android.Manifest.permission
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission
import com.fphoenixcorneae.util.ContextUtil.Companion.context

/**
 * 震动工具类
 * 注意:
 * 1、震动为一直震动的话，如果不取消震动，就算退出，也会一直震动;
 */
class VibrateUtil private constructor() {
    companion object {
        /**
         * Vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         *
         * @param milliseconds The number of milliseconds to vibrate.
         */
        @RequiresPermission(permission.VIBRATE)
        fun vibrate(milliseconds: Long) {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            when {
                vibrator.hasVibrator() -> { // 判断手机硬件是否有振动器
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    milliseconds,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }
                        else -> {
                            vibrator.vibrate(milliseconds)
                        }
                    }
                }
            }
        }

        /**
         * Vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         *
         * @param pattern An array of longs of times for which to turn the vibrator on or off.
         * @param repeat  The index into pattern at which to repeat, or -1 if you don't want to repeat.
         */
        @RequiresPermission(permission.VIBRATE)
        fun vibrate(pattern: LongArray?, repeat: Int) {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            when {
                vibrator.hasVibrator() -> { // 判断手机硬件是否有振动器
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
                        }
                        else -> {
                            vibrator.vibrate(pattern, repeat)
                        }
                    }
                }
            }
        }

        /**
         * Cancel vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         */
        @RequiresPermission(permission.VIBRATE)
        fun cancel() {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            when {
                vibrator.hasVibrator() -> { // 判断手机硬件是否有振动器
                    vibrator.cancel()
                }
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}