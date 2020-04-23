package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

data class WanAndroidAccountBody(
    var username: String = "",
    var password: String = ""
) : Serializable