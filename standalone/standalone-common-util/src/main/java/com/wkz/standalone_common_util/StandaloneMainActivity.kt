package com.wkz.standalone_common_util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Layout
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.orhanobut.logger.Logger
import com.wkz.extension.isNonNull
import com.wkz.extension.isNull
import com.wkz.extension.showToast
import com.wkz.util.*
import com.wkz.util.gson.GsonUtil
import com.wkz.util.xtoast.XToast
import com.wkz.util.xtoast.draggable.MovingDraggable
import com.wkz.util.xtoast.listener.OnClickListener
import kotlinx.android.synthetic.main.standalone_activity_main.*
import java.io.File

class StandaloneMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standalone_activity_main)
        iniListener()
        initData()
    }

    @SuppressLint("NewApi")
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
            IntentUtil.callPhone(this, "13104871646")
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
                    when (type) {
                        PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION -> {
                            IntentUtil.openApplicationDetailsSettings()
                        }
                    }
                }
            })
        }
        mBtnApplySms.setOnClickListener {
            IntentUtil.sendTextMessage(this, "", "测试发送短信")
        }
        mBtnApplyCamera.setOnClickListener {
            IntentUtil.openCamera(this@StandaloneMainActivity)
        }

        // 生成快捷方式
        mBtnShortcut.setOnClickListener {
            IntentUtil.startActivity(
                this,
                StandaloneShortcutActivity::class.java,
                BundleBuilder.of()
                    .putBoolean("Boolean", true)
                    .putString("String", "String")
                    .putString("String1", "String1")
                    .get(),
                200,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right

            )
        }

        // 震动
        mBtnVibrateOneShot.setOnClickListener {
            VibrateUtil.vibrate(200)
        }
        mBtnVibrateWaveform.setOnClickListener {
            VibrateUtil.vibrate(arrayOf(1000L, 200L, 1000L, 200L, 1000L, 200L).toLongArray(), 1)
        }

        // 压缩、解压
        mBtnZipFile.setOnClickListener {
            val srcFilePath = File(Environment.getExternalStorageDirectory(), "周报/")
            if (srcFilePath.exists()) {
                showToast("源文件存在！")
                val zipFilePath =
                    File(Environment.getExternalStorageDirectory(), "周报.zip")
                if (!zipFilePath.exists()) {
                    zipFilePath.createNewFile()
                }
            }
        }

        // 申请"android.permission.WRITE_SETTINGS"权限
        PermissionUtil.requestPermission(
            ActivityUtil.topActivity as FragmentActivity,
            object : PermissionCallBack {
                override fun onPermissionGranted(context: Context?) {
                    BrightnessUtil.setAutoBrightnessEnabled(true)
                }

                override fun onPermissionDenied(context: Context?, type: Int) {
                    if (!Settings.System.canWrite(context)) {
                        IntentUtil.openApplicationManageWriteSettings()
                    }
                }
            },
            Manifest.permission.WRITE_SETTINGS
        )
        // 亮度
        mSbBrightness.setProgress(BrightnessUtil.brightness, true)
        mSbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                BrightnessUtil.setBrightness(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // 杀死后台进程
        Logger.t("ProcessUtil").d(ProcessUtil.foregroundProcessName)
        Logger.t("ProcessUtil").d(ProcessUtil.currentProcessName)
        mBtnKillAllBackgroundProcesses.setOnClickListener {
            ProcessUtil.killAllBackgroundProcesses()
        }

        var xToast: XToast? = null
        // 全局可拖拽的Toast
        mBtnDraggableToast.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                if (xToast.isNull()) {
                    xToast = XToast(ContextUtil.context)
                        .setView(R.layout.standalone_floating_toast)
                        .setGravity(Gravity.END or Gravity.BOTTOM)
                        .setXOffset(100)
                        .setYOffset(100)
                        // 设置指定的拖拽规则
                        .setDraggable(MovingDraggable())
                        .setOnClickListener(object : OnClickListener {
                            override fun onClick(toast: XToast?, view: View) {
                                // 点击后跳转到拨打电话界面
                                val intent = Intent(Intent.ACTION_DIAL)
                                startActivity(intent)
                            }
                        })
                        .show()
                } else {
                    xToast!!.cancel()
                    xToast = null
                }
            } else {
                PermissionUtil.requestPermission(this, object : PermissionCallBack {
                    override fun onPermissionGranted(context: Context?) {
                        if (xToast.isNull()) {
                            xToast = XToast(ContextUtil.context)
                                .setView(R.layout.standalone_floating_toast)
                                .setGravity(Gravity.END or Gravity.BOTTOM)
                                .setXOffset(100)
                                .setYOffset(100)
                                // 设置指定的拖拽规则
                                .setDraggable(MovingDraggable())
                                .setOnClickListener(object : OnClickListener {
                                    override fun onClick(toast: XToast?, view: View) {
                                        // 点击后跳转到拨打电话界面
                                        val intent = Intent(Intent.ACTION_DIAL)
                                        startActivity(intent)
                                    }
                                })
                                .show()
                        } else {
                            xToast!!.cancel()
                            xToast = null
                        }
                    }

                    override fun onPermissionDenied(context: Context?, type: Int) {
                        when (type) {
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION -> {
                                IntentUtil.openSettingsCanDrawOverlays()
                            }
                        }
                    }
                }, Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
        }

        // 崩溃重启
        mBtnCrash.setOnClickListener {
            val mArrays = ArrayList<Long>()
            Logger.d(mArrays[1])
        }

        // Gson解析
        mBtnGsonParse.setOnClickListener {
            val testJson = "{\n" +
                    "    \"like\": \"0\",\n" +
                    "    \"favorite\": \"1\",\n" +
                    "    \"width\": \"\",\n" +
                    "    \"height\": \"null\",\n" +
                    "    \"originalPrice\": \"\",\n" +
                    "    \"realPrice\": \"null\",\n" +
                    "    \"originalCount\": \"\",\n" +
                    "    \"realCount\": \"null\",\n" +
                    "    \"originalTime\": \"\",\n" +
                    "    \"realTime\": \"null\",\n" +
                    "    \"content\": \"null\"\n" +
                    "}"
            val parseResult = GsonUtil.fromJson(testJson, StandaloneGsonParseBean::class.java)
            Logger.d(testJson)
            Logger.d(parseResult)
            showToast(parseResult.toString())
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

    override fun onDestroy() {
        super.onDestroy()
        VibrateUtil.cancel()
    }
}
