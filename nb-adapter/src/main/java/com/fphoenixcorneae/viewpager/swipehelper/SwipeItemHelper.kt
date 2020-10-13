package com.fphoenixcorneae.viewpager.swipehelper

import androidx.recyclerview.widget.RecyclerView

class SwipeItemHelper(slidTranslate: SlidTranslate) {
    private val extension: WItemTouchHelperPlus

    init {
        val callback = PlusItemSlideCallback(slidTranslate)
        extension = WItemTouchHelperPlus(callback)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        extension.attachToRecyclerView(recyclerView)
    }
}
