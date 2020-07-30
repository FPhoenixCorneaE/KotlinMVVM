package com.wkz.framework.base.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import androidx.fragment.app.FragmentActivity
import cn.cricin.folivora.Folivora
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.rxretrofit.network.exception.ErrorStatus
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.widget.MultipleStatusView

/**
 * @desc:BaseActivity基类
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {

        //开启这个flag后，你就可以正常使用Selector这样的DrawableContainers了。
        //同时，你还开启了类似android:drawableLeft这样的compound drawable的使用权限，
        //以及RadioButton的使用权限，以及ImageView’s src属性。
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: FragmentActivity

    /** 根布局 */
    protected lateinit var mMsvRoot: MultipleStatusView

    override fun attachBaseContext(newBase: Context?) {
        // 启用Folivora
        super.attachBaseContext(Folivora.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        // 加载根布局
        mMsvRoot = LayoutInflater.from(mContext).inflate(
            R.layout.framework_layout_base, null, false
        ) as MultipleStatusView
        setContentView(getLayoutId())
        initView()
        initListener()
        initData(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        // 加载布局
        val contentView = LayoutInflater.from(mContext).inflate(
            layoutResID, null, false
        )

        // 将当前布局添加到根布局
        mMsvRoot.removeAllViews()
        mMsvRoot.addView(contentView)
        super.setContentView(mMsvRoot)
    }

    /**
     * 初始化监听器
     */
    protected open fun initListener() {

    }

    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this)
    }

    open fun showLoading() {
        mMsvRoot.showLoading()
    }

    open fun showContent() {
        mMsvRoot.showContent()
    }

    open fun showEmpty() {
        mMsvRoot.showEmpty()
    }

    open fun showNoNetwork() {
        mMsvRoot.showNoNetwork()
    }

    open fun showError() {
        mMsvRoot.showError()
    }

    open fun showErrorMsg(t: Throwable) {
        ExceptionHandle.handleException(t)
        when {
            isAlreadyLoadedData() -> {
                showErrorMsg(getString(R.string.framework_tips_no_network))
            }
            else -> {
                when (ExceptionHandle.errorCode) {
                    ErrorStatus.NETWORK_ERROR -> {
                        showNoNetwork()
                    }
                    else -> {
                        showError()
                    }
                }
            }
        }
    }

    open fun showErrorMsg(errorMsg: CharSequence) {
        showToast(errorMsg)
    }

    /**
     * 已经载入数据
     */
    open fun isAlreadyLoadedData(): Boolean = true

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化View
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData(savedInstanceState: Bundle?)
}