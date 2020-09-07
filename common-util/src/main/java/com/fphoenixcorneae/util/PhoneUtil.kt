package com.fphoenixcorneae.util

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.util.Xml
import androidx.annotation.RequiresPermission
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * 电话工具类
 */
class PhoneUtil private constructor() {
    companion object {
        /**
         * Return whether the device is phone.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isPhone: Boolean
            get() {
                val tm = telephonyManager
                return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
            }

        /**
         * Return the unique device id.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the unique device id
         */
        @get:RequiresPermission(permission.READ_PHONE_STATE)
        @get:SuppressLint("HardwareIds")
        val deviceId: String
            get() {
                if (Build.VERSION.SDK_INT >= 29) {
                    return ""
                }
                val tm = telephonyManager
                val deviceId = tm.deviceId
                if (!TextUtils.isEmpty(deviceId)) return deviceId
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val imei = tm.imei
                    if (!TextUtils.isEmpty(imei)) return imei
                    val meid = tm.meid
                    return if (TextUtils.isEmpty(meid)) "" else meid
                }
                return ""
            }

        /**
         * Return the serial of device.
         *
         * @return the serial of device
         */
        @get:RequiresPermission(permission.READ_PHONE_STATE)
        @get:SuppressLint("HardwareIds")
        val serial: String
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL

        /**
         * Return the IMEI.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the IMEI
         */
        @get:RequiresPermission(permission.READ_PHONE_STATE)
        val iMEI: String?
            get() = getImeiOrMeid(true)

        /**
         * Return the MEID.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the MEID
         */
        @get:RequiresPermission(permission.READ_PHONE_STATE)
        val mEID: String?
            get() = getImeiOrMeid(false)

        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        fun getImeiOrMeid(isImei: Boolean): String? {
            if (Build.VERSION.SDK_INT >= 29) {
                return ""
            }
            val tm = telephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return if (isImei) {
                    getMinOne(tm.getImei(0), tm.getImei(1))
                } else {
                    getMinOne(tm.getMeid(0), tm.getMeid(1))
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val ids =
                    getSystemPropertyByReflect(if (isImei) "ril.gsm.imei" else "ril.cdma.meid")
                if (!TextUtils.isEmpty(ids)) {
                    val idArr = ids.split(",").toTypedArray()
                    return if (idArr.size == 2) {
                        getMinOne(idArr[0], idArr[1])
                    } else {
                        idArr[0]
                    }
                }
                var id0 = tm.deviceId
                var id1 = ""
                try {
                    val method =
                        tm.javaClass.getMethod("getDeviceId", Int::class.javaPrimitiveType)
                    id1 = method.invoke(
                        tm,
                        if (isImei) TelephonyManager.PHONE_TYPE_GSM else TelephonyManager.PHONE_TYPE_CDMA
                    ) as String
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
                if (isImei) {
                    if (id0 != null && id0.length < 15) {
                        id0 = ""
                    }
                    if (id1.length < 15) {
                        id1 = ""
                    }
                } else {
                    if (id0 != null && id0.length == 14) {
                        id0 = ""
                    }
                    if (id1.length == 14) {
                        id1 = ""
                    }
                }
                return getMinOne(id0, id1)
            } else {
                val deviceId = tm.deviceId
                if (isImei) {
                    if (deviceId != null && deviceId.length >= 15) {
                        return deviceId
                    }
                } else {
                    if (deviceId != null && deviceId.length == 14) {
                        return deviceId
                    }
                }
            }
            return ""
        }

        private fun getMinOne(s0: String?, s1: String?): String? {
            val empty0 = TextUtils.isEmpty(s0)
            val empty1 = TextUtils.isEmpty(s1)
            if (empty0 && empty1) return ""
            if (!empty0 && !empty1) {
                return if (s0!! <= s1!!) {
                    s0
                } else {
                    s1
                }
            }
            return if (!empty0) s0 else s1
        }

        private fun getSystemPropertyByReflect(key: String): String {
            try {
                @SuppressLint("PrivateApi")
                val clz =
                    Class.forName("android.os.SystemProperties")
                val getMethod =
                    clz.getMethod("get", String::class.java, String::class.java)
                return getMethod.invoke(clz, key, "") as String
            } catch (e: Exception) { /**/
            }
            return ""
        }

        /**
         * Return the IMSI.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the IMSI
         */
        @get:RequiresPermission(permission.READ_PHONE_STATE)
        @get:SuppressLint("HardwareIds")
        val iMSI: String
            get() = telephonyManager.subscriberId

        /**
         * Returns the current phone type.
         *
         * @return the current phone type
         *
         *  * [TelephonyManager.PHONE_TYPE_NONE]
         *  * [TelephonyManager.PHONE_TYPE_GSM]
         *  * [TelephonyManager.PHONE_TYPE_CDMA]
         *  * [TelephonyManager.PHONE_TYPE_SIP]
         *
         */
        val phoneType: Int
            get() {
                val tm = telephonyManager
                return tm.phoneType
            }

        /**
         * Return whether sim card state is ready.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isSimCardReady: Boolean
            get() {
                val tm = telephonyManager
                return tm.simState == TelephonyManager.SIM_STATE_READY
            }

        /**
         * Return the sim operator name.
         *
         * @return the sim operator name
         */
        val simOperatorName: String
            get() {
                val tm = telephonyManager
                return tm.simOperatorName
            }

        /**
         * Return the sim operator using mnc.
         *
         * @return the sim operator
         */
        val simOperatorByMnc: String
            get() {
                val tm = telephonyManager
                val operator = tm.simOperator ?: return ""
                return when (operator) {
                    "46000", "46002", "46007", "46020" -> "中国移动"
                    "46001", "46006", "46009" -> "中国联通"
                    "46003", "46005", "46011" -> "中国电信"
                    else -> operator
                }
            }

        /**
         * 跳至拨号界面
         * Skip to dial.
         *
         * @param phoneNumber The phone number.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        fun dial(phoneNumber: String): Boolean {
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            if (isIntentAvailable(intent)) {
                ContextUtil.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        /**
         * 拨打电话
         * Make a phone call.
         *
         * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
         *
         * @param phoneNumber The phone number.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        @RequiresPermission(permission.CALL_PHONE)
        fun call(phoneNumber: String): Boolean {
            val intent =
                Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            if (isIntentAvailable(intent)) {
                ContextUtil.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        /**
         * 跳至发送短信界面
         * Send sms.
         *
         * @param phoneNumber The phone number.
         * @param content     The content.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        fun sendSms(phoneNumber: String, content: String?): Boolean {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            if (isIntentAvailable(intent)) {
                intent.putExtra("sms_body", content)
                ContextUtil.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        /**
         * 发送短信
         *
         * 需添加权限 `<uses-permission android:name="android.permission.SEND_SMS"/>`
         *
         * @param phoneNumber 接收号码
         * @param content     短信内容
         */
        @RequiresPermission(permission.SEND_SMS)
        fun sendSmsSilent(phoneNumber: String?, content: String) {
            if (content.isEmpty()) return
            val sentIntent =
                PendingIntent.getBroadcast(ContextUtil.context, 0, Intent(), 0)
            val smsManager = SmsManager.getDefault()
            if (content.length >= 70) {
                val ms: List<String> = smsManager.divideMessage(content)
                for (str in ms) {
                    smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
                }
            } else {
                smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
            }
        }

        /**
         * 获取手机联系人
         *
         * 需添加权限 `<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.READ_CONTACTS"/>`
         *
         * @return 联系人链表
         */
        @RequiresPermission(
            allOf = [
                permission.READ_EXTERNAL_STORAGE,
                permission.READ_CONTACTS
            ]
        )
        fun getAllContactInfo(): List<HashMap<String, String>>? {
            SystemClock.sleep(3000)
            val list =
                ArrayList<HashMap<String, String>>()
            // 1.获取内容解析者
            val resolver: ContentResolver = ContextUtil.context.contentResolver
            // 2.获取内容提供者的地址:com.android.contacts
            // raw_contacts表的地址 :raw_contacts
            // view_data表的地址 : data
            // 3.生成查询地址
            val rawUri =
                Uri.parse("content://com.android.contacts/raw_contacts")
            val dateUri =
                Uri.parse("content://com.android.contacts/data")
            // 4.查询操作,先查询raw_contacts,查询contact_id
            // projection : 查询的字段
            val cursor =
                resolver.query(rawUri, arrayOf("contact_id"), null, null, null)
            cursor.use {
                // 5.解析cursor
                if (it != null) {
                    while (it.moveToNext()) { // 6.获取查询的数据
                        val contactId = it.getString(0)
                        // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
                        // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
                        // 判断contact_id是否为空
                        if (contactId.isNotEmpty()) { //null   ""
                            // 7.根据contact_id查询view_data表中的数据
                            // selection : 查询条件
                            // selectionArgs :查询条件的参数
                            // sortOrder : 排序
                            // 空指针: 1.null.方法 2.参数为null
                            val c = resolver.query(
                                dateUri, arrayOf(
                                    "data1",
                                    "mimetype"
                                ), "raw_contact_id=?", arrayOf(contactId), null
                            )
                            val map =
                                HashMap<String, String>()
                            // 8.解析c
                            if (c != null) {
                                while (c.moveToNext()) { // 9.获取数据
                                    val data1 = c.getString(0)
                                    val mimetype = c.getString(1)
                                    // 10.根据类型去判断获取的data1数据并保存
                                    if (mimetype == "vnd.android.cursor.item/phone_v2") { // 电话
                                        map["phone"] = data1
                                    } else if (mimetype == "vnd.android.cursor.item/name") { // 姓名
                                        map["name"] = data1
                                    }
                                }
                            }
                            // 11.添加到集合中数据
                            list.add(map)
                            // 12.关闭cursor
                            c?.close()
                        }
                    }
                }
            }
            return list
        }

        /**
         * 打开手机联系人界面点击联系人后便获取该号码
         *
         * 参照以下注释代码
         */
        fun getContactNum() {
            Log.d("tips", "U should copy the following code.")
            /*
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 0);

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (data != null) {
                Uri uri = data.getData();
                String num = null;
                // 创建内容解析者
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(uri,
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    num = cursor.getString(cursor.getColumnIndex("data1"));
                }
                cursor.close();
                num = num.replaceAll("-", "");//替换的操作,555-6 -> 5556
            }
        }
        */
        }

        /**
         * 获取手机短信并保存到xml中
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.READ_SMS"/>`
         */
        fun getAllSMS() { // 1.获取短信
            // 1.1获取内容解析者
            val resolver: ContentResolver = ContextUtil.context.contentResolver
            // 1.2获取内容提供者地址   sms,sms表的地址:null  不写
            // 1.3获取查询路径
            val uri = Uri.parse("content://sms")
            // 1.4.查询操作
            // projection : 查询的字段
            // selection : 查询的条件
            // selectionArgs : 查询条件的参数
            // sortOrder : 排序
            val cursor = resolver.query(
                uri,
                arrayOf("address", "date", "type", "body"),
                null,
                null,
                null
            )
            // 设置最大进度
            val count = cursor!!.count //获取短信的个数
            // 2.备份短信
            // 2.1获取xml序列器
            val xmlSerializer = Xml.newSerializer()
            try {
                // 2.2设置xml文件保存的路径
                // os : 保存的位置
                // encoding : 编码格式
                xmlSerializer.setOutput(
                    FileOutputStream(File("/mnt/sdcard/backupsms.xml")),
                    "utf-8"
                )
                // 2.3设置头信息
                // standalone : 是否独立保存
                xmlSerializer.startDocument("utf-8", true)
                // 2.4设置根标签
                xmlSerializer.startTag(null, "smss")
                // 1.5.解析cursor
                while (cursor.moveToNext()) {
                    SystemClock.sleep(1000)
                    // 2.5设置短信的标签
                    xmlSerializer.startTag(null, "sms")
                    // 2.6设置文本内容的标签
                    xmlSerializer.startTag(null, "address")
                    val address = cursor.getString(0)
                    // 2.7设置文本内容
                    xmlSerializer.text(address)
                    xmlSerializer.endTag(null, "address")
                    xmlSerializer.startTag(null, "date")
                    val date = cursor.getString(1)
                    xmlSerializer.text(date)
                    xmlSerializer.endTag(null, "date")
                    xmlSerializer.startTag(null, "type")
                    val type = cursor.getString(2)
                    xmlSerializer.text(type)
                    xmlSerializer.endTag(null, "type")
                    xmlSerializer.startTag(null, "body")
                    val body = cursor.getString(3)
                    xmlSerializer.text(body)
                    xmlSerializer.endTag(null, "body")
                    xmlSerializer.endTag(null, "sms")
                    println("address:$address   date:$date  type:$type  body:$body")
                }
                xmlSerializer.endTag(null, "smss")
                xmlSerializer.endDocument()
                // 2.8将数据刷新到文件中
                xmlSerializer.flush()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                CloseUtil.closeIOQuietly(cursor)
            }
        }

        /**
         * 获取手机状态信息
         *
         * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
         *
         * @return DeviceId(IMEI) = 99000311726612<br></br>
         * DeviceSoftwareVersion = 00<br></br>
         * Line1Number =<br></br>
         * NetworkCountryIso = cn<br></br>
         * NetworkOperator = 46003<br></br>
         * NetworkOperatorName = 中国电信<br></br>
         * NetworkType = 6<br></br>
         * honeType = 2<br></br>
         * SimCountryIso = cn<br></br>
         * SimOperator = 46003<br></br>
         * SimOperatorName = 中国电信<br></br>
         * SimSerialNumber = 89860315045710604022<br></br>
         * SimState = 5<br></br>
         * SubscriberId(IMSI) = 460030419724900<br></br>
         * VoiceMailNumber = *86<br></br>
         */
        @RequiresPermission(permission.READ_PHONE_STATE)
        @SuppressLint("HardwareIds")
        fun getPhoneStatus(): String {
            val tm = ContextUtil.context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var str = ""
            str += "DeviceId(IMEI) = " + tm.deviceId + "\n"
            str += "DeviceSoftwareVersion = " + tm.deviceSoftwareVersion + "\n"
            str += "Line1Number = " + tm.line1Number + "\n"
            str += "NetworkCountryIso = " + tm.networkCountryIso + "\n"
            str += "NetworkOperator = " + tm.networkOperator + "\n"
            str += "NetworkOperatorName = " + tm.networkOperatorName + "\n"
            str += "NetworkType = " + tm.networkType + "\n"
            str += "PhoneType = " + tm.phoneType + "\n"
            str += "SimCountryIso = " + tm.simCountryIso + "\n"
            str += "SimOperator = " + tm.simOperator + "\n"
            str += "SimOperatorName = " + tm.simOperatorName + "\n"
            str += "SimSerialNumber = " + tm.simSerialNumber + "\n"
            str += "SimState = " + tm.simState + "\n"
            str += "SubscriberId(IMSI) = " + tm.subscriberId + "\n"
            str += "VoiceMailNumber = " + tm.voiceMailNumber + "\n"
            return str
        }

        private val telephonyManager: TelephonyManager
            get() = ContextUtil.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        private fun isIntentAvailable(intent: Intent): Boolean {
            return ContextUtil.context
                .packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size > 0
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}