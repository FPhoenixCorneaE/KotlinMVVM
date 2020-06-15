package com.fphoenixcorneae.animated_bottom_view

/**
 * @desc：底部导航Item属性
 * @date：2020-06-15 15:57
 */
data class AnimatedNavigationItem(
    var resourceId: Int = 0,
    var itemName: String = "",
    var normalColor: Int = 0,
    var selectedColor: Int = 0
)