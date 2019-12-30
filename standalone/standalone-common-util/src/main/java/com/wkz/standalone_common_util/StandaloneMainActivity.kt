package com.wkz.standalone_common_util

import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.wkz.extension.isNonNull
import com.wkz.extension.isNull
import com.wkz.extension.showToast
import com.wkz.util.*
import kotlinx.android.synthetic.main.standalone_activity_main.*

class StandaloneMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standalone_activity_main)
        ContextUtil.init(this)
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            // 隐藏线程信息 默认：显示
            .showThreadInfo(false)
            // 决定打印多少行（每一行代表一个方法）默认：2
            .methodCount(2)
            // (Optional) Hides internal method calls up to offset. Default 5
            .methodOffset(7)
            // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .tag("KotlinMVVM")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        iniListener()
        initData()
    }

    private fun iniListener() {
        // 申请权限
        mBtnApplyLocation.setOnClickListener {
            PermissionUtil.requestLocationPermission(this, object : PermissionCallBack {
                /**
                 * 申请权限成功
                 *
                 * @param context 上下文
                 */
                override fun onPermissionGranted(context: Context?) {
                    showToast("申请定位权限成功")
                }

                /**
                 * 申请权限失败
                 *
                 * @param context 上下文
                 * @param type    类型，1是拒绝权限，2是申请失败
                 */
                override fun onPermissionDenied(context: Context?, type: Int) {
                    showToast("申请定位权限失败,Type:$type")
                }
            })
        }
        mBtnApplyPhone.setOnClickListener {
            IntentUtil.callPhone(this,"13104871646")
        }
        mBtnApplyWrite.setOnClickListener {
            PermissionUtil.requestWritePermission(this, object : PermissionCallBack {
                /**
                 * 申请权限成功
                 *
                 * @param context 上下文
                 */
                override fun onPermissionGranted(context: Context?) {
                    showToast("申请读写权限成功")
                }

                /**
                 * 申请权限失败
                 *
                 * @param context 上下文
                 * @param type    类型，1是拒绝权限，2是申请失败
                 */
                override fun onPermissionDenied(context: Context?, type: Int) {
                    showToast("申请读写权限失败,Type:$type")
                }
            })
        }
        mBtnApplySms.setOnClickListener {
            IntentUtil.sendTextMessage(this, "", "测试发送短信")
        }
        mBtnApplyCamera.setOnClickListener {
            IntentUtil.openCamera(this@StandaloneMainActivity)
        }

        mBtnShortcut.setOnClickListener {
            IntentUtil.startActivity(
                this,
                StandaloneShortcutActivity::class.java,
                BundleBuilder.of()
                    .putBoolean("Boolean", true)
                    .putString("String", "String")
                    .putString("String1", "String1")
                    .get(),
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                200
            )
        }
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
        mTvIsNullOrNonNull.text =
            "${isNull}--isNull:${isNull.isNull()}  --isNonNull:${isNull.isNonNull()}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            200 -> {
                showToast("requestCode:$requestCode")
            }
        }
    }
}
