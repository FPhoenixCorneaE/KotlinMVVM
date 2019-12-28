package com.wkz.standalone_common_util

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import com.wkz.extension.isNonNull
import com.wkz.extension.isNull
import com.wkz.util.ContextUtil
import com.wkz.util.PermissionCallBack
import com.wkz.util.PermissionUtil
import com.wkz.util.SpannableStringUtil
import kotlinx.android.synthetic.main.standalone_activity_main.*

class StandaloneMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standalone_activity_main)
        ContextUtil.init(this)
        initData()
    }

    private fun initData() {
        // Spannable字符串
        mTvSpannableString.text = SpannableStringUtil.Builder()
            .append("说,\"你是猪！\"")
            .setAlign(Layout.Alignment.ALIGN_NORMAL)
            .setUnderline()
            .setFontSize(24, true)
            .setBoldItalic()
            .setBackgroundColor(Color.CYAN)
            .setForegroundColor(Color.BLUE)
            .appendLine("\"你是猪！\"")
            .setFontSize(20, true)
            .setQuoteColor(Color.YELLOW, 5, 5)
            .setBackgroundColor(Color.RED)
            .setForegroundColor(Color.WHITE)
            .setBlur(5F, BlurMaskFilter.Blur.NORMAL)
            .appendLine("")
            .setAlign(Layout.Alignment.ALIGN_NORMAL)
            .setResourceId(R.mipmap.standalone_ic_launcher)
            .append("说,\"我是猪！\"")
            .setAlign(Layout.Alignment.ALIGN_NORMAL)
            .setFontProportion(1.5F)
            .setStrikethrough()
            .append("\"你是猪！\"")
            .setSuperscript()
            .appendLine("我让你说,\"我是猪！\"")
            .setAlign(Layout.Alignment.ALIGN_NORMAL)
            .setFontSize(24, true)
            .setForegroundColor(Color.DKGRAY)
            .append("\"你是猪！\"")
            .setAlign(Layout.Alignment.ALIGN_NORMAL)
            .setSubscript()
            .create()

        // 判断是否为null
        val isNull: String? = null
        mTvIsNullOrNonNull.text = "isNull:${isNull.isNull()}  isNonNull:${isNull.isNonNull()}"

        // 申请权限
        mTvApplyLocation.setOnClickListener {
            PermissionUtil.checkLocationPermission(this, object : PermissionCallBack {
                /**
                 * 申请权限成功
                 *
                 * @param context 上下文
                 */
                override fun onPermissionGranted(context: Context?) {
                }

                /**
                 * 申请权限失败
                 *
                 * @param context 上下文
                 * @param type    类型，1是拒绝权限，2是申请失败
                 */
                override fun onPermissionDenied(context: Context?, type: Int) {
                }
            })
        }
        mTvApplyPhone.setOnClickListener {
            PermissionUtil.checkPhonePermissions(this)
        }
        mTvApplyWrite.setOnClickListener {
            PermissionUtil.checkWritePermissionsRequest(this)
        }
        mTvApplySms.setOnClickListener {
            PermissionUtil.checkSmsPermissions(this)
        }
        mTvApplyCamera.setOnClickListener {
            PermissionUtil.checkCameraPermissions(this)
        }
    }
}
