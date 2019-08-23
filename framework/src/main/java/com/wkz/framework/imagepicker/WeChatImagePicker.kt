package com.wkz.framework.imagepicker

import android.content.Context
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker.entity.sources.Camera
import com.qingmei2.rximagepicker.entity.sources.Gallery
import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration
import com.qingmei2.rximagepicker_extension_wechat.ui.WechatImagePickerActivity
import io.reactivex.Observable

interface WeChatImagePicker {

    /**
     * 打开图库
     *
     * @param context 上下文
     * @param config  配置
     * @return Observable<Result>
    </Result> */
    @Gallery(componentClazz = WechatImagePickerActivity::class, openAsFragment = false)
    fun openGallery(context: Context, config: ICustomPickerConfiguration): Observable<Result>

    /**
     * 打开相机
     *
     * @param context 上下文
     * @return Observable<Result>
    </Result> */
    @Camera
    fun openCamera(context: Context): Observable<Result>
}
