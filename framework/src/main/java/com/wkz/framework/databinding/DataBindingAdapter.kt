package com.wkz.framework.databinding

import android.text.Spanned
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wkz.annotation.ShapeType

/**
 * @desc:自定义属性
 */
class DataBindingAdapter {

    companion object {
        /**
         * @desc: @BindingAdapter
         */
        @BindingAdapter(value = ["imageUrl", "placeholder", "shapeType", "roundingRadius"], requireAll = false)
        @JvmStatic
        fun setSrc(
            ivTarget: ImageView,
            imageUrl: Any?,
            placeholder: Int,
            @ShapeType shapeType: Int, @IntRange(from = 1) roundingRadius: Int
        ) {
            val requestOptions = RequestOptions()
                .skipMemoryCache(false)
                .placeholder(placeholder)
                .error(placeholder)
            when (shapeType) {
                ShapeType.CIRCLE -> requestOptions.transform(CircleCrop())
                ShapeType.ROUNDED_RECTANGLE -> requestOptions.transform(CircleCrop(), RoundedCorners(roundingRadius))
                else -> requestOptions.transform(CenterCrop())
            }
            Glide.with(ivTarget)
                .load(imageUrl)
                .apply(requestOptions)
                .into(ivTarget)
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText(view: TextView, text: CharSequence?) {
            val oldText = view.text
            if (text === oldText || text == null && oldText.isEmpty()) {
                return
            }
            if (text is Spanned) {
                if (text == oldText) {
                    // No change in the spans, so don't set anything.
                    return
                }
            } else if (TextUtils.equals(text, oldText)) {
                // No content changes, so don't set anything.
                return
            }
            view.text = text
        }
    }
}