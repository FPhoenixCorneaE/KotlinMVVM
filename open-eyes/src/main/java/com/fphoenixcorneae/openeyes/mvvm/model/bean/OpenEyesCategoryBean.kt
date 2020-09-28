package com.fphoenixcorneae.openeyes.mvvm.model.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @desc 分类 Bean
 */
@Keep
@Parcelize
data class OpenEyesCategoryBean(
    val id: Long,
    val name: String,
    val description: String,
    val bgPicture: String,
    val bgColor: String,
    val headerImage: String
) : Serializable, Parcelable
