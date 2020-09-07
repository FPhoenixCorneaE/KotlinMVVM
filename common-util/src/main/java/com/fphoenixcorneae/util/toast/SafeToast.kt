package com.fphoenixcorneae.util.toast

import android.app.Application
import android.os.Handler
import android.os.Message
import android.view.WindowManager.BadTokenException
import android.widget.Toast

/**
 * Toast 显示安全处理
 */
internal class SafeToast(application: Application?) :
    BaseToast(application) {
    private class SafeHandler(private val mHandler: Handler) :
        Handler() {
        override fun handleMessage(msg: Message) { // 捕获这个异常，避免程序崩溃
            try { /*
                 目前发现在 Android 7.1 主线程被阻塞之后弹吐司会导致崩溃，可使用 Thread.sleep(5000) 进行复现
                 查看源码得知 Google 已经在 Android 8.0 已经修复了此问题
                 主线程阻塞之后 Toast 也会被阻塞，Toast 因为超时导致 Window Token 失效
                 */
                mHandler.handleMessage(msg)
            } catch (ignored: BadTokenException) {
                // android.view.WindowManager$BadTokenException:
                // Unable to add window -- token android.os.BinderProxy is not valid; is your activity running?
            }
        }

    }

    init {
        // 反射 Toast 中的字段
        try { // 获取 mTN 字段对象
            val mTNField = Toast::class.java.getDeclaredField("mTN")
            mTNField.isAccessible = true
            val mTN = mTNField[this]
            // 获取 mTN 中的 mHandler 字段对象
            val mHandlerField =
                mTNField.type.getDeclaredField("mHandler")
            mHandlerField.isAccessible = true
            val mHandler = mHandlerField[mTN] as Handler
            // 偷梁换柱
            mHandlerField[mTN] = SafeHandler(mHandler)
        } catch (ignored: Exception) {
        }
    }
}