package com.wkz.framework.imagepicker

import android.content.Context
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker.entity.sources.Camera
import com.qingmei2.rximagepicker.entity.sources.Gallery
import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration
import com.qingmei2.rximagepicker_extension_zhihu.ui.ZhihuImagePickerActivity

import io.reactivex.Observable

interface ZhihuImagePicker {

    /**
     * 打开图库
     *
     * @param context 上下文
     * @param config  配置
     * @return Observable<Result>
     */
    @Gallery(
        componentClazz = ZhihuImagePickerActivity::class,
        openAsFragment = false
    )
    fun openGalleryAsNormal(
        context: Context,
        config: ICustomPickerConfiguration
    ): Observable<Result>

    /**
     * 打开图库
     *
     * @param context 上下文
     * @param config  配置
     * @return Observable<Result>
     */
    @Gallery(
        componentClazz = ZhihuImagePickerActivity::class,
        openAsFragment = false
    )
    fun openGalleryAsDracula(
        context: Context,
        config: ICustomPickerConfiguration
    ): Observable<Result>

    /**
     * 打开相机
     *
     * @param context 上下文
     * @return Observable<Result>
     */
    @Camera
    fun openCamera(context: Context): Observable<Result>
}
