package com.wkz.wanandroid.mvvm.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.wkz.extension.viewModel
import com.wkz.framework.base.activity.BaseActivity
import com.wkz.framework.widget.ProgressButton
import com.wkz.util.IntentUtil
import com.wkz.util.ResourceUtil
import com.wkz.util.SpannableStringUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidAccountBody
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_activity_login.*

/**
 * @desc: 登录Activity
 * @date: 2020-02-22 17:08
 */
class WanAndroidLoginActivity : BaseActivity(), TextWatcher {

    private val mAccountViewModel by viewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_activity_login

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
                    prepareCall(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                        if (result?.resultCode == Activity.RESULT_OK) {
                            finish()
                        }
                    }.launch(Intent(mContext, WanAndroidRegisterActivity::class.java))
                }
            })
            .create()
        mBtnLogin.apply {
            isEnabled = false
            alpha = 0.2f
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mEtAccount.addTextChangedListener(this)
        mEtPassword.addTextChangedListener(this)
        mBtnLogin.setOnClickListener {
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
                mBtnLogin.postDelayed({
                    if (it) {
                        // 登录成功,进入首页
                        mBtnLogin?.stopAnim(object : ProgressButton.OnStopAnim {
                            override fun onStop() {
                                IntentUtil.startActivity(
                                    mContext,
                                    WanAndroidHomeActivity::class.java
                                )
                                finish()
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