package com.wkz.wanandroid.mvvm.view.fragment

import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.Observer
import com.wkz.extension.androidViewModel
import com.wkz.extension.navigate
import com.wkz.extension.navigateUp
import com.wkz.framework.widget.ProgressButton
import com.wkz.util.KeyboardUtil
import com.wkz.util.ResourceUtil
import com.wkz.util.SpannableStringUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidAccountBody
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_login.*

/**
 * @desc: 登录Fragment
 * @date: 2020-06-02 17:56
 */
class WanAndroidLoginFragment : WanAndroidBaseFragment(), TextWatcher {

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
                    navigate(R.id.mLoginToRegister)
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
        mBtnLogin.setOnClickListener {
            // 隐藏软键盘
            KeyboardUtil.closeKeyboard(mContext)
            val username = mEtAccount.text.toString()
            val password = mEtPassword.text.toString()
            // 登录
            mBtnLogin.startAnim()
            val accountBody = WanAndroidAccountBody(username, password)
            mAccountViewModel.login(accountBody)
        }
        mAccountViewModel.apply {
            // 需要观察该LiveData,否则不会执行登录接口
            mLoginSuccess.observe(mContext, Observer {
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
        mBtnLogin.apply {
            isEnabled =
                mEtAccount.text.isNullOrBlank().not()
                        && mEtPassword.text.isNullOrBlank().not()
            alpha = if (isEnabled) 1f else 0.2f
        }
    }
}