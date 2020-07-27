package com.wkz.wanandroid.mvvm.view.fragment.account

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.wkz.extension.androidViewModel
import com.wkz.extension.navigate
import com.wkz.extension.showToast
import com.wkz.framework.widget.ProgressButton
import com.wkz.util.KeyboardUtil
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidAccountBody
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_register.*

/**
 * @desc: 注册Fragment
 * @date: 2020-06-04 17:08
 */
class WanAndroidRegisterFragment : WanAndroidBaseFragment(), TextWatcher {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_register

    override fun initView() {
        mBtnRegister.apply {
            isEnabled = false
            alpha = 0.2f
        }
    }

    override fun lazyLoadData() {

    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
        mEtAccount.addTextChangedListener(this)
        mEtPassword.addTextChangedListener(this)
        mEtPasswordConfirm.addTextChangedListener(this)
        mBtnRegister.setOnClickListener {
            val username = mEtAccount.text.toString()
            val password = mEtPassword.text.toString()
            val rePassword = mEtPasswordConfirm.text.toString()
            if (username.length < 6) {
                showToast(ResourceUtil.getString(R.string.wan_android_register_tips_account_length))
                return@setOnClickListener
            }
            if (password.length < 6) {
                showToast(ResourceUtil.getString(R.string.wan_android_register_tips_password_length))
                return@setOnClickListener
            }
            if (password != rePassword) {
                showToast(ResourceUtil.getString(R.string.wan_android_register_tips_password_incomformity))
                return@setOnClickListener
            }
            // 隐藏软键盘
            KeyboardUtil.closeKeyboard(mContext)
            // 注册
            mBtnRegister.startAnim()
            val accountBody = WanAndroidAccountBody(username, password)
            mAccountViewModel.register(accountBody)
        }
        mAccountViewModel.apply {
            // 需要观察该LiveData,否则不会执行注册接口
            mRegisterSuccess.observe(mContext, Observer {
                if (!it) {
                    mBtnRegister?.postDelayed({
                        mBtnRegister?.reset()
                    }, 500)
                }
            })
            // 需要观察该LiveData,否则不会执行登录接口
            mLoginSuccess.observe(mContext, Observer {
                mBtnRegister?.postDelayed({
                    if (it) {
                        // 登录成功,进入首页
                        mBtnRegister?.stopAnim(object : ProgressButton.OnStopAnim {
                            override fun onStop() {
                                navigate(R.id.mRegisterToMain)
                            }
                        })
                    } else {
                        mBtnRegister?.reset()
                    }
                }, 500)
            })
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mBtnRegister.apply {
            isEnabled =
                mEtAccount.text.isNullOrBlank().not()
                        && mEtPassword.text.isNullOrBlank().not()
                        && mEtPasswordConfirm.text.isNullOrBlank().not()
            alpha = if (isEnabled) 1f else 0.2f
        }
    }
}