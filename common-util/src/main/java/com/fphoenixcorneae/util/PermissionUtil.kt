package com.fphoenixcorneae.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.fphoenixcorneae.ext.isNull
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 权限申请工具类
 * 注意要点：
 * 1、this 参数可以是 FragmentActivity 或 Fragment。
 *    如果你在 fragment 中使用 RxPermissions，你应该传递 fragment 实例，而不是fragment.getActivity()。
 * 2、请求权限，必须在初始化阶段比如 onCreate 中调用。
 *    应用程序可能在权限请求期间重新启动，因此请求必须在初始化阶段完成。
 * 3、统一规范权限，避免混乱。
 * 4、该库可以和rx结合使用。
 *
 *
 * 参考案例：https://github.com/tbruyelle/RxPermissions
 */
object PermissionUtil {
    /**
     * 定位权限
     */
    private val LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /**
     * 电话权限
     */
    private val PHONE =
        arrayOf(Manifest.permission.CALL_PHONE)

    /**
     * 读写存储权限
     */
    private val WRITE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * 短信权限
     */
    private val SMS =
        arrayOf(Manifest.permission.SEND_SMS)

    /**
     * 相机权限，相机权限包括读写文件权限
     */
    private val CAMERA = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * 定位权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestLocationPermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // requestEachCombined
        // 返回的权限名称:将多个权限名合并成一个
        // 返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .requestEachCombined(*LOCATION)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 读写权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestWritePermissionDelay(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // ensureEachCombined
        // 必须配合rxJava,回调结果与requestEachCombined一样，不过这个可以延迟操作
        Observable.timer(10, TimeUnit.MILLISECONDS)
            .compose(rxPermissions.ensureEachCombined(*WRITE))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 读写权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestWritePermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // requestEachCombined
        // 返回的权限名称:将多个权限名合并成一个
        // 返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .requestEachCombined(*WRITE)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 相机权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestCameraPermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // requestEachCombined
        // 返回的权限名称:将多个权限名合并成一个
        // 返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .requestEachCombined(*CAMERA)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 申请短信息权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestSmsPermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        //requestEach   把每一个权限的名称和申请结果都列出来
        rxPermissions
            .requestEach(*SMS)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时，还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 申请电话权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestPhonePermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // requestEachCombined
        // 返回的权限名称:将多个权限名合并成一个
        // 返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .requestEachCombined(*PHONE)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.BAN_ALLOWED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(
                            activity,
                            PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                        )
                    }
                }
            }
    }

    /**
     * 申请权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun requestPermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null,
        vararg permissions: String
    ) {
        activity?.apply {
            RxPermissions(this)
                .requestEachCombined(*permissions)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            // 用户已经同意该权限
                            callBack?.onPermissionGranted(activity)
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                            // 那么下次再次启动时。还会提示请求权限的对话框
                            callBack?.onPermissionDenied(
                                activity,
                                PermissionCallBack.BAN_ALLOWED
                            )
                        }
                        else -> {
                            // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                            callBack?.onPermissionDenied(
                                activity,
                                PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION
                            )
                        }
                    }
                }
        }
    }
}

/**
 * 权限申请回调callback
 */
interface PermissionCallBack {
    /**
     * 申请权限成功
     *
     * @param context 上下文
     */
    fun onPermissionGranted(context: Context?)

    /**
     * 申请权限失败
     *
     * @param context 上下文
     * @param type    类型
     *                1是选中禁止后不再询问,并禁止允许
     *                2是禁止允许
     */
    fun onPermissionDenied(context: Context?, type: Int)

    companion object {
        /**
         * 选中禁止后不再询问,并禁止允许
         */
        const val STOP_ASKING_AFTER_PROHIBITION = 1

        /**
         * 禁止允许
         */
        const val BAN_ALLOWED = 2
    }
}