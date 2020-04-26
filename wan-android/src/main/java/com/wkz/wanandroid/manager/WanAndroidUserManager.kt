package com.wkz.wanandroid.manager

import com.wkz.util.SharedPreferencesUtil
import com.wkz.util.encryption.AESUtil
import com.wkz.util.gson.GsonUtil
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidUserInfoBean

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

    /* 用户信息,AES解密 */
    val mUserInfo = GsonUtil.fromJson(
        AESUtil.decrypt(
            SharedPreferencesUtil.getString(WanAndroidConstant.WAN_ANDROID_USER_INFO, "")
        )
        , WanAndroidUserInfoBean::class.java
    )

    /* 设置用户信息,AES加密 */
    fun setUserInfo(userInfoBean: WanAndroidUserInfoBean) {
        setLoginStatus(true)
        SharedPreferencesUtil.put(
            WanAndroidConstant.WAN_ANDROID_USER_INFO,
            AESUtil.encrypt(GsonUtil.toJson(userInfoBean))
        )
    }
}