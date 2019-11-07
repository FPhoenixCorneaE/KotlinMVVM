package com.wkz.adapter.viewpager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wkz.adapter.app.FragmentPagerItem
import com.wkz.adapter.app.FragmentPagerItems

/**
 * viewpager2适配器
 */
class FragmentStatePager2ItemAdapter(
    fragmentActivity: FragmentActivity,
    private val pages: FragmentPagerItems
) : FragmentStateAdapter(fragmentActivity) {
    /**
     * Provide a new Fragment associated with the specified position.
     *
     *
     * The adapter will be responsible for the Fragment lifecycle:
     *
     *  * The Fragment will be used to display an item.
     *  * The Fragment will be destroyed when it gets too far from the viewport, and its state
     * will be saved. When the item is close to the viewport again, a new Fragment will be
     * requested, and a previously saved state will be used to initialize it.
     *
     * @see ViewPager2.setOffscreenPageLimit
     */
    override fun createFragment(position: Int): Fragment {
        return getPagerItem(position).instantiate(pages.context, position)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = pages.size

    fun getPageTitle(position: Int): CharSequence {
        return getPagerItem(position).title
    }

    protected fun getPagerItem(position: Int): FragmentPagerItem {
        return pages[position]
    }
}