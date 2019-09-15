package com.wkz.kotlinmvvm.mvvm.model.bean

import java.io.Serializable

/**
 * @desc: 热门标签 Bean
 */
data class OpenEyesTabInfoBean(val tabInfo: TabInfo) : Serializable {
    data class TabInfo(val tabList: ArrayList<Tab>) : Serializable

    data class Tab(val id: Long, val name: String, val apiUrl: String) : Serializable
}
