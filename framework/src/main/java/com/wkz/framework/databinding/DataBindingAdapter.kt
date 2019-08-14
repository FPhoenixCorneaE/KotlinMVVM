package com.wkz.framework.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions

class DataBindingAdapter {

    companion object {
        /**
         * @desc: @BindingAdapter
         */
        @BindingAdapter(value = ["src", "placeholder", "shapeType"], requireAll = false)
        @JvmStatic
        fun setSrc(ivTarget: ImageView, src: Any?, placeholder: Int, shapeType: String?) {
            Glide.with(ivTarget)
                .load(src)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(false)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .transform(CircleCrop())
                )
                .into(ivTarget)
        }
    }
}