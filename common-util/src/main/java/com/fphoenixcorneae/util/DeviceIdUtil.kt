package com.fphoenixcorneae.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 获取唯一设备id工具类，建议使用getPseudoUniqueID（）方法，不需申请任何权限；
 *
 * @author Administrator
 * @date 2018/5/7
 */

class DeviceIdUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        /**
         * The IMEI: 仅仅只对Android手机有效
         * 采用此种方法，需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE，并且用
         * 户应当允许安装此应用。作为手机来讲，IMEI是唯一的，它应该类似于 359881030314356（除非你有一个没有量产的手
         * 机（水货）它可能有无效的IMEI，如：0000000000000）。
         *
         * @return imei
         */
        private val imei: String
            @SuppressLint("HardwareIds", "MissingPermission")
            get() {
                val telephonyMgr = ContextUtil.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val szImei = telephonyMgr.deviceId
                return szImei
            }

        /**
         * Pseudo-Unique ID, 这个在任何Android手机中都有效
         * 有一些特殊的情况，一些如平板电脑的设置没有通话功能，或者你不愿加入READ_PHONE_STATE许可。而你仍然想获得唯
         * 一序列号之类的东西。这时你可以通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出
         * 来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）。但应当明白的是，出现类似情况的可能性基
         * 本可以忽略。大多数的Build成员都是字符串形式的，我们只取他们的长度信息。我们取到13个数字，并在前面加上“35
         * ”。这样这个ID看起来就和15位IMEI一样了。
         *
         * @return PseudoUniqueID
         */
        private//we make this look like a valid IMEI
        val pseudoUniqueID: String
            get() = "35" +
                    Build.BOARD.length % 10 +
                    Build.BRAND.length % 10 +
                    Build.CPU_ABI.length % 10 +
                    Build.DEVICE.length % 10 +
                    Build.DISPLAY.length % 10 +
                    Build.HOST.length % 10 +
                    Build.ID.length % 10 +
                    Build.MANUFACTURER.length % 10 +
                    Build.MODEL.length % 10 +
                    Build.PRODUCT.length % 10 +
                    Build.TAGS.length % 10 +
                    Build.TYPE.length % 10 +
                    Build.USER.length % 10

        /**
         * The Android ID
         * 通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个
         * Andorid手机被Root过的话，这个ID也可以被任意改变。无需任何许可。
         *
         * @return AndroidID
         */
        private val androidID: String
            @SuppressLint("HardwareIds")
            get() = Settings.Secure.getString(
                ContextUtil.context.contentResolver,
                Settings.Secure.ANDROID_ID
            )

        /**
         * The WLAN MAC Address string
         * 是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为
         * null。Returns: 00:11:22:33:44:55 (这不是一个真实的地址。而且这个地址能轻易地被伪造。).WLan不必打开，
         * 就可读取些值。
         *
         * @return m_szWLANMAC
         */
        private val wlanMacAddress: String
            @SuppressLint("HardwareIds", "MissingPermission")
            get() {
                val wm =
                    ContextUtil.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                var mSzWLANMAC = ""
                if (wm != null) {
                    mSzWLANMAC = wm.connectionInfo.macAddress
                }
                return mSzWLANMAC
            }

        /**
         * 只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.Returns: 43:25:78:50:93:38 .
         * 蓝牙没有必要打开，也能读取。
         *
         * @return m_szBTMAC
         */
        private// Local Bluetooth adapter
        val btmacAddress: String
            @SuppressLint("HardwareIds", "MissingPermission")
            get() {
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                return mBluetoothAdapter.address
            }

        /**
         * Combined Device ID
         * 综上所述，我们一共有五种方式取得设备的唯一标识。它们中的一些可能会返回null，或者由于硬件缺失、权限问题等
         * 获取失败。但你总能获得至少一个能用。所以，最好的方法就是通过拼接，或者拼接后的计算出的MD5值来产生一个结果。
         * 通过算法，可产生32位的16进制数据:9DDDF85AFF0A87974CE4541BD94D5F55
         *
         * @return Device ID
         */
        // compute md5
        // get md5 bytes
        // create a hex string
        // if it is a single digit, make sure it have 0 in front (proper padding)
        // add number to string
        // hex string to uppercase
        val uniqueID: String
            get() {
                val mSzLongID = pseudoUniqueID + wlanMacAddress + btmacAddress
                var m: MessageDigest? = null
                try {
                    m = MessageDigest.getInstance("MD5")
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }

                assert(m != null)
                m!!.update(mSzLongID.toByteArray(), 0, mSzLongID.length)
                val pMd5Data = m.digest()
                var mSzUniqueID = StringBuilder()
                for (i in pMd5Data.indices) {
                    val b = 0xFF and pMd5Data[i].toInt()
                    if (b <= 0xF) {
                        mSzUniqueID.append("0")
                    }
                    mSzUniqueID.append(Integer.toHexString(b))
                }
                mSzUniqueID = StringBuilder(mSzUniqueID.toString().toUpperCase())
                return mSzUniqueID.toString()
            }
    }
}
