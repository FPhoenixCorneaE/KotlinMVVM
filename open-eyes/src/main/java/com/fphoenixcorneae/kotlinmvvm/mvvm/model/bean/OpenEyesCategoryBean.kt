package com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * @desc:分类 Bean
 */
@Keep
data class OpenEyesCategoryBean(
    val id: Long,
    val name: String,
    val description: String,
    val bgPicture: String,
    val bgColor: String,
    val headerImage: String
) : Serializable
