package com.wkz.wanandroid.manager

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

    /* 用户信息,AES解密 */
    val sUserInfo: WanAndroidUserInfoBean?
        get() {
            return when {
                sHasLoggedOn -> {
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

    /* 设置用户信息,AES加密 */
    fun setUserInfo(userInfoBean: WanAndroidUserInfoBean) {
        sHasLoggedOn = true
        SharedPreferencesUtil.put(
            WanAndroidConstant.WAN_ANDROID_USER_INFO,
            AESUtil.encrypt(GsonUtil.toJson(userInfoBean))
        )
    }
}