package com.wkz.widget.magicindicator.adapter

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver

import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.IPagerTitleView

/**
 * CommonNavigator适配器，通过它可轻松切换不同的指示器样式
 */
abstract class CommonNavigatorAdapter {

    private val mDataSetObservable = DataSetObservable()

    abstract val count: Int

    abstract fun getTitleView(context: Context, index: Int): IPagerTitleView

    abstract fun getIndicator(context: Context): IPagerIndicator

    open fun getTitleWeight(context: Context, index: Int): Float {
        return 1f
    }

    fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    fun notifyDataSetChanged() {
        mDataSetObservable.notifyChanged()
    }

    fun notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated()
    }
}
