package com.fphoenixcorneae.wanandroid.manager

import com.fphoenixcorneae.ext.isNonNull
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager
import com.fphoenixcorneae.util.SharedPreferencesUtil
import com.fphoenixcorneae.util.encryption.AESUtil
import com.fphoenixcorneae.util.gson.GsonUtil
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidUserInfoBean

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
                    val secretKey = AESUtil.initKey()
                    SharedPreferencesUtil.put(
                        WanAndroidConstant.WAN_ANDROID_USER_INFO_SECRET_KEY,
                        String(secretKey, Charsets.ISO_8859_1)
                    )
                    SharedPreferencesUtil.put(
                        WanAndroidConstant.WAN_ANDROID_USER_INFO,
                        AESUtil.encrypt(GsonUtil.toJson(userInfo!!), secretKey)
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
                    val secretKey = SharedPreferencesUtil.getString(
                        WanAndroidConstant.WAN_ANDROID_USER_INFO_SECRET_KEY,
                        ""
                    ).toByteArray(Charsets.ISO_8859_1)
                    GsonUtil.fromJson(
                        AESUtil.decrypt(
                            SharedPreferencesUtil.getString(
                                WanAndroidConstant.WAN_ANDROID_USER_INFO,
                                ""
                            ),
                            secretKey
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