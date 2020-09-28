package com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment

import android.os.Bundle
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wave.AvatarWaveHelper
import kotlinx.android.synthetic.main.open_eyes_fragment_mine.*

/**
 * @desc 我的 Fragment
 * @date 2020-09-23 16:16
 */
class OpenEyesMineFragment : OpenEyesBaseFragment() {

    companion object {

        fun getInstance(): OpenEyesMineFragment {
            val fragment = OpenEyesMineFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mAvatarWaveHelper: AvatarWaveHelper

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_mine

    override fun initView() {

    }

    override fun lazyLoadData() {
        mAvatarWaveHelper = AvatarWaveHelper(
            mWvAvatar,
            mIvAvatar,
            ResourceUtil.getColor(R.color.open_eyes_color_white),
            ResourceUtil.getColor(R.color.open_eyes_color_white_alpha50)
        )
    }

    override fun isAlreadyLoadedData(): Boolean {
        return true
    }

    override fun onPause() {
        super.onPause()
        mAvatarWaveHelper.cancel()
    }

    override fun onResume() {
        super.onResume()
        mAvatarWaveHelper.start()
    }

}