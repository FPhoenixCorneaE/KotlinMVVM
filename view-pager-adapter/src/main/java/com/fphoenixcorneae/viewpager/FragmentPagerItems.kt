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
package com.fphoenixcorneae.viewpager

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

class FragmentPagerItems private constructor(
    context: Context
) : PagerItems<FragmentPagerItem>(context) {

    class Creator(context: Context) {

        private val items: FragmentPagerItems = FragmentPagerItems(context)

        fun add(@StringRes title: Int, clazz: Class<out Fragment>): Creator {
            return add(FragmentPagerItem.of(items.context.getString(title), clazz))
        }

        fun add(@StringRes title: Int, clazz: Class<out Fragment>, args: Bundle): Creator {
            return add(FragmentPagerItem.of(items.context.getString(title), clazz, args))
        }

        fun add(@StringRes title: Int, width: Float, clazz: Class<out Fragment>): Creator {
            return add(FragmentPagerItem.of(items.context.getString(title), width, clazz))
        }

        fun add(
            @StringRes title: Int, width: Float, clazz: Class<out Fragment>,
            args: Bundle
        ): Creator {
            return add(FragmentPagerItem.of(items.context.getString(title), width, clazz, args))
        }

        fun add(title: CharSequence, clazz: Class<out Fragment>): Creator {
            return add(FragmentPagerItem.of(title, clazz))
        }

        fun add(title: CharSequence, clazz: Class<out Fragment>, args: Bundle): Creator {
            return add(FragmentPagerItem.of(title, clazz, args))
        }

        fun add(item: FragmentPagerItem): Creator {
            items.add(item)
            return this
        }

        fun create(): FragmentPagerItems {
            return items
        }
    }

    companion object {

        fun with(context: Context): Creator {
            return Creator(context)
        }
    }
}
