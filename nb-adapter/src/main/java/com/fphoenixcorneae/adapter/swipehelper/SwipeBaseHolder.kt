package com.fphoenixcorneae.adapter.swipehelper

import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class SwipeBaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Expanding {

    abstract val slidItemWith: Int

    override val actionWidth: Float
        get() = slidItemWith.toFloat()
}
