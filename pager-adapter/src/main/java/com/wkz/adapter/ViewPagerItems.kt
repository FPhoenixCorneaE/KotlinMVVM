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
package com.wkz.adapter

import android.content.Context

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

class ViewPagerItems(context: Context) : PagerItems<ViewPagerItem>(context) {

    class Creator(context: Context) {

        private val items: ViewPagerItems = ViewPagerItems(context)

        fun add(@StringRes title: Int, @LayoutRes resource: Int): Creator {
            return add(ViewPagerItem.of(items.context.getString(title), resource))
        }

        fun add(@StringRes title: Int, width: Float, @LayoutRes resource: Int): Creator {
            return add(ViewPagerItem.of(items.context.getString(title), width, resource))
        }

        fun add(title: CharSequence, @LayoutRes resource: Int): Creator {
            return add(ViewPagerItem.of(title, resource))
        }

        fun add(item: ViewPagerItem): Creator {
            items.add(item)
            return this
        }

        fun create(): ViewPagerItems {
            return items
        }
    }

    companion object {

        fun with(context: Context): Creator {
            return Creator(context)
        }
    }
}
