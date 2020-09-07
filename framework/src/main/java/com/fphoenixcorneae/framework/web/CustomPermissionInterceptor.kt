package com.fphoenixcorneae.framework.web

import com.just.agentweb.PermissionInterceptor
import com.orhanobut.logger.Logger
import com.fphoenixcorneae.util.gson.GsonUtil

class CustomPermissionInterceptor : PermissionInterceptor {
    /**
     * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
     * @param url
     * @param permissions
     * @param action
     * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
     */
    override fun intercept(
        url: String,
        permissions: Array<String>,
        action: String
    ): Boolean {
        Logger.i(
            "mUrl:" + url + "  permission:" + GsonUtil.toJson(permissions) + " action:" + action
        )
        return false
    }
}