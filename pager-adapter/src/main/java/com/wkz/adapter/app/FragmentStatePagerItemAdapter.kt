/**
 * Copyright (C) 2015 ogaclejapan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wkz.adapter.app

import android.view.ViewGroup

import java.lang.ref.WeakReference
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentStatePagerItemAdapter(fm: FragmentManager, private val pages: FragmentPagerItems) :
    FragmentStatePagerAdapter(fm) {
    private val holder: SparseArrayCompat<WeakReference<Fragment>> = SparseArrayCompat(pages.size)

    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        return getPagerItem(position).instantiate(pages.context, position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = super.instantiateItem(container, position)
        if (item is Fragment) {
            holder.put(position, WeakReference(item))
        }
        return item
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        holder.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return getPagerItem(position).title
    }

    override fun getPageWidth(position: Int): Float {
        return getPagerItem(position).width
    }

    fun getPage(position: Int): Fragment? {
        val weakRefItem = holder.get(position)
        return weakRefItem?.get()
    }

    protected fun getPagerItem(position: Int): FragmentPagerItem {
        return pages[position]
    }
}
