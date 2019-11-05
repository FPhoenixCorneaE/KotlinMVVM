package com.wkz.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * @desc: 万能ViewHolder
 */
class NBViewHolder private constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {

        fun create(itemView: View): NBViewHolder {
            return NBViewHolder(itemView)
        }
    }
}
