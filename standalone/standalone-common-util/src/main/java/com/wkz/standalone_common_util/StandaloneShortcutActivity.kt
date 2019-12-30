package com.wkz.standalone_common_util

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wkz.extension.showToast
import com.wkz.util.ShortcutUtil
import kotlinx.android.synthetic.main.standalone_activity_shortcut.*

class StandaloneShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standalone_activity_shortcut)
        iniListener()
        initData()
    }

    private fun iniListener() {
        mBtnHasShortcut.setOnClickListener {
            showToast("存在快捷方式：${ShortcutUtil.hasShortcut(this, "快捷方式")}")
        }
        mBtnCreateShortcut.setOnClickListener {
            ShortcutUtil.createShortcut(
                this,
                "快捷方式",
                "111",
                BitmapFactory.decodeResource(resources, R.mipmap.standalone_ic_launcher)
            )
        }
        mBtnDeleteShortcut.setOnClickListener {
            ShortcutUtil.deleteShortcut(this, "快捷方式")
        }
    }

    private fun initData() {
        showToast(
            "Bundle数据${intent.getBooleanExtra(
                "Boolean",
                false
            )}  ${intent.getStringExtra("String")}  ${intent.getStringExtra("String1")}"
        )
    }
}