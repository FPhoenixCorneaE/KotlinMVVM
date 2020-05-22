package com.wkz.wanandroid.manager

import com.wkz.extension.isNonNull
import com.wkz.rxretrofit.network.RetrofitManager
import com.wkz.util.SharedPreferencesUtil
import com.wkz.util.encryption.AESUtil
import com.wkz.util.gson.GsonUtil
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidUserInfoBean

object WanAndroidUserManager {

    /* 登录状态 */
    var sHasLoggedOn: Boolean
        set(loginState) {
            SharedPreferencesUtil.put(
                WanAndroidConstant.WAN_ANDROID_LOGIN_STATUS,
                loginState
            )
        }
        get() = SharedPreferencesUtil.getBoolean(
            WanAndroidConstant.WAN_ANDROID_LOGIN_STATUS,
            false
        )

    /* 用户信息 */
    var sUserInfo: WanAndroidUserInfoBean?
        set(userInfo) {
            when {
                userInfo.isNonNull() -> {
                    sHasLoggedOn = true
                    /* AES加密 */
                    SharedPreferencesUtil.put(
                        WanAndroidConstant.WAN_ANDROID_USER_INFO,
                        AESUtil.encrypt(GsonUtil.toJson(userInfo!!))
                    )
                }
                else -> {
                    sHasLoggedOn = false
                    SharedPreferencesUtil.put(
                        WanAndroidConstant.WAN_ANDROID_USER_INFO,
                        ""
                    )
                }
            }
        }
        get() {
            return when {
                sHasLoggedOn -> {
                    /* AES解密 */
                    GsonUtil.fromJson(
                        AESUtil.decrypt(
                            SharedPreferencesUtil.getString(
                                WanAndroidConstant.WAN_ANDROID_USER_INFO,
                                ""
                            )
                        )
                        , WanAndroidUserInfoBean::class.java
                    )
                }
                else -> {
                    null
                }
            }
        }

    /**
     * 退出登录
     */
    fun logout() {
        // 清空Cookies
        RetrofitManager.clearCookieJar()
        // 清空用户信息
        sUserInfo = null
    }
}