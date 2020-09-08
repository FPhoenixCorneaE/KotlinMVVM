package com.fphoenixcorneae.util

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import com.fphoenixcorneae.ext.loggerE
import com.fphoenixcorneae.util.AppUtil.Companion.packageName
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import com.fphoenixcorneae.util.SharedPreferencesUtil.Companion.getString
import com.fphoenixcorneae.util.SharedPreferencesUtil.Companion.put
import java.util.*

/**
 * 语言工具类
 */
class LanguageUtil private constructor() {
    companion object {
        private const val KEY_LOCALE = "KEY_LOCALE"
        private const val VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM"
        /**
         * Apply the system language in the [Application.onCreate].
         */
        fun applySystemLanguageInAppOnCreate() {
            if (isAppliedSystemLanguage) {
                return
            }
            applyLanguage(
                Resources.getSystem().configuration.locale,
                "",
                true,
                false
            )
        }

        /**
         * Apply the language in the [Application.onCreate].
         *
         * @param locale The language of locale.
         */
        fun applyLanguageInAppOnCreate(locale: Locale) {
            if (isAppliedLanguage) {
                return
            }
            applyLanguage(locale, "", false, false)
        }

        /**
         * Apply the system language.
         *
         * @param activityClz The class of activity will be started after apply system language.
         */
        fun applySystemLanguage(activityClz: Class<out Activity?>?) {
            applyLanguage(
                Resources.getSystem().configuration.locale,
                activityClz,
                true,
                true
            )
        }

        /**
         * Apply the system language.
         *
         * @param activityClassName The full class name of activity will be started after apply system language.
         */
        fun applySystemLanguage(activityClassName: String) {
            applyLanguage(
                Resources.getSystem().configuration.locale,
                activityClassName,
                true,
                true
            )
        }

        /**
         * Apply the language.
         *
         * @param locale      The language of locale.
         * @param activityClz The class of activity will be started after apply system language.
         * It will start the launcher activity if the class is null.
         */
        fun applyLanguage(
            locale: Locale,
            activityClz: Class<out Activity?>?
        ) {
            applyLanguage(locale, activityClz, false, true)
        }

        /**
         * Apply the language.
         *
         * @param locale            The language of locale.
         * @param activityClassName The class of activity will be started after apply system language.
         * It will start the launcher activity if the class name is null.
         */
        fun applyLanguage(
            locale: Locale,
            activityClassName: String
        ) {
            applyLanguage(locale, activityClassName, false, true)
        }

        private fun applyLanguage(
            locale: Locale,
            activityClz: Class<out Activity?>?,
            isFollowSystem: Boolean,
            isNeedStartActivity: Boolean
        ) {
            if (activityClz == null) {
                applyLanguage(
                    locale,
                    "",
                    isFollowSystem,
                    isNeedStartActivity
                )
                return
            }
            applyLanguage(
                locale,
                activityClz.name,
                isFollowSystem,
                isNeedStartActivity
            )
        }

        private fun applyLanguage(
            locale: Locale,
            activityClassName: String,
            isFollowSystem: Boolean,
            isNeedStartActivity: Boolean
        ) {
            if (isFollowSystem) {
                put(
                    KEY_LOCALE,
                    VALUE_FOLLOW_SYSTEM
                )
            } else {
                val localLanguage = locale.language
                val localCountry = locale.country
                put(
                    KEY_LOCALE,
                    "$localLanguage$$localCountry"
                )
            }
            updateLanguage(locale)
            if (isNeedStartActivity) {
                val intent = Intent()
                val realActivityClassName =
                    if (TextUtils.isEmpty(activityClassName)) launcherActivity else activityClassName
                intent.component = ComponentName(
                    context,
                    realActivityClassName
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
        }

        /**
         * Return whether applied the system language by [LanguageUtil].
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isAppliedSystemLanguage: Boolean
            get() = VALUE_FOLLOW_SYSTEM == getString(KEY_LOCALE)

        /**
         * Return whether applied the language by [LanguageUtil].
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isAppliedLanguage: Boolean
            get() = !TextUtils.isEmpty(getString(KEY_LOCALE))

        /**
         * Return the locale.
         *
         * @return the locale
         */
        val currentLocale: Locale
            get() = context.resources.configuration.locale

        fun applyLanguage() {
            val spLocale = getString(KEY_LOCALE)
            if (TextUtils.isEmpty(spLocale)) {
                return
            }
            if (VALUE_FOLLOW_SYSTEM == spLocale) {
                val sysLocale = Resources.getSystem().configuration.locale
                updateLanguage(sysLocale)
                updateLanguage(sysLocale)
                return
            }
            val languageCountry = spLocale.split("\\$").toTypedArray()
            if (languageCountry.size != 2) {
                loggerE("The Language string of $spLocale is not in the correct format.")
                return
            }
            val settingLocale = Locale(languageCountry[0], languageCountry[1])
            updateLanguage(settingLocale)
            updateLanguage(settingLocale)
        }

        private fun updateLanguage(locale: Locale) {
            val context = context
            val resources = context.resources
            val config = resources.configuration
            val contextLocale = config.locale
            if (equals(contextLocale.language, locale.language)
                && equals(
                    contextLocale.country,
                    locale.country
                )
            ) {
                return
            }
            val dm = resources.displayMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale)
                context.createConfigurationContext(config)
            } else {
                config.locale = locale
            }
            resources.updateConfiguration(config, dm)
        }

        private fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
            if (s1 === s2) {
                return true
            }
            var length = 0
            return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
                if (s1 is String && s2 is String) {
                    s1 == s2
                } else {
                    for (i in 0 until length) {
                        if (s1[i] != s2[i]) {
                            return false
                        }
                    }
                    true
                }
            } else false
        }

        private val launcherActivity: String
            private get() {
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setPackage(packageName)
                val pm = context.packageManager
                val info = pm.queryIntentActivities(intent, 0)
                val next = info.iterator().next()
                return if (next != null) {
                    next.activityInfo.name
                } else "no launcher activity"
            }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}