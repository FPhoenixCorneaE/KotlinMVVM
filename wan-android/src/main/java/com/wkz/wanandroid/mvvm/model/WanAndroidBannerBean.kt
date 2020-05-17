package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 首页Banner
 */
data class WanAndroidBannerBean(
    var id: Int,
    var title: String,
    var desc: String,
    var type: Int,
    var url: String,
    var imagePath: String
) : Serializable