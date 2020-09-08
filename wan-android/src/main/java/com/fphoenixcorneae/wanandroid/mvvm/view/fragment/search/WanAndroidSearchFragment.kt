package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.search

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.transition.Fade
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.fphoenixcorneae.flowlayout.FlowItem
import com.fphoenixcorneae.flowlayout.FlowLayout
import com.fphoenixcorneae.ext.*
import com.fphoenixcorneae.ext.view.*
import com.fphoenixcorneae.titlebar.CommonTitleBar
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.util.KeyboardUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.util.SharedPreferencesUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidSearchBean
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidSearchViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_search.*

/**
 * @desc: 搜索Fragment
 * @date: 2020-06-28 14:31
 */
class WanAndroidSearchFragment : WanAndroidBaseFragment() {

    /* 搜索ViewModel */
    private val mSearchViewModel by viewModel<WanAndroidSearchViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_search

    override fun initView() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                setUpEnterAnimation()
                setUpExitAnimation()
            }
            else -> {
                setUpView()
            }
        }

        mTbTitleBar.init(object : CommonTitleBar.OnTitleBarClickListener {
            override fun onClicked(v: View?, action: Int, extra: String?) {
                when (action) {
                    CommonTitleBar.MotionAction.ACTION_SEARCH_SUBMIT -> {
                        // 搜索框输入状态下,键盘提交触发
                        updateSearchHistory(extra ?: "")
                        goToSearchResult(extra)
                    }
                    CommonTitleBar.MotionAction.ACTION_RIGHT_TEXT -> {
                        onBackPressed()
                    }
                }
            }
        })
    }


    override fun initListener() {
        mRvHotSearch.mOnItemClickListener = object : FlowLayout.OnItemClickListener {
            override fun onItemClick(
                itemName: CharSequence?,
                position: Int,
                isSelected: Boolean,
                selectedData: ArrayList<in FlowItem>
            ) {
                updateSearchHistory(itemName.toString())
                goToSearchResult(itemName)
            }
        }
        mRvSearchHistory.mOnItemClickListener = object : FlowLayout.OnItemClickListener {
            override fun onItemClick(
                itemName: CharSequence?,
                position: Int,
                isSelected: Boolean,
                selectedData: ArrayList<in FlowItem>
            ) {
                updateSearchHistory(itemName.toString())
                goToSearchResult(itemName)
            }

            override fun onDelete(
                itemName: CharSequence?,
                position: Int,
                selectedData: ArrayList<in FlowItem>
            ) {
                updateSearchHistory(itemName.toString(), true)
            }
        }
        mTvEmpty.setOnClickListener {
            MaterialDialog(mContext).show {
                title(text = getString(R.string.wan_android_search_empty_history))
                message(text = getString(R.string.wan_android_search_confirm_empty_history))
                positiveButton(text = getString(R.string.wan_android_confirm)) {
                    mSearchViewModel.mSearchHistory.postValue(arrayListOf())
                }
                negativeButton(text = getString(R.string.wan_android_cancel)) {

                }
                lifecycleOwner(viewLifecycleOwner)
                view.titleLayout.findViewById<TextView>(R.id.md_text_title)
                    .setTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                getActionButton(WhichButton.POSITIVE)
                    .updateTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
            }
        }
        mSearchViewModel.apply {
            mHotSearch.observe(viewLifecycleOwner, Observer {
                it?.let {
                    mRvHotSearch.apply {
                        mDatas = it as ArrayList<in FlowItem>
                    }
                }
            })
            mSearchHistory.observe(viewLifecycleOwner, Observer {
                mTvEmpty.isVisible = it.isNotEmpty()
                mTvNoSearchHistory.isVisible = it.isEmpty()
                mRvSearchHistory.apply {
                    mDatas = it as ArrayList<in FlowItem>
                }
                // 保存搜索历史
                SharedPreferencesUtil.put(
                    WanAndroidConstant.WAN_ANDROID_SEARCH_HISTORY,
                    it.toJson()
                )
            })
        }
    }

    /**
     * 更新搜索历史
     */
    private fun updateSearchHistory(itemName: String, isDelete: Boolean = false) {
        mSearchViewModel.mSearchHistory.apply {
            value?.let {
                when {
                    it.size >= 20 -> {
                        it.removeAt(it.lastIndex)
                    }
                }
                // 这里必须从后面开始轮询，否则会出现ConcurrentModificationException异常
                for (index in it.size - 1 downTo 0) {
                    if (it[index].name == itemName) {
                        it.removeAt(index)
                    }
                }
                when {
                    !isDelete -> {
                        it.add(0, WanAndroidSearchBean(name = itemName))
                    }
                }
            }
            postValue(value)
        }
    }

    /**
     * 跳转搜索结果页面
     */
    private fun goToSearchResult(itemName: CharSequence?) {
        // 关闭软键盘
        KeyboardUtil.closeKeyboard(mTbTitleBar.centerSearchEditText)
        navigateNext(
            R.id.searchResultFragment,
            BundleBuilder.of()
                .putString(WanAndroidConstant.WAN_ANDROID_SEARCH_KEY, itemName.toString())
                .get()
        )
    }

    private fun setUpView() {
        mClRoot.setBackgroundColor(Color.TRANSPARENT)
        mFabCircle.gone()
        mClSearch.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in))
        mClSearch.visible()
        // 打开软键盘
        KeyboardUtil.openKeyboard(mTbTitleBar.centerSearchEditText)
    }

    override fun lazyLoadData() {
        mSearchViewModel.getHotSearchData()
        mSearchViewModel.getSearchHistoryData()
    }

    override fun isAlreadyLoadedData(): Boolean = true

    /**
     * 进场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpEnterAnimation() {
        sharedElementEnterTransition = TransitionInflater.from(mContext)
            .inflateTransition(R.transition.wan_android_transition_arc_motion)
            .addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                }

                override fun onTransitionEnd(transition: Transition) {
                    transition.removeListener(this)
                    animateRevealShow()
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }
            })
    }

    /**
     * 退场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpExitAnimation() {
        sharedElementReturnTransition = Fade().apply {
            duration = 300
        }
    }

    /**
     * 揭露动画显示
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateRevealShow() {
        mClRoot.animateRevealShow(
            mFabCircle.width / 2,
            R.color.wan_android_colorPrimary,
            object : OnRevealAnimationListener {
                override fun onRevealShow() {
                    setUpView()
                }
            })
    }

    /**
     * 圆圈凝聚效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateRevealHide() {
        mClRoot.animateRevealHide(
            mFabCircle.width / 2,
            R.color.wan_android_colorPrimary,
            object : OnRevealAnimationListener {
                override fun onRevealHide() {
                    mFabCircle.visible()
                    mClSearch.gone()
                    defaultBackPressed()
                }
            })
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateRevealHide()
        } else {
            defaultBackPressed()
        }
    }

    /**
     * 默认返回
     */
    private fun defaultBackPressed() {
        // 关闭软键盘
        KeyboardUtil.closeKeyboard(mTbTitleBar.centerSearchEditText)
        popBackStack()
    }

    override fun onDestroy() {
        KeyboardUtil.fixSoftInputLeaks(mContext.window)
        super.onDestroy()
    }
}