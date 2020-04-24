package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.extension.showToast
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.model.WanAndroidAccountBody

/**
 *  @desc: 账号登录、注册ViewModel
 *  @date: 2020-04-23 10:54
 */
class WanAndroidAccountViewModel : WanAndroidBaseViewModel() {
    /* 账户注册主体 */
    private val mRegisterAccountBody = MutableLiveData<WanAndroidAccountBody>()

    /* 账户登录主体 */
    private val mLoginAccountBody = MutableLiveData<WanAndroidAccountBody>()

    /* 注册成功 */
    val mRegisterSuccess = Transformations.switchMap(mRegisterAccountBody) { accountBody ->
        Transformations.map(
            sWanAndroidService.register(
                accountBody.username,
                accountBody.password,
                accountBody.password
            )
        ) {
            if (it.errorCode != -1) {
                // 注册成功直接登录
                mLoginAccountBody.value = mRegisterAccountBody.value
                true
            } else {
                showToast(it.errorMsg)
                false
            }
        }
    }

    /* 登录成功 */
    val mLoginSuccess =
        Transformations.switchMap(mLoginAccountBody) { accountBody ->
            Transformations.map(
                sWanAndroidService.login(
                    accountBody.username,
                    accountBody.password
                )
            ) {
                WanAndroidUserManager.setLoginStatus(it.errorCode != -1)
                if (it.errorCode == -1) {
                    // 登录失败,提示失败信息
                    showToast(it.errorMsg)
                }
                it.errorCode != -1
            }
        }

    /**
     * 注册
     */
    fun register(accountBody: WanAndroidAccountBody) {
        mRegisterAccountBody.value = accountBody
    }

    /**
     * 登录
     */
    fun login(accountBody: WanAndroidAccountBody) {
        mLoginAccountBody.value = accountBody
    }
}