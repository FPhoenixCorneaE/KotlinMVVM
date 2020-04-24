package com.wkz.wanandroid.manager

import com.wkz.util.SharedPreferencesUtil
import com.wkz.wanandroid.constant.WanAndroidConstant

object WanAndroidUserManager {

    /* 登录状态 */
    val hasLoggedOn = SharedPreferencesUtil.getBoolean(
        WanAndroidConstant.WAN_ANDROID_LOGIN_STATUS,
        false
    )

    /**
     * 设置登录状态
     */
    fun setLoginStatus(loginStatus: Boolean) {
        SharedPreferencesUtil.put(
            WanAndroidConstant.WAN_ANDROID_LOGIN_STATUS,
            loginStatus
        )
    }
}