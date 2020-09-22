package com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * @desc: 热门标签 Bean
 */
@Keep
data class OpenEyesTabInfoBean(val tabInfo: TabInfo) : Serializable {
    data class TabInfo(val tabList: ArrayList<Tab>) : Serializable

    data class Tab(val id: Long, val name: String, val apiUrl: String) : Serializable
}
