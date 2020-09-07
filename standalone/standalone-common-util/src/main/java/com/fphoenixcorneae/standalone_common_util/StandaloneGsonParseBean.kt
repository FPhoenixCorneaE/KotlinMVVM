package com.fphoenixcorneae.standalone_common_util

data class StandaloneGsonParseBean constructor(
    var like: Boolean = false,
    var favorite: Boolean = false,
    var width: Double = 0.0,
    var height: Double = 0.0,
    var originalPrice: Float = 0f,
    var realPrice: Float = 0f,
    var originalCount: Int = 0,
    var realCount: Int = 0,
    var originalTime: Long = 0,
    var realTime: Long = 0,
    var content: String? = null
)