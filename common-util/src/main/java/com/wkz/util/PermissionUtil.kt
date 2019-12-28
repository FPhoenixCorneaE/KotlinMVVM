package com.wkz.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wkz.extension.isNull
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
     * 电话
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
    fun checkLocationPermission(
        activity: FragmentActivity?,
        callBack: PermissionCallBack? = null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // request
        // 不支持返回权限名，返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .request(*LOCATION)
            .subscribe { aBoolean ->
                when {
                    aBoolean -> {
                        //已经获得权限
                        callBack?.onPermissionGranted(activity)
                    }
                    else -> {
                        //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
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
    fun checkWritePermissionsTime(
        activity: FragmentActivity?,
        callBack: PermissionCallBack?=null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        //ensure
        //必须配合rxJava,回调结果与request一样，不过这个可以延迟操作
        Observable.timer(10, TimeUnit.MILLISECONDS)
            .compose(rxPermissions.ensure(*WRITE))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aBoolean ->
                when {
                    aBoolean -> {
                        //已经获得权限
                        callBack?.onPermissionGranted(activity)
                    }
                    else -> {
                        //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
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
    fun checkWritePermissionsRequest(
        activity: FragmentActivity?,
        callBack: PermissionCallBack?=null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        //request    不支持返回权限名，返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .request(*WRITE)
            .subscribe { aBoolean ->
                when {
                    aBoolean -> {
                        //已经获得权限
                        callBack?.onPermissionGranted(activity)
                    }
                    else -> {
                        //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
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
    fun checkCameraPermissions(
        activity: FragmentActivity?,
        callBack: PermissionCallBack?=null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        //requestEachCombined
        //返回的权限名称:将多个权限名合并成一个
        //返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
            .requestEachCombined(*CAMERA)
            .subscribe { permission ->
                val shouldShowRequestPermissionRationale =
                    permission.shouldShowRequestPermissionRationale
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时。还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.DEFEATED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
                    }
                }
            }
    }

    /**
     * 检测短信息权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun checkSmsPermissions(
        activity: FragmentActivity?,
        callBack: PermissionCallBack?=null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        //requestEach   把每一个权限的名称和申请结果都列出来
        rxPermissions
            .requestEach(*SMS)
            .subscribe { permission ->
                val shouldShowRequestPermissionRationale =
                    permission.shouldShowRequestPermissionRationale
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        callBack?.onPermissionGranted(activity)
                    }
                    shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                        // 那么下次再次启动时，还会提示请求权限的对话框
                        callBack?.onPermissionDenied(activity, PermissionCallBack.DEFEATED)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
                    }
                }
            }
    }

    /**
     * 检测电话权限
     *
     * @param activity activity
     * @param callBack 回调callBack
     */
    @SuppressLint("CheckResult")
    fun checkPhonePermissions(
        activity: FragmentActivity?,
        callBack: PermissionCallBack?=null
    ) {
        if (activity.isNull()) {
            return
        }
        val rxPermissions = RxPermissions(activity!!)
        // request
        // 不支持返回权限名，返回的权限结果:全部同意时值为true,否则值为false
        rxPermissions
            .request(*PHONE)
            .subscribe { aBoolean ->
                when {
                    aBoolean -> {
                        //已经获得权限
                        callBack?.onPermissionGranted(activity)
                    }
                    else -> {
                        //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                        callBack?.onPermissionDenied(activity, PermissionCallBack.REFUSE)
                    }
                }
            }
    }

    /**
     * 拨打电话
     *
     * @param activity        上下文
     * @param telephoneNumber 电话
     */
    fun callPhone(
        activity: FragmentActivity?,
        telephoneNumber: String?
    ) {
        if (activity.isNull() || telephoneNumber.isNullOrEmpty()) {
            return
        }
        checkPhonePermissions(activity, object : PermissionCallBack {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted(context: Context?) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:$telephoneNumber"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            }

            override fun onPermissionDenied(
                context: Context?,
                type: Int
            ) {
            }
        })
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
     * @param type    类型，1是拒绝权限，2是申请失败
     */
    fun onPermissionDenied(context: Context?, type: Int)

    companion object {
        /**
         * 拒绝权限
         */
        const val REFUSE = 1
        /**
         * 权限申请失败
         */
        const val DEFEATED = 2
    }
}