package com.wkz.wanandroid.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.extension.showToast
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.model.WanAndroidAccountBody
import com.wkz.wanandroid.mvvm.model.WanAndroidUserInfoBean

/**
 *  @desc: 账号登录、注册ViewModel
 *  @date: 2020-04-23 10:54
 */
class WanAndroidAccountViewModel(application: Application) :
    WanAndroidBaseShareViewModel(application) {
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
    var mLoginSuccess: MutableLiveData<Boolean> =
        Transformations.switchMap(mLoginAccountBody) { accountBody ->
            Transformations.map(
                sWanAndroidService.login(
                    accountBody.username,
                    accountBody.password
                )
            ) {
                val loginSuccess = it.errorCode != -1 && it.errorCode != 1000
                if (loginSuccess) {
                    // 登录成功,保存用户信息
                    WanAndroidUserManager.setUserInfo(it.data ?: WanAndroidUserInfoBean())
                } else {
                    // 登录失败,提示失败信息
                    showToast(it.errorMsg)
                }
                loginSuccess
            }
        } as MutableLiveData<Boolean>

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

    init {
        // 初始化登录成功状态
        mLoginSuccess.value = WanAndroidUserManager.sHasLoggedOn
    }
}