package com.fphoenixcorneae.widget.magicindicator.helper

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

import com.fphoenixcorneae.widget.magicindicator.MagicIndicator

/**
 * 简化和ViewPager绑定
 */
fun MagicIndicator.bindViewPager(viewPager: ViewPager) {
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            this@bindViewPager.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            this@bindViewPager.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            this@bindViewPager.onPageScrollStateChanged(state)
        }
    })
}

fun MagicIndicator.bindViewPager2(viewPager2: ViewPager2) {
    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            this@bindViewPager2.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            this@bindViewPager2.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            this@bindViewPager2.onPageScrollStateChanged(state)
        }
    })
}
