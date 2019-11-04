package com.wkz.adapter

/**
 * @desc: 动画类型
 */
enum class AnimationType(var resId: Int) {
    /**
     * 从右边平移进来
     */
    TRANSLATE_FROM_RIGHT(R.anim.recycler_item_translate_from_right),
    /**
     * 从左边平移进来
     */
    TRANSLATE_FROM_LEFT(R.anim.recycler_item_translate_from_left),
    /**
     * 从底部平移上来
     */
    TRANSLATE_FROM_BOTTOM(R.anim.recycler_item_translate_from_bottom),
    /**
     * 缩放
     */
    SCALE(R.anim.recycler_item_scale),
    /**
     * 淡入
     */
    ALPHA(R.anim.recycler_item_alpha)
}
