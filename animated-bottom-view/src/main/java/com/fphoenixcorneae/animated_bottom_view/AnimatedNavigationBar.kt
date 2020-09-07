package com.fphoenixcorneae.animated_bottom_view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fphoenixcorneae.util.ImageUtil
import com.fphoenixcorneae.util.SizeUtil
import kotlin.math.abs


/**
 * 导航条
 */
class AnimatedNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    //导航底部的高度
    private var navigationBottom = 0

    //导航上部的高度
    private var navigationTop = 0

    //导航的总高度
    private var navigationHeight = 0

    //导航的总宽度
    private var navigationWidth = 0

    //导航某一项的宽度
    private var navigationButtonWidth = 0

    //小球距离顶部的距离
    private var distanceTop = 0

    //小球最大向下的距离
    private var maxTranslationY = 0

    //当前选中的是第几项
    private var currentIndex = 0

    //动画动画（上下左右+旋转）View
    private var animatedGroup: AnimatedGroup? = null

    //贝塞尔曲线View
    private var concaveView: ConcaveView? = null

    //5个View
    private val navigationButtonListView = arrayListOf<View>()

    //旋转动画
    private val rotateAnimationManager by lazy {
        RotateAnimationManager()
    }

    //位移动画
    private var translationAnimationManager: TranslationAnimationManager? = null

    //导航Item列表
    var navigationItemList = emptyArray<AnimatedNavigationItem>()

    //导航条Item点击监听
    var onNavigationItemClickListener: OnNavigationItemClickListener? = null

    init {
        // 使用硬件的方式离屏缓冲
        setLayerType(LAYER_TYPE_HARDWARE, null)
        navigationBottom = SizeUtil.dp2px(50.0f)
        navigationTop = SizeUtil.dp2px(30.0f)
        distanceTop = SizeUtil.dp2px(10.0f)
        maxTranslationY = SizeUtil.dp2px(45.0f)
        navigationHeight = navigationBottom + navigationTop
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        navigationWidth = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), navigationHeight)
    }

    fun addImageButtons(animatedNavigationItems: Array<AnimatedNavigationItem>) {
        removeAllViews()
        animatedGroup = AnimatedGroup(context)
        animatedGroup?.setImageResource(
            animatedNavigationItems[0].resourceId,
            animatedNavigationItems[0].selectedColor
        )
        rotateAnimationManager.view = animatedGroup?.getAnimationView()
        animatedGroup?.let {
            super.addView(it)
        }

        concaveView = ConcaveView(context)
        concaveView?.let {
            super.addView(it)
            it.post {
                it.setFractionAndX(
                    0.0f,
                    (animatedGroup!!.left + animatedGroup!!.right) / 2.0f
                )
            }
        }
        navigationItemList = animatedNavigationItems

        for (i in animatedNavigationItems.indices) {
            // item布局
            val itemLayout = LinearLayout(context)
            itemLayout.orientation = LinearLayout.VERTICAL
            itemLayout.gravity = Gravity.CENTER
            val itemLayoutParams = LayoutParams(-1, -1)
            super.addView(itemLayout, itemLayoutParams)
            navigationButtonListView.add(itemLayout)
            // item图像
            val itemImage = ImageView(context)
            ImageUtil.setTintColor(
                itemImage,
                animatedNavigationItems[i].resourceId,
                animatedNavigationItems[i].normalColor
            )
            val itemImageLayoutParams =
                LinearLayout.LayoutParams(SizeUtil.dp2px(30.0f), SizeUtil.dp2px(30.0f))
            itemLayout.addView(itemImage, itemImageLayoutParams)
            // item名称
            val itemName = TextView(context)
            itemName.text = animatedNavigationItems[i].itemName
            itemName.setTextColor(animatedNavigationItems[i].normalColor)
            itemName.textSize = 16f
            val itemNameLayoutParams = LinearLayout.LayoutParams(-2, -2)
            itemLayout.addView(itemName, itemNameLayoutParams)

            if (i == 0) {
                itemLayout.alpha = 0.0f
            }
            itemLayout.setOnClickListener {
                if (currentIndex == i) {
                    return@setOnClickListener
                }

                animatedGroup?.let {
                    translationAnimationManager = TranslationAnimationManager(
                        it.translationX,
                        navigationButtonWidth * i.toFloat(),
                        it.translationY,
                        maxTranslationY.toFloat()
                    )
                    translationAnimationManager?.view = animatedGroup
                    translationAnimationManager?.setAnimationUpdate(object : AnimationUpdate {
                        var p = 0.0f
                        override fun onAnimationUpdate(percent: Float) {

                            when {
                                p < 0.5f && percent >= 0.5f -> {
                                    it.setImageResource(
                                        navigationItemList[i].resourceId,
                                        animatedNavigationItems[i].selectedColor
                                    )
                                }
                                percent == 1.0f -> {
                                    rotateAnimationManager.startAnimation()
                                }
                            }
                            p = percent

                            //根据translationX的值，去设置5个View的透明度
                            animatedGroup?.let {
                                val animationViewX = navigationButtonWidth / 2.0f + it.translationX

                                for (j in 0 until navigationButtonListView.size) {
                                    val navigationButtonView = navigationButtonListView[j]
                                    val centerX =
                                        (navigationButtonView.left + navigationButtonView.right) / 2.0f
                                    val d = abs(animationViewX - centerX)
                                    when {
                                        d <= navigationButtonWidth * 0.75f -> {
                                            navigationButtonView.alpha = 0.0f
                                        }
                                        d >= navigationButtonWidth * 1.0f -> {
                                            navigationButtonView.alpha = 1.0f
                                        }
                                        else -> {
                                            navigationButtonView.alpha =
                                                (d - navigationButtonWidth * 0.75f) / (navigationButtonWidth * (1.0f - 0.75f))
                                        }
                                    }
                                }
                            }

                            concaveView?.setFractionAndX(
                                it.translationY / maxTranslationY.toFloat(),
                                it.translationX + (it.left + it.right) * 0.5f
                            )
                        }
                    })
                    translationAnimationManager?.startAnimation()
                }
                currentIndex = i
                onNavigationItemClickListener?.onItemClick(it, currentIndex)
            }
        }
        requestLayout()
    }

    override fun addView(child: View?) {

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount <= 0) {
            return
        }

        if (navigationButtonListView.size > 0) {
            navigationButtonWidth = navigationWidth / (navigationButtonListView.size)
        }

        for (i in 0 until navigationButtonListView.size) {
            val view = navigationButtonListView[i]
            view.layout(
                i * navigationButtonWidth,
                navigationTop,
                (i + 1) * navigationButtonWidth,
                navigationTop + navigationBottom
            )
        }

        animatedGroup?.let {
            it.layout(
                (navigationButtonWidth - it.measuredWidth) / 2,
                distanceTop,
                (navigationButtonWidth - it.measuredWidth) / 2 + it.measuredWidth,
                it.measuredHeight + distanceTop
            )
        }

        concaveView?.layout(0, navigationTop, this.measuredWidth, this.measuredHeight)
    }
}