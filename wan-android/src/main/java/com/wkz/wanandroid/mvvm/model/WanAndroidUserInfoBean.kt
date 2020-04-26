package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 账户信息
 */
data class WanAndroidUserInfoBean(
    var admin: Boolean = false,
    var chapterTops: MutableList<String>? = mutableListOf(),
    var collectIds: MutableList<String>? = mutableListOf(),
    var email: String = "",
    var icon: String = "",
    var id: String = "",
    var nickname: String = "",
    var password: String = "",
    var token: String = "",
    var type: Int = 0,
    var username: String = ""
) : Serializable
