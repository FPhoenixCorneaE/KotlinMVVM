package com.fphoenixcorneae.util

import android.content.res.Resources
import android.util.DisplayMetrics
import com.fphoenixcorneae.ext.loggerE
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.lang.reflect.Field
import java.util.*

/**
 * 适配屏幕工具类
 */
class AdaptScreenUtil private constructor() {
    companion object {
        private var sMetricsFields: MutableList<Field>? = null

        /**
         * Adapt for the horizontal screen, and call it in [android.app.Activity.getResources].
         */
        fun adaptWidth(
            resources: Resources,
            designWidth: Int
        ): Resources {
            val newXdpi =
                resources.displayMetrics.widthPixels * 72f / designWidth
            applyDisplayMetrics(resources, newXdpi)
            return resources
        }
        /**
         * Adapt for the vertical screen, and call it in [android.app.Activity.getResources].
         */
        /**
         * Adapt for the vertical screen, and call it in [android.app.Activity.getResources].
         */
        @JvmOverloads
        fun adaptHeight(
            resources: Resources,
            designHeight: Int,
            includeNavBar: Boolean = false
        ): Resources {
            val screenHeight = (resources.displayMetrics.heightPixels
                    + if (includeNavBar) getNavBarHeight(resources) else 0) * 72f
            val newXdpi = screenHeight / designHeight
            applyDisplayMetrics(resources, newXdpi)
            return resources
        }

        private fun getNavBarHeight(resources: Resources): Int {
            val resourceId =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId != 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }

        /**
         * @param resources The resources.
         * @return the resource
         */
        fun closeAdapt(resources: Resources): Resources {
            val newXdpi =
                Resources.getSystem().displayMetrics.density * 72f
            applyDisplayMetrics(resources, newXdpi)
            return resources
        }

        /**
         * Value of pt to value of px.
         *
         * @param ptValue The value of pt.
         * @return value of px
         */
        fun pt2Px(ptValue: Float): Int {
            val metrics =
                context.resources.displayMetrics
            return (ptValue * metrics.xdpi / 72f + 0.5).toInt()
        }

        /**
         * Value of px to value of pt.
         *
         * @param pxValue The value of px.
         * @return value of pt
         */
        fun px2Pt(pxValue: Float): Int {
            val metrics =
                context.resources.displayMetrics
            return (pxValue * 72 / metrics.xdpi + 0.5).toInt()
        }

        private fun applyDisplayMetrics(
            resources: Resources,
            newXdpi: Float
        ) {
            resources.displayMetrics.xdpi = newXdpi
            context.resources.displayMetrics.xdpi = newXdpi
            applyOtherDisplayMetrics(resources, newXdpi)
        }

        fun preLoad() {
            applyDisplayMetrics(
                Resources.getSystem(),
                Resources.getSystem().displayMetrics.xdpi
            )
        }

        private fun applyOtherDisplayMetrics(
            resources: Resources,
            newXdpi: Float
        ) {
            if (sMetricsFields == null) {
                sMetricsFields = ArrayList()
                var resCls: Class<*>? = resources.javaClass
                var declaredFields = resCls!!.declaredFields
                while (declaredFields.isNotEmpty()) {
                    for (field in declaredFields) {
                        if (field.type.isAssignableFrom(DisplayMetrics::class.java)) {
                            field.isAccessible = true
                            val tmpDm =
                                getMetricsFromField(resources, field)
                            if (tmpDm != null) {
                                sMetricsFields!!.add(field)
                                tmpDm.xdpi = newXdpi
                            }
                        }
                    }
                    resCls = resCls!!.superclass
                    declaredFields = resCls?.declaredFields ?: break
                }
            } else {
                applyMetricsFields(resources, newXdpi)
            }
        }

        private fun applyMetricsFields(
            resources: Resources,
            newXdpi: Float
        ) {
            for (metricsField in sMetricsFields!!) {
                try {
                    val dm = metricsField[resources] as DisplayMetrics
                    dm.xdpi = newXdpi
                } catch (e: Exception) {
                    loggerE("applyMetricsFields: $e")
                }
            }
        }

        private fun getMetricsFromField(
            resources: Resources,
            field: Field
        ): DisplayMetrics? {
            return try {
                when {
                    field[resources] is DisplayMetrics -> {
                        field[resources] as DisplayMetrics
                    }
                    else -> {
                        null
                    }
                }
            } catch (e: Exception) {
                loggerE("getMetricsFromField: $e")
                null
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}