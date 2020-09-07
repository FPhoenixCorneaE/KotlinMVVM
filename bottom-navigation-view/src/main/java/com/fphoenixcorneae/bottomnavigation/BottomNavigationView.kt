package com.fphoenixcorneae.bottomnavigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.fphoenixcorneae.bottom_navigation.R
import java.util.*

class BottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var onBottomNavigationItemClickListener: OnBottomNavigationItemClickListener? = null

    private val NAVIGATION_HEIGHT = resources.getDimension(R.dimen.bottom_navigation_height).toInt()

    private val NAVIGATION_LINE_WIDTH =
        resources.getDimension(R.dimen.bottom_navigation_line_width).toInt()

    private var textActiveSize: Float = 0.toFloat()

    private var textInactiveSize: Float = 0.toFloat()

    private val bottomNavigationItems = ArrayList<BottomNavigationItem>()

    private val viewList = ArrayList<View>()

    private var itemActiveColorWithoutColoredBackground = -1

    private var navigationWidth: Int = 0

    private var shadowHeight: Int = 0

    private var itemInactiveColor: Int = 0

    private var itemWidth: Int = 0

    private var itemHeight: Int = 0

    private var withText: Boolean = false

    private var coloredBackground: Boolean = false

    private var disableShadow: Boolean = false

    private var isTablet: Boolean = false

    private var viewPagerSlide: Boolean = false

    private var isCustomFont = false

    private var willNotRecreate = true

    private var container: FrameLayout? = null

    private var backgroundColorTemp: View? = null

    private var mViewPager: ViewPager? = null

    private var font: Typeface? = null

    private var currentItem: Int = 0

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val res = resources
            val array = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationView)
            withText = array.getBoolean(R.styleable.BottomNavigationView_bnv_with_text, true)
            coloredBackground =
                array.getBoolean(R.styleable.BottomNavigationView_bnv_colored_background, true)
            disableShadow = array.getBoolean(R.styleable.BottomNavigationView_bnv_shadow, false)
            isTablet = array.getBoolean(R.styleable.BottomNavigationView_bnv_tablet, false)
            viewPagerSlide =
                array.getBoolean(R.styleable.BottomNavigationView_bnv_viewpager_slide, true)
            itemActiveColorWithoutColoredBackground =
                array.getColor(R.styleable.BottomNavigationView_bnv_active_color, -1)
            textActiveSize = array.getDimensionPixelSize(
                R.styleable.BottomNavigationView_bnv_active_text_size,
                res.getDimensionPixelSize(R.dimen.bottom_navigation_text_size_active)
            ).toFloat()
            textInactiveSize = array.getDimensionPixelSize(
                R.styleable.BottomNavigationView_bnv_inactive_text_size,
                res.getDimensionPixelSize(R.dimen.bottom_navigation_text_size_inactive)
            ).toFloat()

            array.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        navigationWidth = BottomNavigationUtil.getActionbarSize(context)
        val params = layoutParams
        if (coloredBackground) {
            itemActiveColorWithoutColoredBackground =
                ContextCompat.getColor(context, R.color.colorActive)
            itemInactiveColor = ContextCompat.getColor(context, R.color.colorInactive)
            shadowHeight = resources.getDimension(R.dimen.bottom_navigation_shadow_height).toInt()
        } else {
            if (itemActiveColorWithoutColoredBackground == -1) {
                itemActiveColorWithoutColoredBackground =
                    ContextCompat.getColor(context, R.color.itemActiveColorWithoutColoredBackground)
            }
            itemInactiveColor = ContextCompat.getColor(context, R.color.withoutColoredBackground)
            shadowHeight =
                resources.getDimension(R.dimen.bottom_navigation_shadow_height_without_colored_background)
                    .toInt()
        }
        when {
            isTablet -> {
                params.width = navigationWidth + NAVIGATION_LINE_WIDTH
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            else -> {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height =
                    when {
                        disableShadow -> NAVIGATION_HEIGHT
                        else -> NAVIGATION_HEIGHT + shadowHeight
                    }
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        elevation = resources.getDimension(R.dimen.bottom_navigation_elevation)
                    }
                }
            }
        }
        layoutParams = params
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (willNotRecreate) {
            removeAllViews()
        }
        if (currentItem < 0 || currentItem > bottomNavigationItems.size - 1) {
            throw IndexOutOfBoundsException(
                if (currentItem < 0)
                    "Position must be 0 or greater than 0, current is $currentItem"
                else
                    "Position must be less or equivalent than items size, items size is " + (bottomNavigationItems.size - 1) + " current is " + currentItem
            )
        }
        if (bottomNavigationItems.size == 0) {
            throw NullPointerException("You need at least one item")
        }
        val containerParams: LayoutParams
        val params: LayoutParams
        val lineParams: LayoutParams
        val white = ContextCompat.getColor(context, R.color.white)
        backgroundColorTemp = View(context)
        viewList.clear()
        if (isTablet) {
            itemWidth = LayoutParams.MATCH_PARENT
            itemHeight = navigationWidth
        } else {
            itemWidth = width / bottomNavigationItems.size
            itemHeight = LayoutParams.MATCH_PARENT
        }
        container = FrameLayout(context)
        val shadow = View(context)
        val line = View(context)
        val items = LinearLayout(context)
        items.orientation = if (isTablet) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
        val shadowParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, shadowHeight)
        if (isTablet) {
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.colorInactive))
            containerParams = LayoutParams(navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            lineParams = LayoutParams(NAVIGATION_LINE_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT)
            lineParams.addRule(ALIGN_PARENT_RIGHT)
            params = LayoutParams(navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            items.setPadding(0, itemHeight / 2, 0, 0)
            addView(line, lineParams)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val backgroundLayoutParams = LayoutParams(
                    navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT
                )
                backgroundLayoutParams.addRule(ALIGN_PARENT_LEFT)
                container!!.addView(backgroundColorTemp, backgroundLayoutParams)
            }
        } else {
            params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT)
            containerParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT)
            shadowParams.addRule(ABOVE, container!!.id)
            shadow.setBackgroundResource(R.drawable.bottom_navigation_shadow)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val backgroundLayoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT
                )
                backgroundLayoutParams.addRule(ALIGN_PARENT_BOTTOM)
                container!!.addView(backgroundColorTemp, backgroundLayoutParams)
            }
        }
        containerParams.addRule(if (isTablet) ALIGN_PARENT_LEFT else ALIGN_PARENT_BOTTOM)
        addView(shadow, shadowParams)
        addView(container, containerParams)
        container!!.addView(items, params)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for (i in 0 until bottomNavigationItems.size) {
            if (!coloredBackground) {
                bottomNavigationItems[i].color = white
            }

            val textActivePaddingTop =
                context.resources.getDimension(R.dimen.bottom_navigation_padding_top_active).toInt()
            val viewInactivePaddingTop =
                context.resources.getDimension(R.dimen.bottom_navigation_padding_top_inactive)
                    .toInt()
            val viewInactivePaddingTopWithoutText =
                context.resources.getDimension(R.dimen.bottom_navigation_padding_top_inactive_without_text)
                    .toInt()
            val view = inflater.inflate(R.layout.bottom_navigation, this, false)
            val icon = view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
            val title = view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView

            when {
                isCustomFont -> {
                    title.typeface = font
                }
                else -> {
                    title.typeface = ResourcesCompat.getFont(context, R.font.noahtt_normal)
                }
            }

            if (isTablet) {
                title.visibility = View.GONE
            }

            title.setTextColor(itemInactiveColor)
            viewList.add(view)

            if (bottomNavigationItems[i].imageResourceActive != 0) {
                if (i == currentItem) {
                    icon.setImageResource(bottomNavigationItems[i].imageResourceActive)
                } else {
                    icon.setImageResource(bottomNavigationItems[i].imageResource)
                }
            } else {
                icon.setImageResource(bottomNavigationItems[i].imageResource)
                icon.setColorFilter(if (i == currentItem) itemActiveColorWithoutColoredBackground else itemInactiveColor)
            }

            if (i == currentItem) {
                container!!.setBackgroundColor(bottomNavigationItems[i].color)
                title.setTextColor(itemActiveColorWithoutColoredBackground)
                icon.scaleX = 1.1.toFloat()
                icon.scaleY = 1.1.toFloat()
            }

            if (isTablet) {
                view.setPadding(
                    view.paddingLeft, view.paddingTop, if (i == currentItem)
                        textActivePaddingTop
                    else if (withText) viewInactivePaddingTop else viewInactivePaddingTopWithoutText,
                    view.paddingBottom
                )
            } else {
                view.setPadding(
                    view.paddingLeft,
                    if (i == currentItem)
                        textActivePaddingTop
                    else if (withText) viewInactivePaddingTop else viewInactivePaddingTopWithoutText,
                    view.paddingRight,
                    view.paddingBottom
                )
            }
            when {
                i == currentItem -> title.setTextSize(TypedValue.COMPLEX_UNIT_PX, textActiveSize)
                withText -> title.setTextSize(TypedValue.COMPLEX_UNIT_PX, textInactiveSize)
                else -> title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0F)
            }
            title.text = bottomNavigationItems[i].title
            val itemParams = LayoutParams(itemWidth, itemHeight)
            items.addView(view, itemParams)
            view.setOnClickListener { onBottomNavigationItemClick(i) }

        }
    }

    private fun onBottomNavigationItemClick(itemIndex: Int) {

        if (currentItem == itemIndex) {
            return
        }

        val viewActivePaddingTop =
            context.resources.getDimension(R.dimen.bottom_navigation_padding_top_active).toInt()
        val viewInactivePaddingTop =
            context.resources.getDimension(R.dimen.bottom_navigation_padding_top_inactive).toInt()
        val viewInactivePaddingTopWithoutText =
            context.resources.getDimension(R.dimen.bottom_navigation_padding_top_inactive_without_text)
                .toInt()
        var centerX: Int
        var centerY: Int
        for (i in viewList.indices) {
            if (i == itemIndex) {
                val view = viewList[itemIndex].findViewById<View>(R.id.bottom_navigation_container)
                val title = view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
                BottomNavigationUtil.changeTextColor(
                    title,
                    itemInactiveColor,
                    itemActiveColorWithoutColoredBackground
                )
                if (withText) {
                    BottomNavigationUtil.changeTextSize(title, textInactiveSize, textActiveSize)
                } else {
                    BottomNavigationUtil.changeTextSize(title, 0F, textActiveSize)
                }
                if (bottomNavigationItems[i].imageResourceActive != 0) {
                    icon.setImageResource(bottomNavigationItems[i].imageResourceActive)
                } else {
                    BottomNavigationUtil.changeImageColorFilter(
                        icon,
                        itemInactiveColor,
                        itemActiveColorWithoutColoredBackground
                    )
                }

                if (isTablet) {
                    if (withText) {
                        BottomNavigationUtil.changeRightPadding(
                            view,
                            viewInactivePaddingTop.toFloat(),
                            viewActivePaddingTop.toFloat()
                        )
                    } else {
                        BottomNavigationUtil.changeRightPadding(
                            view,
                            viewInactivePaddingTopWithoutText.toFloat(),
                            viewActivePaddingTop.toFloat()
                        )
                    }
                } else {
                    if (withText)
                        BottomNavigationUtil.changeViewTopPadding(
                            view,
                            viewInactivePaddingTop.toFloat(),
                            viewActivePaddingTop.toFloat()
                        )
                    else
                        BottomNavigationUtil.changeViewTopPadding(
                            view,
                            viewInactivePaddingTopWithoutText.toFloat(),
                            viewActivePaddingTop.toFloat()
                        )
                }

                icon.animate()
                    .setDuration(150)
                    .scaleX(1.1.toFloat())
                    .scaleY(1.1.toFloat())
                    .start()

                if (isTablet) {
                    centerX = viewList[itemIndex].width / 2
                    centerY = viewList[itemIndex].y.toInt() + viewList[itemIndex].height / 2
                } else {
                    centerX = viewList[itemIndex].x.toInt() + viewList[itemIndex].width / 2
                    centerY = viewList[itemIndex].height / 2
                }

                val finalRadius = Math.max(width, height)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    backgroundColorTemp!!.setBackgroundColor(bottomNavigationItems[itemIndex].color)
                    val changeBackgroundColor = ViewAnimationUtils.createCircularReveal(
                        backgroundColorTemp,
                        centerX,
                        centerY,
                        0f,
                        finalRadius.toFloat()
                    )
                    changeBackgroundColor.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            container!!.setBackgroundColor(bottomNavigationItems[itemIndex].color)
                        }
                    })
                    changeBackgroundColor.start()
                } else {
                    BottomNavigationUtil.changeViewBackgroundColor(
                        container as View,
                        bottomNavigationItems[currentItem].color,
                        bottomNavigationItems[itemIndex].color
                    )
                }

            } else if (i == currentItem) {
                val view = viewList[i].findViewById<View>(R.id.bottom_navigation_container)
                val title = view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView

                when {
                    bottomNavigationItems[i].imageResourceActive != 0 -> icon.setImageResource(
                        bottomNavigationItems[i].imageResource
                    )
                    else -> BottomNavigationUtil.changeImageColorFilter(
                        icon,
                        itemActiveColorWithoutColoredBackground,
                        itemInactiveColor
                    )
                }

                BottomNavigationUtil.changeTextColor(
                    title,
                    itemActiveColorWithoutColoredBackground,
                    itemInactiveColor
                )
                when {
                    withText -> BottomNavigationUtil.changeTextSize(
                        title,
                        textActiveSize,
                        textInactiveSize
                    )
                    else -> BottomNavigationUtil.changeTextSize(title, textActiveSize, 0F)
                }

                when {
                    isTablet -> when {
                        withText -> BottomNavigationUtil.changeRightPadding(
                            view,
                            viewActivePaddingTop.toFloat(),
                            viewInactivePaddingTop.toFloat()
                        )
                        else -> BottomNavigationUtil.changeRightPadding(
                            view,
                            viewActivePaddingTop.toFloat(),
                            viewInactivePaddingTopWithoutText.toFloat()
                        )
                    }
                    else -> when {
                        withText -> BottomNavigationUtil.changeViewTopPadding(
                            view,
                            viewActivePaddingTop.toFloat(),
                            viewInactivePaddingTop.toFloat()
                        )
                        else -> BottomNavigationUtil.changeViewTopPadding(
                            view,
                            viewActivePaddingTop.toFloat(),
                            viewInactivePaddingTopWithoutText.toFloat()
                        )
                    }
                }

                icon.animate()
                    .setDuration(150)
                    .scaleX(0.9.toFloat())
                    .scaleY(0.9.toFloat())
                    .start()
            }
        }

        if (mViewPager != null) {
            mViewPager!!.setCurrentItem(itemIndex, viewPagerSlide)
        }

        if (onBottomNavigationItemClickListener != null) {
            onBottomNavigationItemClickListener!!.onNavigationItemClick(itemIndex)
        }
        currentItem = itemIndex
    }

    /**
     * Creates a connection between this navigation view and a ViewPager
     *
     * @param pager          pager to connect to navigation view
     * @param colorResources color resources for every item in the ViewPager adapter
     * @param imageResources images resources for every item in the ViewPager adapter
     */
    fun setUpWithViewPager(
        pager: ViewPager,
        colorResources: IntArray,
        imageResources: IntArray
    ): BottomNavigationView {
        this.mViewPager = pager
        if (pager.adapter == null) {
            throw IllegalArgumentException("you should set a adapter for ViewPager first!")
        }
        if (pager.adapter!!.count != colorResources.size || pager.adapter!!.count != imageResources.size) {
            throw IllegalArgumentException("colorResources and imageResources must be equal to the ViewPager items : " + pager.adapter!!.count)
        }
        for (i in 0 until pager.adapter!!.count) {
            addTab(
                BottomNavigationItem(
                    pager.adapter!!.getPageTitle(i),
                    colorResources[i],
                    imageResources[i]
                )
            )
        }
        return this
    }

    /**
     * Set items for BottomNavigation
     *
     * @param items items to set
     */
    fun setTabs(items: List<BottomNavigationItem>): BottomNavigationView {
        bottomNavigationItems.clear()
        bottomNavigationItems.addAll(items)
        return this
    }

    /**
     * Add item for BottomNavigation
     *
     * @param item item to add
     */
    fun addTab(vararg item: BottomNavigationItem): BottomNavigationView {
        bottomNavigationItems.addAll(listOf(*item))
        return this
    }

    /**
     * Activate BottomNavigation tablet mode
     */
    fun activateTabletMode(): BottomNavigationView {
        isTablet = true
        return this
    }

    /**
     * Change text visibility
     *
     * @param withText disable or enable item text
     */
    fun isWithText(withText: Boolean): BottomNavigationView {
        this.withText = withText
        return this
    }

    /**
     * Item Active Color if isColoredBackground(false)
     *
     * @param itemActiveColorWithoutColoredBackground active item color
     */
    fun setItemActiveColorWithoutColoredBackground(itemActiveColorWithoutColoredBackground: Int): BottomNavigationView {
        this.itemActiveColorWithoutColoredBackground = itemActiveColorWithoutColoredBackground
        return this
    }

    /**
     * With this BottomNavigation background will be white
     *
     * @param coloredBackground disable or enable background color
     */
    fun isColoredBackground(coloredBackground: Boolean): BottomNavigationView {
        this.coloredBackground = coloredBackground
        return this
    }

    /**
     * Change tab programmatically
     *
     * @param position selected tab position
     */
    fun selectTab(position: Int): BottomNavigationView {
        onBottomNavigationItemClick(position)
        currentItem = position
        return this
    }

    /**
     * Disable bottom_navigation_shadow of BottomNavigationView
     */
    fun disableShadow(): BottomNavigationView {
        disableShadow = true
        return this
    }

    /**
     * Disable slide animation when using ViewPager
     */
    fun disableViewPagerSlide(): BottomNavigationView {
        viewPagerSlide = false
        return this
    }

    /**
     * Change Active text size
     *
     * @param textActiveSize size
     */
    fun setTextActiveSize(textActiveSize: Float): BottomNavigationView {
        this.textActiveSize = textActiveSize
        return this
    }

    /**
     * Change Inactive text size
     *
     * @param textInactiveSize size
     */
    fun setTextInactiveSize(textInactiveSize: Float): BottomNavigationView {
        this.textInactiveSize = textInactiveSize
        return this
    }

    /**
     * Setup interface for item onClick
     */
    fun setOnBottomNavigationItemClickListener(onBottomNavigationItemClickListener: OnBottomNavigationItemClickListener): BottomNavigationView {
        this.onBottomNavigationItemClickListener = onBottomNavigationItemClickListener
        return this
    }

    /**
     * Returns the item that is currently selected
     *
     * @return Currently selected item
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * If your activity/fragment will not recreate
     * you can call this method
     *
     * @param willNotRecreate set true if will not recreate
     */
    fun willNotRecreate(willNotRecreate: Boolean): BottomNavigationView {
        this.willNotRecreate = willNotRecreate
        return this
    }

    /**
     * set custom font for item texts
     *
     * @param font custom font
     */
    fun setFont(font: Typeface?): BottomNavigationView {
        isCustomFont = true
        this.font = font
        return this
    }

    /**
     * get item text size on active status
     *
     * @return font size
     */
    fun getTextActiveSize(): Float {
        return textActiveSize
    }

    /**
     * get item text size on inactive status
     *
     * @return font size
     */
    fun getTextInactiveSize(): Float {
        return textInactiveSize
    }


    fun getItem(position: Int): BottomNavigationItem {
        onBottomNavigationItemClick(position)
        return bottomNavigationItems[position]
    }
}
