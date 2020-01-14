package com.wkz.titlebar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import com.wkz.util.*
import com.wkz.util.statusbar.StatusBarUtil.getStatusBarHeight
import com.wkz.util.statusbar.StatusBarUtil.setDarkMode
import com.wkz.util.statusbar.StatusBarUtil.setLightMode
import com.wkz.util.statusbar.StatusBarUtil.supportTransparentStatusBar
import com.wkz.util.statusbar.StatusBarUtil.transparentStatusBar
import kotlin.math.max

/**
 * 通用标题栏
 */
class CommonTitleBar(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs), View.OnClickListener {
    private var viewStatusBarFill // 状态栏填充视图
            : View? = null
    /**
     * 获取标题栏底部横线
     *
     * @return
     */
    var bottomLine // 分隔线视图
            : View? = null
        private set
    private var viewBottomShadow // 底部阴影
            : View? = null
    private var rlMain // 主视图
            : RelativeLayout? = null
    /**
     * 获取标题栏左边TextView，对应leftType = textView
     *
     * @return
     */
    var leftTextView // 左边TextView
            : TextView? = null
        private set
    /**
     * 获取标题栏左边ImageButton，对应leftType = imageButton
     *
     * @return
     */
    var leftImageButton // 左边ImageButton
            : ImageButton? = null
        private set
    /**
     * 获取左边自定义布局
     *
     * @return
     */
    var leftCustomView: View? = null
        private set
    /**
     * 获取标题栏右边TextView，对应rightType = textView
     *
     * @return
     */
    var rightTextView // 右边TextView
            : TextView? = null
        private set
    /**
     * 获取标题栏右边ImageButton，对应rightType = imageButton
     *
     * @return
     */
    var rightImageButton // 右边ImageButton
            : ImageButton? = null
        private set
    /**
     * 获取右边自定义布局
     *
     * @return
     */
    var rightCustomView: View? = null
        private set
    var centerLayout: LinearLayout? = null
        private set
    /**
     * 获取标题栏中间TextView，对应centerType = textView
     *
     * @return
     */
    var centerTextView // 标题栏文字
            : TextView? = null
        private set
    var centerSubTextView // 副标题栏文字
            : TextView? = null
        private set
    private var progressCenter // 中间进度条,默认隐藏
            : ProgressBar? = null
    /**
     * 获取搜索框布局，对应centerType = searchView
     *
     * @return
     */
    var centerSearchView // 中间搜索框布局
            : RelativeLayout? = null
        private set
    /**
     * 获取搜索框内部输入框，对应centerType = searchView
     *
     * @return
     */
    var centerSearchEditText: EditText? = null
        private set
    var centerSearchLeftImageView: ImageView? = null
        private set
    /**
     * 获取搜索框右边图标ImageView，对应centerType = searchView
     *
     * @return
     */
    var centerSearchRightImageView: ImageView? = null
        private set
    /**
     * 获取中间自定义布局视图
     *
     * @return
     */
    var centerCustomView // 中间自定义视图
            : View? = null
        private set
    private var fillStatusBar: Boolean = true // 是否撑起状态栏, true时,标题栏浸入状态栏 = false
    private var titleBarColor: Int = Color.WHITE// 标题栏背景颜色 = 0
    private var titleBarHeight: Int = SizeUtil.dp2px(44f) // 标题栏高度 = 0
    private var statusBarColor: Int = Color.WHITE // 状态栏颜色 = 0
    private var statusBarMode: Int = 0 // 状态栏模式 = 0
    private var showBottomLine: Boolean = true // 是否显示底部分割线 = false
    private var bottomLineColor: Int = Color.parseColor("#dddddd")// 分割线颜色 = 0
    private var bottomShadowHeight: Float = SizeUtil.dp2px(0f).toFloat() // 底部阴影高度 = 0f
    private var leftType =
        TYPE_LEFT_NONE// 左边视图类型 = 0
    private var leftText // 左边TextView文字
            : String? = null
    private var leftTextColor =
        ResourceUtil.getColor(R.color.common_titlebar_text_selector)// 左边TextView颜色 = 0
    private var leftTextSize = SizeUtil.dp2px(16f).toFloat()// 左边TextView文字大小 = 0f
    private var leftDrawable = 0// 左边TextView drawableLeft资源 = 0
    private var leftDrawablePadding = 5f // 左边TextView drawablePadding = 0f
    private var leftImageResource = R.drawable.common_titlebar_reback_selector // 左边图片资源 = 0
    private var leftCustomViewRes = 0// 左边自定义视图布局资源 = 0
    private var rightType =
        TYPE_RIGHT_NONE // 右边视图类型 = 0
    private var rightText // 右边TextView文字
            : String? = null
    private var rightTextColor =
        ResourceUtil.getColor(R.color.common_titlebar_text_selector)// 右边TextView颜色 = 0
    private var rightTextSize = SizeUtil.dp2px(16f).toFloat()// 右边TextView文字大小 = 0f
    private var rightImageResource = 0// 右边图片资源 = 0
    private var rightCustomViewRes = 0// 右边自定义视图布局资源 = 0
    private var centerType =
        TYPE_CENTER_NONE // 中间视图类型 = 0
    private var centerText // 中间TextView文字
            : String? = null
    private var centerTextColor = Color.parseColor("#333333")// 中间TextView字体颜色 = 0
    private var centerTextSize = SizeUtil.dp2px(18f).toFloat()// 中间TextView字体大小 = 0f
    private var centerTextMarquee = true// 中间TextView字体是否显示跑马灯效果 = false
    private var centerSubText // 中间subTextView文字
            : String? = null
    private var centerSubTextColor = Color.parseColor("#666666")// 中间subTextView字体颜色 = 0
    private var centerSubTextSize = SizeUtil.dp2px(11f).toFloat()// 中间subTextView字体大小 = 0f
    private var centerSearchEditable = true// 搜索框是否可输入 = false
    private var centerSearchBgResource = R.drawable.common_titlebar_search_gray_shape // 搜索框背景图片 = 0
    private var centerSearchRightType =
        TYPE_CENTER_SEARCH_RIGHT_VOICE// 搜索框右边按钮类型  0: voice 1: delete = 0
    private var centerCustomViewRes = 0// 中间自定义布局资源 = 0
    private var PADDING_5 = SizeUtil.dp2px(5f)
    private var PADDING_16 = SizeUtil.dp2px(16f)
    private var listener: OnTitleBarClickListener? = null
    private var doubleClickListener: OnTitleBarDoubleClickListener? = null

    private fun loadAttributes(
        context: Context,
        attrs: AttributeSet
    ) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // notice 未引入沉浸式标题栏之前,隐藏标题栏撑起布局
            fillStatusBar = array.getBoolean(R.styleable.CommonTitleBar_fillStatusBar, true)
        }
        titleBarColor =
            array.getColor(R.styleable.CommonTitleBar_titleBarColor, Color.WHITE)
        titleBarHeight = array.getDimension(
            R.styleable.CommonTitleBar_titleBarHeight,
            SizeUtil.dp2px(44f).toFloat()
        ).toInt()
        statusBarColor =
            array.getColor(R.styleable.CommonTitleBar_statusBarColor, Color.WHITE)
        statusBarMode = array.getInt(R.styleable.CommonTitleBar_statusBarMode, 0)
        showBottomLine = array.getBoolean(R.styleable.CommonTitleBar_showBottomLine, true)
        bottomLineColor = array.getColor(
            R.styleable.CommonTitleBar_bottomLineColor,
            Color.parseColor("#dddddd")
        )
        bottomShadowHeight = array.getDimension(
            R.styleable.CommonTitleBar_bottomShadowHeight,
            SizeUtil.dp2px(0f).toFloat()
        )
        leftType = array.getInt(
            R.styleable.CommonTitleBar_leftType,
            TYPE_LEFT_NONE
        )
        when (leftType) {
            TYPE_LEFT_TEXT_VIEW -> {
                leftText = array.getString(R.styleable.CommonTitleBar_leftText)
                leftTextColor = array.getColor(
                    R.styleable.CommonTitleBar_leftTextColor,
                    ResourceUtil.getColor(R.color.common_titlebar_text_selector)
                )
                leftTextSize = array.getDimension(
                    R.styleable.CommonTitleBar_leftTextSize,
                    SizeUtil.dp2px(16f).toFloat()
                )
                leftDrawable = array.getResourceId(R.styleable.CommonTitleBar_leftDrawable, 0)
                leftDrawablePadding =
                    array.getDimension(R.styleable.CommonTitleBar_leftDrawablePadding, 5f)
            }
            TYPE_LEFT_IMAGE_BUTTON -> {
                leftImageResource = array.getResourceId(
                    R.styleable.CommonTitleBar_leftImageResource,
                    R.drawable.common_titlebar_reback_selector
                )
            }
            TYPE_LEFT_CUSTOM_VIEW -> {
                leftCustomViewRes =
                    array.getResourceId(R.styleable.CommonTitleBar_leftCustomView, 0)
            }
        }
        rightType = array.getInt(
            R.styleable.CommonTitleBar_rightType,
            TYPE_RIGHT_NONE
        )
        when (rightType) {
            TYPE_RIGHT_TEXT_VIEW -> {
                rightText = array.getString(R.styleable.CommonTitleBar_rightText)
                rightTextColor = array.getColor(
                    R.styleable.CommonTitleBar_rightTextColor,
                    ResourceUtil.getColor(R.color.common_titlebar_text_selector)
                )
                rightTextSize = array.getDimension(
                    R.styleable.CommonTitleBar_rightTextSize,
                    SizeUtil.dp2px(16f).toFloat()
                )
            }
            TYPE_RIGHT_IMAGE_BUTTON -> {
                rightImageResource =
                    array.getResourceId(R.styleable.CommonTitleBar_rightImageResource, 0)
            }
            TYPE_RIGHT_CUSTOM_VIEW -> {
                rightCustomViewRes =
                    array.getResourceId(R.styleable.CommonTitleBar_rightCustomView, 0)
            }
        }
        centerType = array.getInt(
            R.styleable.CommonTitleBar_centerType,
            TYPE_CENTER_NONE
        )
        when (centerType) {
            TYPE_CENTER_TEXT_VIEW -> {
                centerText = array.getString(R.styleable.CommonTitleBar_centerText)
                // 如果当前上下文对象是Activity，就获取Activity的标题
                if (centerText.isNullOrBlank() && getContext() is Activity) {
                    // 获取清单文件中的 android:label 属性值
                    val label = (getContext() as Activity).title
                    if (!label.isNullOrBlank()) {
                        try {
                            val packageManager = getContext().packageManager
                            val packageInfo =
                                packageManager.getPackageInfo(getContext().packageName, 0)
                            // 如果当前 Activity 没有设置 android:label 属性，则默认会返回 APP 名称，则需要过滤掉
                            if (label.toString() != packageInfo.applicationInfo.loadLabel(
                                    packageManager
                                ).toString()
                            ) {
                                // 设置标题
                                centerText = label.toString()
                            }
                        } catch (ignored: PackageManager.NameNotFoundException) {
                        }
                    }
                }
                centerTextColor = array.getColor(
                    R.styleable.CommonTitleBar_centerTextColor,
                    Color.parseColor("#333333")
                )
                centerTextSize = array.getDimension(
                    R.styleable.CommonTitleBar_centerTextSize,
                    SizeUtil.dp2px(18f).toFloat()
                )
                centerTextMarquee =
                    array.getBoolean(R.styleable.CommonTitleBar_centerTextMarquee, true)
                centerSubText = array.getString(R.styleable.CommonTitleBar_centerSubText)
                centerSubTextColor = array.getColor(
                    R.styleable.CommonTitleBar_centerSubTextColor,
                    Color.parseColor("#666666")
                )
                centerSubTextSize = array.getDimension(
                    R.styleable.CommonTitleBar_centerSubTextSize,
                    SizeUtil.dp2px(11f).toFloat()
                )
            }
            TYPE_CENTER_SEARCH_VIEW -> {
                centerSearchEditable =
                    array.getBoolean(R.styleable.CommonTitleBar_centerSearchEditable, true)
                centerSearchBgResource = array.getResourceId(
                    R.styleable.CommonTitleBar_centerSearchBg,
                    R.drawable.common_titlebar_search_gray_shape
                )
                centerSearchRightType = array.getInt(
                    R.styleable.CommonTitleBar_centerSearchRightType,
                    TYPE_CENTER_SEARCH_RIGHT_VOICE
                )
            }
            TYPE_CENTER_CUSTOM_VIEW -> {
                centerCustomViewRes =
                    array.getResourceId(R.styleable.CommonTitleBar_centerCustomView, 0)
            }
        }
        array.recycle()
    }

    private val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    private val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    /**
     * 初始化全局视图
     *
     * @param context 上下文
     */
    private fun initGlobalViews(context: Context) {
        val globalParams =
            ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        layoutParams = globalParams
        val transparentStatusBar = supportTransparentStatusBar()
        // 构建标题栏填充视图
        if (fillStatusBar && transparentStatusBar) {
            val statusBarHeight = getStatusBarHeight(context)
            viewStatusBarFill = View(context)
            viewStatusBarFill!!.id = ViewUtil.generateViewId()
            viewStatusBarFill!!.setBackgroundColor(statusBarColor)
            val statusBarParams =
                LayoutParams(MATCH_PARENT, statusBarHeight)
            statusBarParams.addRule(ALIGN_PARENT_TOP)
            addView(viewStatusBarFill, statusBarParams)
        }
        // 构建主视图
        rlMain = RelativeLayout(context)
        rlMain!!.id = ViewUtil.generateViewId()
        rlMain!!.setBackgroundColor(titleBarColor)
        val mainParams =
            LayoutParams(MATCH_PARENT, titleBarHeight)
        if (fillStatusBar && transparentStatusBar) {
            mainParams.addRule(BELOW, viewStatusBarFill!!.id)
        } else {
            mainParams.addRule(ALIGN_PARENT_TOP)
        }
        // 计算主布局高度
        if (showBottomLine) {
            mainParams.height =
                titleBarHeight - max(1, SizeUtil.dp2px(0.4f))
        } else {
            mainParams.height = titleBarHeight
        }
        addView(rlMain, mainParams)
        // 构建分割线视图
        if (showBottomLine) {
            // 已设置显示标题栏分隔线,5.0以下机型,显示分隔线
            bottomLine = View(context)
            bottomLine!!.setBackgroundColor(bottomLineColor)
            val bottomLineParams = LayoutParams(
                MATCH_PARENT,
                max(1, SizeUtil.dp2px(0.4f))
            )
            bottomLineParams.addRule(BELOW, rlMain!!.id)
            addView(bottomLine, bottomLineParams)
        } else if (bottomShadowHeight != 0f) {
            viewBottomShadow = View(context)
            viewBottomShadow!!.setBackgroundResource(R.drawable.common_titlebar_bottom_shadow)
            val bottomShadowParams = LayoutParams(
                MATCH_PARENT,
                SizeUtil.dp2px(bottomShadowHeight)
            )
            bottomShadowParams.addRule(BELOW, rlMain!!.id)
            addView(viewBottomShadow, bottomShadowParams)
        }
    }

    /**
     * 初始化主视图
     *
     * @param context 上下文
     */
    private fun initMainViews(context: Context) {
        if (leftType != TYPE_LEFT_NONE) {
            initMainLeftViews(context)
        }
        if (rightType != TYPE_RIGHT_NONE) {
            initMainRightViews(context)
        }
        if (centerType != TYPE_CENTER_NONE) {
            initMainCenterViews(context)
        }
    }

    /**
     * 初始化主视图左边部分
     * -- add: adaptive RTL
     *
     * @param context 上下文
     */
    private fun initMainLeftViews(context: Context) {
        val leftInnerParams =
            LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        leftInnerParams.addRule(ALIGN_PARENT_START)
        leftInnerParams.addRule(CENTER_VERTICAL)
        when (leftType) {
            TYPE_LEFT_TEXT_VIEW -> { // 初始化左边TextView
                leftTextView = TextView(context)
                leftTextView!!.id = ViewUtil.generateViewId()
                leftTextView!!.text = leftText
                leftTextView!!.setTextColor(leftTextColor)
                leftTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize)
                leftTextView!!.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                leftTextView!!.isSingleLine = true
                leftTextView!!.setOnClickListener(this)
                // 设置DrawableLeft及DrawablePadding
                if (leftDrawable != 0) {
                    leftTextView!!.compoundDrawablePadding = leftDrawablePadding.toInt()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        leftTextView!!.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            leftDrawable,
                            0,
                            0,
                            0
                        )
                    } else {
                        leftTextView!!.setCompoundDrawablesWithIntrinsicBounds(
                            leftDrawable,
                            0,
                            0,
                            0
                        )
                    }
                }
                leftTextView!!.setPadding(PADDING_16, 0, PADDING_16, 0)
                rlMain!!.addView(leftTextView, leftInnerParams)
            }
            TYPE_LEFT_IMAGE_BUTTON -> { // 初始化左边ImageButton
                leftImageButton = ImageButton(context)
                leftImageButton!!.id = ViewUtil.generateViewId()
                leftImageButton!!.setBackgroundColor(Color.TRANSPARENT)
                leftImageButton!!.setImageResource(leftImageResource)
                leftImageButton!!.setPadding(PADDING_16, 0, PADDING_16, 0)
                leftImageButton!!.setOnClickListener(this)
                rlMain!!.addView(leftImageButton, leftInnerParams)
            }
            TYPE_LEFT_CUSTOM_VIEW -> { // 初始化自定义View
                leftCustomView =
                    LayoutInflater.from(context).inflate(leftCustomViewRes, rlMain, false)
                leftCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = ViewUtil.generateViewId()
                    }
                }
                rlMain!!.addView(leftCustomView, leftInnerParams)
            }
        }
    }

    /**
     * 初始化主视图右边部分
     * -- add: adaptive RTL
     *
     * @param context 上下文
     */
    private fun initMainRightViews(context: Context) {
        val rightInnerParams =
            LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        rightInnerParams.addRule(ALIGN_PARENT_END)
        rightInnerParams.addRule(CENTER_VERTICAL)
        when (rightType) {
            TYPE_RIGHT_TEXT_VIEW -> { // 初始化右边TextView
                rightTextView = TextView(context)
                rightTextView!!.id = ViewUtil.generateViewId()
                rightTextView!!.text = rightText
                rightTextView!!.setTextColor(rightTextColor)
                rightTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize)
                rightTextView!!.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                rightTextView!!.isSingleLine = true
                rightTextView!!.setPadding(PADDING_16, 0, PADDING_16, 0)
                rightTextView!!.setOnClickListener(this)
                rlMain!!.addView(rightTextView, rightInnerParams)
            }
            TYPE_RIGHT_IMAGE_BUTTON -> { // 初始化右边ImageBtn
                rightImageButton = ImageButton(context)
                rightImageButton!!.id = ViewUtil.generateViewId()
                rightImageButton!!.setImageResource(rightImageResource)
                rightImageButton!!.setBackgroundColor(Color.TRANSPARENT)
                rightImageButton!!.scaleType = ImageView.ScaleType.CENTER_INSIDE
                rightImageButton!!.setPadding(PADDING_16, 0, PADDING_16, 0)
                rightImageButton!!.setOnClickListener(this)
                rlMain!!.addView(rightImageButton, rightInnerParams)
            }
            TYPE_RIGHT_CUSTOM_VIEW -> { // 初始化自定义view
                rightCustomView =
                    LayoutInflater.from(context).inflate(rightCustomViewRes, rlMain, false)
                rightCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = ViewUtil.generateViewId()
                    }
                }
                rlMain!!.addView(rightCustomView, rightInnerParams)
            }
        }
    }

    /**
     * 初始化主视图中间部分
     *
     * @param context 上下文
     */
    private fun initMainCenterViews(context: Context) {
        when (centerType) {
            TYPE_CENTER_TEXT_VIEW -> {
                // 初始化中间子布局
                centerLayout = LinearLayout(context)
                centerLayout!!.id = ViewUtil.generateViewId()
                centerLayout!!.gravity = Gravity.CENTER
                centerLayout!!.orientation = LinearLayout.VERTICAL
                centerLayout!!.setOnClickListener(this)
                val centerParams =
                    LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                centerParams.marginStart = PADDING_16
                centerParams.marginEnd = PADDING_16
                centerParams.addRule(CENTER_IN_PARENT)
                rlMain!!.addView(centerLayout, centerParams)
                // 初始化标题栏TextView
                centerTextView = TextView(context)
                centerTextView!!.text = centerText
                centerTextView!!.setTextColor(centerTextColor)
                centerTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerTextSize)
                centerTextView!!.gravity = Gravity.CENTER
                centerTextView!!.isSingleLine = true
                // 字体加粗
                centerTextView!!.paint.isFakeBoldText = true
                // 设置跑马灯效果
                centerTextView!!.maxWidth =
                    (ScreenUtil.screenWidth * 3 / 5.0).toInt()
                if (centerTextMarquee) {
                    centerTextView!!.ellipsize = TextUtils.TruncateAt.MARQUEE
                    centerTextView!!.marqueeRepeatLimit = -1
                    centerTextView!!.requestFocus()
                    centerTextView!!.isSelected = true
                }
                val centerTextParams =
                    LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                centerLayout!!.addView(centerTextView, centerTextParams)
                // 初始化进度条, 显示于标题栏左边
                progressCenter = ProgressBar(context)
                progressCenter!!.indeterminateDrawable =
                    resources.getDrawable(R.drawable.common_titlebar_progress_draw)
                progressCenter!!.visibility = View.GONE
                val progressWidth = SizeUtil.dp2px(18f)
                val progressParams =
                    LayoutParams(progressWidth, progressWidth)
                progressParams.addRule(CENTER_VERTICAL)
                progressParams.addRule(START_OF, centerLayout!!.id)
                rlMain!!.addView(progressCenter, progressParams)
                // 初始化副标题栏
                centerSubTextView = TextView(context)
                centerSubTextView!!.text = centerSubText
                centerSubTextView!!.setTextColor(centerSubTextColor)
                centerSubTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerSubTextSize)
                centerSubTextView!!.gravity = Gravity.CENTER
                centerSubTextView!!.isSingleLine = true
                if (TextUtils.isEmpty(centerSubText)) {
                    centerSubTextView!!.visibility = View.GONE
                }
                val centerSubTextParams =
                    LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                centerLayout!!.addView(centerSubTextView, centerSubTextParams)
            }
            TYPE_CENTER_SEARCH_VIEW -> {
                // 初始化通用搜索框
                centerSearchView = RelativeLayout(context)
                centerSearchView!!.setBackgroundResource(centerSearchBgResource)
                val centerParams =
                    LayoutParams(MATCH_PARENT, MATCH_PARENT)
                // 设置边距
                centerParams.topMargin = SizeUtil.dp2px(7f)
                centerParams.bottomMargin = SizeUtil.dp2px(7f)
                // 根据左边的布局类型来设置边距,布局依赖规则
                when (leftType) {
                    TYPE_LEFT_TEXT_VIEW -> {
                        centerParams.addRule(END_OF, leftTextView!!.id)
                        centerParams.marginStart = PADDING_5
                    }
                    TYPE_LEFT_IMAGE_BUTTON -> {
                        centerParams.addRule(END_OF, leftImageButton!!.id)
                        centerParams.marginStart = PADDING_5
                    }
                    TYPE_LEFT_CUSTOM_VIEW -> {
                        centerParams.addRule(END_OF, leftCustomView!!.id)
                        centerParams.marginStart = PADDING_5
                    }
                    else -> {
                        centerParams.marginStart = PADDING_16
                    }
                }
                // 根据右边的布局类型来设置边距,布局依赖规则
                when (rightType) {
                    TYPE_RIGHT_TEXT_VIEW -> {
                        centerParams.addRule(START_OF, rightTextView!!.id)
                        centerParams.marginEnd = PADDING_5
                    }
                    TYPE_RIGHT_IMAGE_BUTTON -> {
                        centerParams.addRule(START_OF, rightImageButton!!.id)
                        centerParams.marginEnd = PADDING_5
                    }
                    TYPE_RIGHT_CUSTOM_VIEW -> {
                        centerParams.addRule(START_OF, rightCustomView!!.id)
                        centerParams.marginEnd = PADDING_5
                    }
                    else -> {
                        centerParams.marginEnd = PADDING_16
                    }
                }
                rlMain!!.addView(centerSearchView, centerParams)
                // 初始化搜索框搜索ImageView
                centerSearchLeftImageView = ImageView(context)
                centerSearchLeftImageView!!.id = ViewUtil.generateViewId()
                centerSearchLeftImageView!!.setOnClickListener(this)
                val searchIconWidth = SizeUtil.dp2px(15f)
                val searchParams =
                    LayoutParams(searchIconWidth, searchIconWidth)
                searchParams.addRule(CENTER_VERTICAL)
                searchParams.addRule(ALIGN_PARENT_START)
                searchParams.marginStart = PADDING_16
                centerSearchView!!.addView(centerSearchLeftImageView, searchParams)
                centerSearchLeftImageView!!.setImageResource(R.drawable.common_titlebar_search_normal)
                // 初始化搜索框语音ImageView
                centerSearchRightImageView = ImageView(context)
                centerSearchRightImageView!!.id = ViewUtil.generateViewId()
                centerSearchRightImageView!!.setOnClickListener(this)
                val voiceParams =
                    LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                voiceParams.addRule(CENTER_VERTICAL)
                voiceParams.addRule(ALIGN_PARENT_END)
                voiceParams.marginEnd = PADDING_16
                centerSearchView!!.addView(centerSearchRightImageView, voiceParams)
                if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) {
                    centerSearchRightImageView!!.setImageResource(R.drawable.common_titlebar_voice)
                } else {
                    centerSearchRightImageView!!.setImageResource(R.drawable.common_titlebar_delete_normal)
                    centerSearchRightImageView!!.visibility = View.GONE
                }
                // 初始化文字输入框
                centerSearchEditText = EditText(context)
                centerSearchEditText!!.setBackgroundColor(Color.TRANSPARENT)
                centerSearchEditText!!.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                centerSearchEditText!!.hint = resources.getString(R.string.titlebar_search_hint)
                centerSearchEditText!!.setTextColor(Color.parseColor("#666666"))
                centerSearchEditText!!.setHintTextColor(Color.parseColor("#999999"))
                centerSearchEditText!!.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    SizeUtil.dp2px(14f).toFloat()
                )
                centerSearchEditText!!.setPadding(PADDING_5, 0, PADDING_5, 0)
                if (!centerSearchEditable) {
                    centerSearchEditText!!.isCursorVisible = false
                    centerSearchEditText!!.clearFocus()
                    centerSearchEditText!!.isFocusable = false
                    centerSearchEditText!!.setOnClickListener(this)
                }
                centerSearchEditText!!.isCursorVisible = false
                centerSearchEditText!!.isSingleLine = true
                centerSearchEditText!!.ellipsize = TextUtils.TruncateAt.END
                centerSearchEditText!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
                centerSearchEditText!!.addTextChangedListener(centerSearchWatcher)
                centerSearchEditText!!.onFocusChangeListener = focusChangeListener
                centerSearchEditText!!.setOnEditorActionListener(editorActionListener)
                centerSearchEditText!!.setOnClickListener {
                    centerSearchEditText!!.isCursorVisible = true
                }
                val searchHintParams =
                    LayoutParams(MATCH_PARENT, MATCH_PARENT)
                searchHintParams.addRule(END_OF, centerSearchLeftImageView!!.id)
                searchHintParams.addRule(START_OF, centerSearchRightImageView!!.id)
                searchHintParams.addRule(CENTER_VERTICAL)
                searchHintParams.marginStart = PADDING_5
                searchHintParams.marginEnd = PADDING_5
                centerSearchView!!.addView(centerSearchEditText, searchHintParams)
            }
            TYPE_CENTER_CUSTOM_VIEW -> { // 初始化中间自定义布局
                centerCustomView =
                    LayoutInflater.from(context).inflate(centerCustomViewRes, rlMain, false)
                centerCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = ViewUtil.generateViewId()
                    }
                }
                val centerCustomParams =
                    LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                centerCustomParams.marginStart = PADDING_16
                centerCustomParams.marginEnd = PADDING_16
                centerCustomParams.addRule(CENTER_IN_PARENT)
                rlMain!!.addView(centerCustomView, centerCustomParams)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUpImmersionTitleBar()
    }

    private fun setUpImmersionTitleBar() {
        val window = window ?: return
        // 设置状态栏背景透明
        transparentStatusBar(window)
        // 设置图标主题
        if (statusBarMode == 0) {
            setDarkMode(window)
        } else {
            setLightMode(window)
        }
    }

    private val window: Window?
        get() {
            val context = context
            val activity: Activity
            activity = if (context is Activity) {
                context
            } else {
                (context as ContextWrapper).baseContext as Activity
            }
            return activity.window
        }

    private val centerSearchWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) = when (centerSearchRightType) {
            TYPE_CENTER_SEARCH_RIGHT_VOICE -> {
                when {
                    TextUtils.isEmpty(s) -> {
                        centerSearchRightImageView!!.setImageResource(R.drawable.common_titlebar_voice)
                    }
                    else -> {
                        centerSearchRightImageView!!.setImageResource(R.drawable.common_titlebar_delete_normal)
                    }
                }
            }
            else -> {
                when {
                    TextUtils.isEmpty(s) -> {
                        centerSearchRightImageView!!.visibility = View.GONE
                    }
                    else -> {
                        centerSearchRightImageView!!.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    private val focusChangeListener = OnFocusChangeListener { v, hasFocus ->
        if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_DELETE) {
            val input = centerSearchEditText!!.text.toString()
            if (hasFocus && !TextUtils.isEmpty(input)) {
                centerSearchRightImageView!!.visibility = View.VISIBLE
            } else {
                centerSearchRightImageView!!.visibility = View.GONE
            }
        }
    }
    private val editorActionListener = OnEditorActionListener { v, actionId, event ->
        if (listener != null && actionId == EditorInfo.IME_ACTION_SEARCH) {
            listener!!.onClicked(
                v,
                MotionAction.ACTION_SEARCH_SUBMIT,
                centerSearchEditText!!.text.toString()
            )
        }
        false
    }
    /**
     * 双击事件中，上次被点击时间
     */
    private var lastClickMillis: Long = 0

    override fun onClick(v: View) {
        if (listener == null) {
            return
        }
        when {
            v == centerLayout && doubleClickListener != null -> {
                val currentClickMillis = System.currentTimeMillis()
                if (currentClickMillis - lastClickMillis < 500) {
                    doubleClickListener!!.onDoubleClicked(v)
                }
                lastClickMillis = currentClickMillis
            }
            v == leftTextView -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_LEFT_TEXT, null
                )
            }
            v == leftImageButton -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_LEFT_BUTTON, null
                )
            }
            v == rightTextView -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_RIGHT_TEXT, null
                )
            }
            v == rightImageButton -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_RIGHT_BUTTON, null
                )
            }
            v == centerSearchEditText || v == centerSearchLeftImageView -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_SEARCH, null
                )
            }
            v == centerSearchRightImageView -> {
                centerSearchEditText!!.setText("")
                if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) { // 语音按钮被点击
                    listener!!.onClicked(
                        v,
                        MotionAction.ACTION_SEARCH_VOICE, null
                    )
                } else {
                    listener!!.onClicked(
                        v,
                        MotionAction.ACTION_SEARCH_DELETE, null
                    )
                }
            }
            v == centerTextView -> {
                listener!!.onClicked(
                    v,
                    MotionAction.ACTION_CENTER_TEXT, null
                )
            }
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    override fun setBackgroundColor(color: Int) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill!!.setBackgroundColor(color)
        }
        rlMain!!.setBackgroundColor(color)
    }

    /**
     * 设置背景图片
     *
     * @param resource
     */
    override fun setBackgroundResource(resource: Int) {
        setBackgroundColor(Color.TRANSPARENT)
        super.setBackgroundResource(resource)
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    fun setStatusBarColor(color: Int) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill!!.setBackgroundColor(color)
        }
    }

    /**
     * 是否填充状态栏
     *
     * @param show
     */
    fun showStatusBar(show: Boolean) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill!!.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    /**
     * 切换状态栏模式
     */
    fun toggleStatusBarMode() {
        val window = window ?: return
        transparentStatusBar(window)
        if (statusBarMode == 0) {
            statusBarMode = 1
            setLightMode(window)
        } else {
            statusBarMode = 0
            setDarkMode(window)
        }
    }

    /**
     * @param leftView
     */
    fun setLeftView(leftView: View) {
        if (leftView.id == View.NO_ID) {
            leftView.id = ViewUtil.generateViewId()
        }
        val leftInnerParams =
            LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        leftInnerParams.addRule(ALIGN_PARENT_START)
        leftInnerParams.addRule(CENTER_VERTICAL)
        rlMain!!.addView(leftView, leftInnerParams)
    }

    /**
     * @param centerView
     */
    fun setCenterView(centerView: View) {
        if (centerView.id == View.NO_ID) {
            centerView.id = ViewUtil.generateViewId()
        }
        val centerInnerParams =
            LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        centerInnerParams.addRule(CENTER_IN_PARENT)
        centerInnerParams.addRule(CENTER_VERTICAL)
        rlMain!!.addView(centerView, centerInnerParams)
    }

    /**
     * @param rightView
     */
    fun setRightView(rightView: View) {
        if (rightView.id == View.NO_ID) {
            rightView.id = ViewUtil.generateViewId()
        }
        val rightInnerParams =
            LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        rightInnerParams.addRule(ALIGN_PARENT_END)
        rightInnerParams.addRule(CENTER_VERTICAL)
        rlMain!!.addView(rightView, rightInnerParams)
    }

    /**
     * 显示中间进度条
     */
    fun showCenterProgress() {
        progressCenter!!.visibility = View.VISIBLE
    }

    /**
     * 隐藏中间进度条
     */
    fun dismissCenterProgress() {
        progressCenter!!.visibility = View.GONE
    }

    /**
     * 显示或隐藏输入法,centerType="searchView"模式下有效
     *
     * @return
     */
    fun showSoftInputKeyboard(show: Boolean) {
        if (centerSearchEditable && show) {
            centerSearchEditText!!.isFocusable = true
            centerSearchEditText!!.isFocusableInTouchMode = true
            centerSearchEditText!!.requestFocus()
            KeyboardUtil.openKeyboard(centerSearchEditText!!)
        } else {
            KeyboardUtil.closeKeyboard(centerSearchEditText!!)
        }
    }

    /**
     * 设置搜索框右边图标
     *
     * @param res
     */
    fun setSearchRightImageResource(res: Int) {
        if (centerSearchRightImageView != null) {
            centerSearchRightImageView!!.setImageResource(res)
        }
    }

    /**
     * 获取SearchView输入结果
     */
    val searchKey: String
        get() = if (centerSearchEditText != null) {
            centerSearchEditText!!.text.toString()
        } else ""

    /**
     * 设置点击事件监听
     *
     * @param listener
     */
    fun setListener(listener: OnTitleBarClickListener?) {
        this.listener = listener
    }

    /**
     * 设置双击监听
     */
    fun setDoubleClickListener(doubleClickListener: OnTitleBarDoubleClickListener?) {
        this.doubleClickListener = doubleClickListener
    }

    @Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS,
        AnnotationTarget.VALUE_PARAMETER
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class MotionAction {
        companion object {
            /**
             * 左边TextView被点击
             */
            var ACTION_LEFT_TEXT = 1
            /**
             * 左边ImageBtn被点击
             */
            var ACTION_LEFT_BUTTON = 2
            /**
             * 右边TextView被点击
             */
            var ACTION_RIGHT_TEXT = 3
            /**
             * 右边ImageBtn被点击
             */
            var ACTION_RIGHT_BUTTON = 4
            /**
             * 搜索框被点击,搜索框不可输入的状态下会被触发
             */
            var ACTION_SEARCH = 5
            /**
             * 搜索框输入状态下,键盘提交触发
             */
            var ACTION_SEARCH_SUBMIT = 6
            /**
             * 语音按钮被点击
             */
            var ACTION_SEARCH_VOICE = 7
            /**
             * 搜索删除按钮被点击
             */
            var ACTION_SEARCH_DELETE = 8
            /**
             * 中间文字点击
             */
            var ACTION_CENTER_TEXT = 9
        }
    }

    /**
     * 点击事件
     */
    interface OnTitleBarClickListener {
        /**
         * @param v
         * @param action 对应ACTION_XXX, 如ACTION_LEFT_TEXT
         * @param extra  中间为搜索框时,如果可输入,点击键盘的搜索按钮,会返回输入关键词
         */
        fun onClicked(
            v: View?, @MotionAction action: Int,
            extra: String?
        )
    }

    /**
     * 标题栏双击事件监听
     */
    interface OnTitleBarDoubleClickListener {
        fun onDoubleClicked(v: View?)
    }

    companion object {
        private const val TYPE_LEFT_NONE = 0
        private const val TYPE_LEFT_TEXT_VIEW = 1
        private const val TYPE_LEFT_IMAGE_BUTTON = 2
        private const val TYPE_LEFT_CUSTOM_VIEW = 3
        private const val TYPE_RIGHT_NONE = 0
        private const val TYPE_RIGHT_TEXT_VIEW = 1
        private const val TYPE_RIGHT_IMAGE_BUTTON = 2
        private const val TYPE_RIGHT_CUSTOM_VIEW = 3
        private const val TYPE_CENTER_NONE = 0
        private const val TYPE_CENTER_TEXT_VIEW = 1
        private const val TYPE_CENTER_SEARCH_VIEW = 2
        private const val TYPE_CENTER_CUSTOM_VIEW = 3
        private const val TYPE_CENTER_SEARCH_RIGHT_VOICE = 0
        private const val TYPE_CENTER_SEARCH_RIGHT_DELETE = 1
    }

    init {
        loadAttributes(context, attrs)
        initGlobalViews(context)
        initMainViews(context)
    }
}