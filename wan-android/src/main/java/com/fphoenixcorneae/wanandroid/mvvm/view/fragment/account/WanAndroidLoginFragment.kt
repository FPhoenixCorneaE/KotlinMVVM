package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.account

import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import com.fphoenixcorneae.ext.androidViewModel
import com.fphoenixcorneae.ext.view.isVisible
import com.fphoenixcorneae.ext.navigateUp
import com.fphoenixcorneae.framework.widget.ProgressButton
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.util.SpannableStringUtil
import com.fphoenixcorneae.ext.closeKeyboard
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidAccountBody
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_login.*

/**
 * @desc: 登录Fragment
 * @date: 2020-06-02 17:56
 */
class WanAndroidLoginFragment : WanAndroidBaseFragment(), TextWatcher, View.OnFocusChangeListener,
    View.OnClickListener {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_login

    override fun initView() {
        val noAccountStr = ResourceUtil.getString(R.string.wan_android_no_account)
        mBtnNoAccount.movementMethod = LinkMovementMethod.getInstance()
        mBtnNoAccount.text = SpannableStringUtil.Builder()
            .append(noAccountStr.substring(0..5))
            .setFlag(Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            .setForegroundColor(ResourceUtil.getColor(R.color.wan_android_color_darker_gray))
            .append(noAccountStr.substring(6))
            .setFlag(Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            .setForegroundColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
            .setUnderline()
            .setClickSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // 没有账号,去注册
                    navigateNext(R.id.registerFragment)
                }
            })
            .create()
        mBtnLogin.apply {
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
        mEtAccount.onFocusChangeListener = this
        mEtPassword.onFocusChangeListener = this
        mIvAccountClear.setOnClickListener(this)
        mIvPasswordClear.setOnClickListener(this)
        mIvPasswordState.setOnClickListener(this)
        mBtnLogin.setOnClickListener(this)
        mAccountViewModel.apply {
            // 需要观察该LiveData,否则不会执行登录接口
            mLoginSuccess.observe(viewLifecycleOwner, {
                mBtnLogin?.postDelayed({
                    if (it) {
                        // 登录成功,进入首页
                        mBtnLogin?.stopAnim(object : ProgressButton.OnStopAnim {
                            override fun onStop() {
                                navigateUp()
                            }
                        })
                    } else {
                        mBtnLogin?.reset()
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
        mBtnLogin.apply {
            isEnabled =
                mEtAccount.text.isNullOrBlank().not()
                        && mEtPassword.text.isNullOrBlank().not()
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
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvAccountClear -> mEtAccount.editableText.clear()
            mIvPasswordClear -> mEtPassword.editableText.clear()
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
            mBtnLogin -> {
                // 隐藏软键盘
                mContext.closeKeyboard()
                val username = mEtAccount.text.toString()
                val password = mEtPassword.text.toString()
                // 登录
                mBtnLogin.startAnim()
                val accountBody = WanAndroidAccountBody(username, password)
                mAccountViewModel.login(accountBody)
            }
        }
    }
}