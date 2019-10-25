package com.wkz.widget.magicindicator.helper

import androidx.viewpager.widget.ViewPager

import com.wkz.widget.magicindicator.MagicIndicator

/**
 * 简化和ViewPager绑定
 */
object ViewPagerHelper {
    fun bind(magicIndicator: MagicIndicator, viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magicIndicator.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }
}