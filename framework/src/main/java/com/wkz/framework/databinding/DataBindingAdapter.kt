package com.wkz.framework.databinding

import android.text.Spanned
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions


class DataBindingAdapter {

    companion object {
        /**
         * @desc: @BindingAdapter
         */
        @BindingAdapter(value = ["imageUrl", "placeholder", "shapeType"], requireAll = false)
        @JvmStatic
        fun setSrc(ivTarget: ImageView, imageUrl: Any?, placeholder: Int, shapeType: String?) {
            Glide.with(ivTarget)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(false)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .transform(CircleCrop())
                )
                .into(ivTarget)
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText(view: TextView, text: CharSequence?) {
            val oldText = view.text
            if (text === oldText || text == null && oldText.length == 0) {
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