package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.account

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import com.fphoenixcorneae.ext.androidViewModel
import com.fphoenixcorneae.ext.toast
import com.fphoenixcorneae.ext.view.isVisible
import com.fphoenixcorneae.framework.widget.ProgressButton
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.ext.closeKeyboard
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidAccountBody
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_register.*

/**
 * @desc: 注册Fragment
 * @date: 2020-06-04 17:08
 */
class WanAndroidRegisterFragment : WanAndroidBaseFragment(), TextWatcher,
    View.OnFocusChangeListener, View.OnClickListener {

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
        mEtAccount.onFocusChangeListener = this
        mEtPassword.onFocusChangeListener = this
        mEtPasswordConfirm.onFocusChangeListener = this
        mIvAccountClear.setOnClickListener(this)
        mIvPasswordClear.setOnClickListener(this)
        mIvPasswordConfirmClear.setOnClickListener(this)
        mIvPasswordState.setOnClickListener(this)
        mIvPasswordConfirmState.setOnClickListener(this)
        mBtnRegister.setOnClickListener(this)
        mAccountViewModel.apply {
            // 需要观察该LiveData,否则不会执行注册接口
            mRegisterSuccess.observe(viewLifecycleOwner, {
                if (!it) {
                    mBtnRegister?.postDelayed({
                        mBtnRegister?.reset()
                    }, 500)
                }
            })
            // 需要观察该LiveData,否则不会执行登录接口
            mLoginSuccess.observe(viewLifecycleOwner, {
                mBtnRegister?.postDelayed({
                    if (it) {
                        // 登录成功,进入首页
                        mBtnRegister?.stopAnim(object : ProgressButton.OnStopAnim {
                            override fun onStop() {
                                navigateNext(R.id.mainFragment)
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
        mIvAccountClear.isVisible = mEtAccount.isFocused && mEtAccount.text.isNullOrBlank().not()
        mIvPasswordClear.isVisible = mEtPassword.isFocused && mEtPassword.text.isNullOrBlank().not()
        mIvPasswordConfirmClear.isVisible =
            mEtPasswordConfirm.isFocused && mEtPasswordConfirm.text.isNullOrBlank().not()
        mBtnRegister.apply {
            isEnabled =
                mEtAccount.text.isNullOrBlank().not()
                        && mEtPassword.text.isNullOrBlank().not()
                        && mEtPasswordConfirm.text.isNullOrBlank().not()
            animate().alpha(if (isEnabled) 1f else 0.2f).setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mEtAccount -> mIvAccountClear.isVisible =
                hasFocus && mEtAccount.text.isNullOrBlank().not()
            mEtPassword -> {
                mIvPasswordClear.isVisible = hasFocus && mEtPassword.text.isNullOrBlank().not()
                mIvPasswordState.isVisible = hasFocus
            }
            mEtPasswordConfirm -> {
                mIvPasswordConfirmClear.isVisible =
                    hasFocus && mEtPasswordConfirm.text.isNullOrBlank().not()
                mIvPasswordConfirmState.isVisible = hasFocus
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvAccountClear -> mEtAccount.editableText.clear()
            mIvPasswordClear -> mEtPassword.editableText.clear()
            mIvPasswordConfirmClear -> mEtPasswordConfirm.editableText.clear()
            mIvPasswordState -> {
                when {
                    // 隐藏密码
                    v!!.isSelected -> mEtPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    // 显示密码
                    else -> mEtPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
                v.isSelected = !v.isSelected
                mEtPassword.setSelection(mEtPassword.text.toString().length)
            }
            mIvPasswordConfirmState -> {
                when {
                    // 隐藏密码
                    v!!.isSelected -> mEtPasswordConfirm.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    // 显示密码
                    else -> mEtPasswordConfirm.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                }
                v.isSelected = !v.isSelected
                mEtPasswordConfirm.setSelection(mEtPasswordConfirm.text.toString().length)
            }
            mBtnRegister -> {
                val username = mEtAccount.text.toString()
                val password = mEtPassword.text.toString()
                val rePassword = mEtPasswordConfirm.text.toString()
                when {
                    username.length < 6 -> {
                        toast(ResourceUtil.getString(R.string.wan_android_register_tips_account_length))
                    }
                    password.length < 6 -> {
                        toast(ResourceUtil.getString(R.string.wan_android_register_tips_password_length))
                    }
                    password != rePassword -> {
                        toast(ResourceUtil.getString(R.string.wan_android_register_tips_password_incomformity))
                    }
                    else -> {
                        // 隐藏软键盘
                        mContext.closeKeyboard()
                        // 注册
                        mBtnRegister.startAnim()
                        val accountBody = WanAndroidAccountBody(username, password)
                        mAccountViewModel.register(accountBody)
                    }
                }
            }
        }
    }
}