package com.fphoenixcorneae.animated_bottom_view

import android.view.View

/**
 * 导航条Item点击监听
 */
interface OnNavigationItemClickListener {
    fun onItemClick(itemView: View, index: Int)
}