package com.fphoenixcorneae.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import android.view.View
import androidx.annotation.AnimRes
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fphoenixcorneae.ext.isNull
import java.io.File
import java.io.Serializable

/**
 * Intent操作
 */
object IntentUtil {

    /**
     * @param enterAnim 进入动画
     * @param exitAnim  退出动画
     */
    fun startActivity(
        context: Context,
        className: Class<*>,
        bundle: Bundle? = null,
        requestCode: Int = -1,
        @AnimRes enterAnim: Int = 0,
        @AnimRes exitAnim: Int = 0
    ) {
        val intent = Intent()
        intent.setClass(context, className)
        intent.putExtras(BundleBuilder.of(bundle).get())
        val optionsBundle =
            ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle()
        if (requestCode < 0) {
            ActivityCompat.startActivity(
                context,
                intent,
                optionsBundle
            )
        } else {
            scanForActivity(context)?.startActivityForResult(
                intent, requestCode,
                optionsBundle
            )
        }
        scanForActivity(context)?.overridePendingTransition(enterAnim, exitAnim)
    }

    /**
     * 添加共享元素
     * @param views 共享元素,必须设置"android:transitionName"属性
     */
    fun startActivity(
        activity: Activity,
        className: Class<*>,
        bundle: Bundle? = null,
        requestCode: Int = -1,
        vararg views: View
    ) {
        val intent = Intent()
        intent.setClass(activity, className)
        intent.putExtras(BundleBuilder.of(bundle).get())
        var optionsBundle: Bundle? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val sharedElements = ArrayList<Pair<View, String>>(views.size)
            for ((index, view) in views.withIndex()) {
                sharedElements.add(Pair.create(view, view.transitionName))
            }
            optionsBundle =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    *sharedElements.toTypedArray()
                )
                    .toBundle()
        }
        if (requestCode < 0) {
            ActivityCompat.startActivity(
                activity,
                intent,
                optionsBundle
            )
        } else {
            ActivityCompat.startActivityForResult(
                activity,
                intent,
                requestCode,
                optionsBundle
            )
        }
    }

    /**
     * 根据action启动activity
     * @param action 动作
     */
    fun startActivity(
        context: Context?,
        action: String?,
        bundle: Bundle? = null,
        requestCode: Int = -1
    ) {
        val intent = Intent(action)
        intent.putExtras(BundleBuilder.of(bundle).get())
        if (requestCode < 0) {
            context?.startActivity(intent)
        } else {
            scanForActivity(context)?.startActivityForResult(intent, requestCode)
        }
    }

    /**
     * 启动服务
     * @param context     上下文
     * @param serviceName 服务名字
     */
    fun startService(
        context: Context,
        serviceName: Class<*>,
        bundle: Bundle? = null
    ) {
        val intent = Intent()
        intent.setClass(context, serviceName)
        intent.putExtras(BundleBuilder.of(bundle).get())
        context.startService(intent)
    }

    /**
     * 通过包名打开软件
     */
    fun startAppByPackageName(context: Context?, appPackageName: String?) {
        if (context.isNull() || appPackageName.isNullOrEmpty()) {
            return
        }
        val intent = context!!.packageManager.getLaunchIntentForPackage(appPackageName)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun getInstallAppIntent(
        file: File,
        isNewTask: Boolean = false
    ): Intent? {
        val intent = Intent(Intent.ACTION_VIEW)
        val data: Uri
        val type = "application/vnd.android.package-archive"
        val context = ContextUtil.context
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file)
        } else {
            val authority = "${AppUtil.packageName}.FileProvider"
            data = FileProvider.getUriForFile(context, authority, file)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.grantUriPermission(
            AppUtil.packageName,
            data,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        intent.setDataAndType(data, type)
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }


    fun getUninstallAppIntent(
        packageName: String,
        isNewTask: Boolean = false
    ): Intent? {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }

    fun getLaunchAppIntent(
        packageName: String,
        isNewTask: Boolean = false
    ): Intent? {
        val launcherActivity: String = ActivityUtil.getLauncherActivity(packageName)
        if (launcherActivity.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val cn = ComponentName(packageName, launcherActivity)
            intent.component = cn
            return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
        }
        return null
    }

    /**
     * Search a word in a browser
     */
    fun search(context: Context?, string: String?) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, string)
        context?.startActivity(intent)
    }

    /**
     * Open url in a browser
     */
    fun openUrl(context: Context?, url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context?.startActivity(intent)
    }

    /**
     * Open map in a map app
     */
    fun openMap(context: Context?, parh: String?) {
        if (parh.isNullOrEmpty()) {
            return
        }
        val uri = Uri.parse(parh)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context?.startActivity(intent)
    }

    /**
     * Open dial
     */
    fun openDial(context: Context?) {
        val intent = Intent(Intent.ACTION_CALL_BUTTON)
        context?.startActivity(intent)
    }

    /**
     * Open dial with a number
     */
    fun openDial(context: Context?, number: String) {
        val uri = Uri.parse("tel:$number")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        context?.startActivity(intent)
    }

    /**
     * Call up, requires Permission "android.permission.CALL_PHONE"
     */
    fun callPhone(
        activity: FragmentActivity?,
        telephoneNumber: String?
    ) {
        if (activity.isNull() || telephoneNumber.isNullOrEmpty()) {
            return
        }
        PermissionUtil.requestPhonePermission(activity, object : PermissionCallBack {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted(context: Context?) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:$telephoneNumber"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            }

            override fun onPermissionDenied(context: Context?, type: Int) {
                when (type) {
                    PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION -> {
                        openApplicationDetailsSettings()
                    }
                }
            }
        })
    }

    /**
     * Send message
     */
    fun sendMessage(
        context: Context,
        sendNo: String,
        sendContent: String?
    ) {
        val uri = Uri.parse("smsto:$sendNo")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", sendContent)
        context.startActivity(intent)
    }

    /**
     * Send Text Message
     * @param destinationAddress 接收方手机号码
     * @param textMessage 信息内容
     */
    fun sendTextMessage(
        activity: FragmentActivity?,
        destinationAddress: String,
        textMessage: String
    ) {
        PermissionUtil.requestPermission(activity, object : PermissionCallBack {
            /**
             * 申请权限成功
             *
             * @param context 上下文
             */
            override fun onPermissionGranted(context: Context?) {
                when {
                    destinationAddress.isNotBlank() -> {
                        // 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供的短信工具
                        val sms = SmsManager.getDefault()
                        // 如果短信没有超过限制长度，则返回一个长度的List
                        val texts = sms.divideMessage(textMessage)
                        texts.forEach {
                            sms.sendTextMessage(destinationAddress, null, it, null, null)
                        }
                    }
                }
            }

            /**
             * 申请权限失败
             *
             * @param context 上下文
             * @param type    类型，1是拒绝权限，2是申请失败
             */
            override fun onPermissionDenied(context: Context?, type: Int) {
                when (type) {
                    PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION -> {
                        openApplicationDetailsSettings()
                    }
                }
            }
        }, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)
    }

    /**
     * Open contact person
     */
    fun openContacts(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
        context.startActivity(intent)
    }

    /**
     * Open system settings
     *
     * @param action The action contains global system-level device preferences.
     */
    fun openSettings(action: String? = Settings.ACTION_SETTINGS) {
        val intent = Intent(action)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextUtil.context.startActivity(intent)
    }

    /**
     * 打开悬浮窗设置页面
     */
    @SuppressLint("InlinedApi")
    fun openSettingsCanDrawOverlays() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${AppUtil.packageName}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextUtil.context.startActivity(intent)
    }

    /**
     * 打开应用修改系统设置页面
     */
    @SuppressLint("InlinedApi")
    fun openApplicationManageWriteSettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:${AppUtil.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextUtil.context.startActivity(intent)
    }

    /**
     * Open App Detail page
     */
    fun openApplicationDetailsSettings() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", AppUtil.packageName, null)
        } else {
            val appPkgName =
                when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.FROYO -> "pkg"
                    else -> "com.android.settings.ApplicationPkgName"
                }
            intent.action = Intent.ACTION_VIEW
            intent.setClassName(
                "com.android.settings",
                "com.android.settings.InstalledAppDetails"
            )
            intent.putExtra(appPkgName, AppUtil.packageName)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextUtil.context.startActivity(intent)
    }

    /**
     * Open camera
     */
    fun openCamera(activity: FragmentActivity?) {
        PermissionUtil.requestCameraPermission(activity, object : PermissionCallBack {
            /**
             * 申请权限成功
             *
             * @param context 上下文
             */
            override fun onPermissionGranted(context: Context?) {
                startActivity(context, MediaStore.ACTION_IMAGE_CAPTURE)
            }

            /**
             * 申请权限失败
             *
             * @param context 上下文
             * @param type    类型，1是拒绝权限，2是申请失败
             */
            override fun onPermissionDenied(context: Context?, type: Int) {
                when (type) {
                    PermissionCallBack.STOP_ASKING_AFTER_PROHIBITION -> {
                        openApplicationDetailsSettings()
                    }
                }
            }
        })
    }

    /**
     * Take camera, this photo data will be returned in onActivityResult()
     */
    fun takeCamera(activity: Activity, requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Choose photo, this photo data will be returned in onActivityResult()
     */
    fun choosePhoto(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Get activity from context object
     *
     * @param context something
     * @return object of Activity or null if it is not Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        if (context.isNull()) {
            return null
        }
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }
}

/**
 * BundleBuilder
 */
class BundleBuilder private constructor(b: Bundle?) {

    private val bundle: Bundle = b?.let { Bundle(it) } ?: Bundle()

    /**
     * Inserts all mappings from the given Bundle into this Bundle.
     *
     * @param bundle a Bundle
     */
    fun putAll(bundle: Bundle?): BundleBuilder {
        this.bundle.putAll(bundle)
        return this
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     */
    fun putByte(key: String?, value: Byte): BundleBuilder {
        bundle.putByte(key, value)
        return this
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char, or null
     */
    fun putChar(key: String?, value: Char): BundleBuilder {
        bundle.putChar(key, value)
        return this
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     */
    fun putShort(key: String?, value: Short): BundleBuilder {
        bundle.putShort(key, value)
        return this
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     */
    fun putFloat(key: String?, value: Float): BundleBuilder {
        bundle.putFloat(key, value)
        return this
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     */
    fun putCharSequence(key: String?, value: CharSequence?): BundleBuilder {
        bundle.putCharSequence(key, value)
        return this
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     */
    fun putParcelable(key: String?, value: Parcelable?): BundleBuilder {
        bundle.putParcelable(key, value)
        return this
    }

    /**
     * Inserts a Size value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Size object, or null
     */
    @TargetApi(21)
    fun putSize(key: String?, value: Size?): BundleBuilder {
        bundle.putSize(key, value)
        return this
    }

    /**
     * Inserts a SizeF value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a SizeF object, or null
     */
    @TargetApi(21)
    fun putSizeF(key: String?, value: SizeF?): BundleBuilder {
        bundle.putSizeF(key, value)
        return this
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     */
    fun putParcelableArray(key: String?, value: Array<Parcelable?>?): BundleBuilder {
        bundle.putParcelableArray(key, value)
        return this
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     */
    fun putParcelableArrayList(
        key: String?,
        value: ArrayList<out Parcelable?>?
    ): BundleBuilder {
        bundle.putParcelableArrayList(key, value)
        return this
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     */
    fun putSparseParcelableArray(
        key: String?,
        value: SparseArray<out Parcelable?>?
    ): BundleBuilder {
        bundle.putSparseParcelableArray(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<Integer> object, or null
    </Integer></Integer> */
    fun putIntegerArrayList(key: String?, value: ArrayList<Int?>?): BundleBuilder {
        bundle.putIntegerArrayList(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<String> object, or null
    </String></String> */
    fun putStringArrayList(
        key: String?,
        value: ArrayList<String?>?
    ): BundleBuilder {
        bundle.putStringArrayList(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<CharSequence> object, or null
    </CharSequence></CharSequence> */
    @TargetApi(8)
    fun putCharSequenceArrayList(
        key: String?,
        value: ArrayList<CharSequence?>?
    ): BundleBuilder {
        bundle.putCharSequenceArrayList(key, value)
        return this
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     */
    fun putSerializable(key: String?, value: Serializable?): BundleBuilder {
        bundle.putSerializable(key, value)
        return this
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     */
    fun putByteArray(key: String?, value: ByteArray?): BundleBuilder {
        bundle.putByteArray(key, value)
        return this
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     */
    fun putShortArray(key: String?, value: ShortArray?): BundleBuilder {
        bundle.putShortArray(key, value)
        return this
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     */
    fun putCharArray(key: String?, value: CharArray?): BundleBuilder {
        bundle.putCharArray(key, value)
        return this
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     */
    fun putFloatArray(key: String?, value: FloatArray?): BundleBuilder {
        bundle.putFloatArray(key, value)
        return this
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     */
    @TargetApi(8)
    fun putCharSequenceArray(
        key: String?,
        value: Array<CharSequence?>?
    ): BundleBuilder {
        bundle.putCharSequenceArray(key, value)
        return this
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     */
    fun putBundle(key: String?, value: Bundle?): BundleBuilder {
        bundle.putBundle(key, value)
        return this
    }

    /**
     * Inserts an [IBinder] value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     *
     *
     * You should be very careful when using this function.  In many
     * places where Bundles are used (such as inside of Intent objects), the Bundle
     * can live longer inside of another process than the process that had originally
     * created it.  In that case, the IBinder you supply here will become invalid
     * when your process goes away, and no longer usable, even if a new process is
     * created for you later on.
     *
     * @param key   a String, or null
     * @param value an IBinder object, or null
     */
    @TargetApi(18)
    fun putBinder(key: String?, value: IBinder?): BundleBuilder {
        bundle.putBinder(key, value)
        return this
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Boolean, or null
     */
    fun putBoolean(key: String?, value: Boolean): BundleBuilder {
        bundle.putBoolean(key, value)
        return this
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int, or null
     */
    fun putInt(key: String?, value: Int): BundleBuilder {
        bundle.putInt(key, value)
        return this
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     */
    fun putLong(key: String?, value: Long): BundleBuilder {
        bundle.putLong(key, value)
        return this
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     */
    fun putDouble(key: String?, value: Double): BundleBuilder {
        bundle.putDouble(key, value)
        return this
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     */
    fun putString(key: String?, value: String?): BundleBuilder {
        bundle.putString(key, value)
        return this
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean array object, or null
     */
    fun putBooleanArray(key: String?, value: BooleanArray?): BundleBuilder {
        bundle.putBooleanArray(key, value)
        return this
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an int array object, or null
     */
    fun putIntArray(key: String?, value: IntArray?): BundleBuilder {
        bundle.putIntArray(key, value)
        return this
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a long array object, or null
     */
    fun putLongArray(key: String?, value: LongArray?): BundleBuilder {
        bundle.putLongArray(key, value)
        return this
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a double array object, or null
     */
    fun putDoubleArray(key: String?, value: DoubleArray?): BundleBuilder {
        bundle.putDoubleArray(key, value)
        return this
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String array object, or null
     */
    fun putStringArray(key: String?, value: Array<String?>?): BundleBuilder {
        bundle.putStringArray(key, value)
        return this
    }

    /**
     * Get the bundle.
     *
     * @return a bundle
     */
    fun get(): Bundle {
        return bundle
    }

    /**
     * Set the argument of Fragment.
     *
     * @param fragment a fragment
     * @return a fragment
     */
    fun <T : Fragment?> into(fragment: T): T {
        fragment!!.arguments = get()
        return fragment
    }

    companion object {
        /**
         * Constructs a Bundle containing a copy of the mappings from the given
         * Bundle.
         *
         * @param bundle a Bundle to be copied.
         */
        fun of(bundle: Bundle? = null): BundleBuilder {
            return BundleBuilder(bundle)
        }
    }

}