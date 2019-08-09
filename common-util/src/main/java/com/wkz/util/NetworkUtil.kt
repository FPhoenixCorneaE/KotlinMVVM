package com.wkz.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils

import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections
import java.util.Locale
import java.util.Properties

/**
 * 网络信息工具类
 */
class NetworkUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        private val ETHERNET = "eth0"
        private val WLAN = "wlan0"
        private val DNS1 = "[net.dns1]"
        private val DNS2 = "[net.dns2]"
        private val ETHERNET_GATEWAY = "[dhcp.eth0.gateway]"
        private val WLAN_GATEWAY = "[dhcp.wlan0.gateway]"
        private val ETHERNET_MASK = "[dhcp.eth0.mask]"
        private val WLAN_MASK = "[dhcp.wlan0.mask]"

        /**
         * 判断网络是否可用
         * Judge whether current network is available
         */
        val isNetworkAvailable: Boolean
            get() {
                val mConnectivityManager =
                    ContextUtil.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var info: NetworkInfo? = null
                if (mConnectivityManager != null) {
                    info = mConnectivityManager.activeNetworkInfo
                }
                return info != null && info.isAvailable
            }

        /**
         * 判断WIFI网络是否可用
         */
        val isWifiConnected: Boolean
            get() {
                val mConnectivityManager =
                    ContextUtil.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var networkInfo: NetworkInfo? = null
                if (mConnectivityManager != null) {
                    networkInfo = mConnectivityManager.activeNetworkInfo
                }
                return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI && networkInfo.isAvailable
            }

        /**
         * 判断MOBILE网络是否可用
         */
        val isMobileConnected: Boolean
            get() {
                val mConnectivityManager =
                    ContextUtil.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var networkInfo: NetworkInfo? = null
                if (mConnectivityManager != null) {
                    networkInfo = mConnectivityManager.activeNetworkInfo
                }
                return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_MOBILE && networkInfo.isAvailable
            }

        /**
         * 获取当前网络连接的类型信息
         */
        val connectedType: Int
            get() {
                val mConnectivityManager =
                    ContextUtil.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var mNetworkInfo: NetworkInfo? = null
                if (mConnectivityManager != null) {
                    mNetworkInfo = mConnectivityManager.activeNetworkInfo
                }
                return if (mNetworkInfo != null && mNetworkInfo.isAvailable) mNetworkInfo.type else -1
            }

        /**
         * Get local ipv4 address
         */
        val localIPv4: String
            get() = getLocalIp(true)

        /**
         * Get local ipv6 address
         */
        val localIPv6: String
            get() = getLocalIp(false)

        /**
         * Get local ip address
         */
        private fun getLocalIp(useIPv4: Boolean): String {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val nif = en.nextElement()
                    val inet = nif.inetAddresses
                    while (inet.hasMoreElements()) {
                        val addr = inet.nextElement()
                        if (!addr.isLoopbackAddress) {
                            val ip = addr.hostAddress.toUpperCase(Locale.getDefault())
                            val isIPv4 = addr is Inet4Address
                            if (useIPv4) {
                                if (isIPv4) {
                                    return ip
                                }
                            } else {
                                if (!isIPv4) {
                                    val delim = ip.indexOf('%')
                                    return if (delim < 0) ip else ip.substring(0, delim)
                                }
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                com.orhanobut.logger.Logger.e(e.toString())
            }

            return ""
        }

        /**
         * Get wlan mac address
         */
        val wlanMacAddress: String
            get() = getMacAddress(WLAN)

        /**
         * Get ethernet mac address
         */
        val ethernetMacAddress: String
            get() = getMacAddress(ETHERNET)

        /**
         * Get mac address
         */
        private fun getMacAddress(interfaceName: String?): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    if (interfaceName != null) {
                        if (!intf.name.equals(interfaceName, ignoreCase = true)) {
                            continue
                        }
                    }
                    val mac: ByteArray?
                    mac = intf.hardwareAddress
                    if (mac == null) {
                        return ""
                    }
                    val buf = StringBuilder()
                    for (aMac in mac) {
                        buf.append(String.format("%02X:", aMac))
                    }
                    if (buf.length > 0) {
                        buf.deleteCharAt(buf.length - 1)
                    }
                    return buf.toString()
                }
            } catch (e: SocketException) {
                com.orhanobut.logger.Logger.e(e.toString())
            }

            return ""
        }

        /**
         * Get dns1
         */
        val dnS1: String?
            get() = getPropInfo(DNS1)

        /**
         * Get dns2
         */
        val dnS2: String?
            get() = getPropInfo(DNS2)

        /**
         * Get ethernet gateway
         */
        val ethernetGateway: String?
            get() = getPropInfo(ETHERNET_GATEWAY)

        /**
         * Get wlan gateway
         */
        val wlanGateway: String?
            get() = getPropInfo(WLAN_GATEWAY)

        /**
         * Get ethernet mask
         */
        val ethernetMask: String?
            get() = getPropInfo(ETHERNET_MASK)

        /**
         * Get wlan mask
         */
        val wlanMask: String?
            get() = getPropInfo(WLAN_MASK)

        /**
         * Get prop information by different interface name
         */
        private fun getPropInfo(interfaceName: String): String? {
            var re = ""
            try {
                val process = Runtime.getRuntime().exec("getprop")
                val pr = Properties()
                pr.load(process.inputStream)
                re = pr.getProperty(interfaceName, "")
                if (!TextUtils.isEmpty(re) && re.length > 6) {
                    re = re.substring(1, re.length - 1)
                    return re
                }
            } catch (e: IOException) {
                com.orhanobut.logger.Logger.e(e.toString())
            }

            return re
        }
    }
}
