package com.wkz.adapter.swipehelper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.R
import java.util.*


/**
 * Created by WANG on 2018/4/27.
 */

class WItemTouchHelperPlus
/**
 * Creates an WItemTouchHelperPlus that will work with the given Callback.
 *
 *
 * You can attach WItemTouchHelperPlus to a RecyclerView via
 * [.attachToRecyclerView]. Upon attaching, it will add an item decoration,
 * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
 *
 * @param callback The Callback which controls the behavior of this touch helper.
 */
(
        /**
         * Developer callback which controls the behavior of WItemTouchHelperPlus.
         */
        internal var mCallback: WItemTouchHelperPlus.Callback) : RecyclerView.ItemDecoration(), RecyclerView.OnChildAttachStateChangeListener {

    /**
     * Views, whose state should be cleared after they are detached from RecyclerView.
     * This is necessary after swipe dismissing an item. We wait until animator finishes its job
     * to clean these views.
     */
    internal val mPendingCleanup: MutableList<View> = ArrayList()

    /**
     * Re-use array to calculate dx dy for a ViewHolder
     */
    private val mTmpPosition = FloatArray(2)

    /**
     * Currently selected view holder
     */
    internal var mSelected: RecyclerView.ViewHolder? = null

    /**
     * Previous selected view holder
     */
    internal var mPreOpened: RecyclerView.ViewHolder? = null

    /**
     * The reference coordinates for the action start. For drag & drop, this is the time long
     * press is completed vs for swipe, this is the initial touch point.
     */
    internal var mInitialTouchX: Float = 0.toFloat()

    internal var mInitialTouchY: Float = 0.toFloat()

    /**
     * Set when WItemTouchHelperPlus is assigned to a RecyclerView.
     */
    internal var mSwipeEscapeVelocity: Float = 0.toFloat()

    /**
     * Set when WItemTouchHelperPlus is assigned to a RecyclerView.
     */
    internal var mMaxSwipeVelocity: Float = 0.toFloat()

    /**
     * The diff between the last event and initial touch.
     */
    internal var mDx: Float = 0.toFloat()

    internal var mDy: Float = 0.toFloat()

    /**
     * The coordinates of the selected view at the time it is selected. We record these values
     * when action starts so that we can consistently position it even if LayoutManager moves the
     * View.
     */
    internal var mSelectedStartX: Float = 0.toFloat()

    internal var mSelectedStartY: Float = 0.toFloat()

    /**
     * The pointer we are tracking.
     */
    internal var mActivePointerId = ACTIVE_POINTER_ID_NONE

    /**
     * Current mode.
     */
    internal var mActionState = ACTION_STATE_IDLE

    /**
     * The direction flags obtained from unmasking
     * [WItemTouchHelperPlus.Callback.getAbsoluteMovementFlags] for the current
     * action state.
     */
    internal var mSelectedFlags: Int = 0

    /**
     * When a View is dragged or swiped and needs to go back to where it was, we create a Recover
     * Animation and animate it to its location using this custom Animator, instead of using
     * framework Animators.
     * Using framework animators has the side effect of clashing with ItemAnimator, creating
     * jumpy UIs.
     */
    internal var mRecoverAnimations: MutableList<RecoverAnimation> = ArrayList()

    private var mSlop: Int = 0

    internal var mRecyclerView: RecyclerView? = null

    /**
     * When user drags a view to the edge, we start scrolling the LayoutManager as long as View
     * is partially out of bounds.
     */
    internal val mScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mSelected != null && scrollIfNecessary()) {
                if (mSelected != null) { //it might be lost during scrolling
                    moveIfNecessary(mSelected)
                }
                mRecyclerView!!.removeCallbacks(this)
                ViewCompat.postOnAnimation(mRecyclerView!!, this)
            }
        }
    }

    /**
     * Used for detecting fling swipe
     */
    internal var mVelocityTracker: VelocityTracker? = null

    //re-used list for selecting a swap target
    private var mSwapTargets: MutableList<RecyclerView.ViewHolder>? = null

    //re used for for sorting swap targets
    private var mDistances: MutableList<Int>? = null

    /**
     * If drag & drop is supported, we use child drawing order to bring them to front.
     */
    private var mChildDrawingOrderCallback: RecyclerView.ChildDrawingOrderCallback? = null

    /**
     * This keeps a reference to the child dragged by the user. Even after user stops dragging,
     * until view reaches its final position (end of recover animation), we keep a reference so
     * that it can be drawn above other children.
     */
    internal var mOverdrawChild: View? = null

    /**
     * We cache the position of the overdraw child to avoid recalculating it each time child
     * position callback is called. This value is invalidated whenever a child is attached or
     * detached.
     */
    internal var mOverdrawChildPosition = -1

    /**
     * Used to detect long press.
     */
    internal var mGestureDetector: GestureDetectorCompat? = null

    /**
     * Is the last open item closed
     */
    private var closePreItem = false

    /**
     * Whether the current entry has been clicked
     */
    private var mClick: Boolean = false

    internal var mLastX = 0f


    private val mOnItemTouchListener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
            mGestureDetector!!.onTouchEvent(event)
            if (DEBUG) {
                Log.d(TAG, "intercept: x:" + event.x + ",y:" + event.y + ", " + event)
            }
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_DOWN) {
                mClick = true
                mLastX = event.x
                mActivePointerId = event.getPointerId(0)
                mInitialTouchX = event.x
                mInitialTouchY = event.y
                obtainVelocityTracker()
                if (mSelected == null) {
                    val animation = findAnimation(event)
                    if (animation != null) {
                        //这里是当item侧滑已经展开的时候.如果animation不为null 说明Item的侧滑是展开的状态  需要进行关闭
                        mInitialTouchX -= animation.mX
                        mInitialTouchY -= animation.mY
                        endRecoverAnimation(animation.mViewHolder, true)
                        if (mPendingCleanup.remove(animation.mViewHolder.itemView)) {
                            mCallback.clearView(mRecyclerView, animation.mViewHolder)
                        }
                        select(animation.mViewHolder, animation.mActionState)
                        updateDxDy(event, mSelectedFlags, 0)
                    } else {
                        if (null != mPreOpened) {
                            closePreItem = true
                            closeOpenedPreItem(mPreOpened)
                            return true
                        }
                    }
                }
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                closePreItem = false
                //这边是新增的
                if (mClick && action == MotionEvent.ACTION_UP) {
                    doChildClickEvent(event.rawX, event.rawY)
                }
                mClick = false
                mActivePointerId = ACTIVE_POINTER_ID_NONE
                select(null, ACTION_STATE_IDLE)
            } else if (mActivePointerId != ACTIVE_POINTER_ID_NONE && !closePreItem) {
                // in a non scroll orientation, if distance change is above threshold, we
                // can select the item
                val index = event.findPointerIndex(mActivePointerId)
                if (DEBUG) {
                    Log.d(TAG, "pointer index $index")
                }
                if (index >= 0) {
                    checkSelectForSwipe(action, event, index)
                }
            }
            if (mVelocityTracker != null) {
                mVelocityTracker!!.addMovement(event)
            }
            return mSelected != null
        }

        /**
         * 判断当前点击的位置是否为view并且该View实现了OnClickListener事件
         * @param x
         * @param y
         */
        private fun doChildClickEvent(x: Float, y: Float) {
            if (mSelected == null) return
            var consumeEventView: View? = mSelected!!.itemView
            if (consumeEventView is ViewGroup) {
                consumeEventView = findConsumeView((consumeEventView as ViewGroup?)!!, x, y)
            }
            consumeEventView?.performClick()
        }

        /**
         * 查找view
         * @param parent 父容器
         * @param x  点击事件的x坐标
         * @param y  点击事件的y坐标
         * @return
         */
        private fun findConsumeView(parent: ViewGroup, x: Float, y: Float): View? {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                if (child is ViewGroup && child.getVisibility() == View.VISIBLE) {
                    val view = findConsumeView(child, x, y)
                    if (view != null) {
                        return view
                    }
                } else {
                    if (isInBoundsClickable(x.toInt(), y.toInt(), child)) return child
                }
            }
            return if (isInBoundsClickable(x.toInt(), y.toInt(), parent)) parent else null
        }

        /**
         * 边界判断
         * @param x
         * @param y
         * @param child 再点击事件下找到的view
         * @return
         */
        private fun isInBoundsClickable(x: Int, y: Int, child: View): Boolean {
            val location = IntArray(2)
            child.getLocationOnScreen(location)
            val rect = Rect(location[0], location[1], location[0] + child.width, location[1] + child.height)
            return if (rect.contains(x, y) && ViewCompat.hasOnClickListeners(child)
                    && child.visibility == View.VISIBLE) {
                true
            } else false
        }

        override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
            mGestureDetector!!.onTouchEvent(event)
            if (mVelocityTracker != null) {
                mVelocityTracker!!.addMovement(event)
            }
            if (mActivePointerId == ACTIVE_POINTER_ID_NONE) {
                return
            }
            val action = event.actionMasked
            val activePointerIndex = event.findPointerIndex(mActivePointerId)
            if (activePointerIndex >= 0) {
                checkSelectForSwipe(action, event, activePointerIndex)
            }
            val viewHolder = mSelected ?: return
            when (action) {
                MotionEvent.ACTION_MOVE -> {
                    // Find the index of the active pointer and fetch its position
                    if (activePointerIndex >= 0) {
                        if (Math.abs(event.x - mLastX) > mSlop) mClick = false
                        mLastX = event.x
                        updateDxDy(event, mSelectedFlags, activePointerIndex)
                        moveIfNecessary(viewHolder)
                        mRecyclerView!!.removeCallbacks(mScrollRunnable)
                        mScrollRunnable.run()
                        mRecyclerView!!.invalidate()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mVelocityTracker != null) {
                        mVelocityTracker!!.clear()
                    }

                    if (mClick) {
                        doChildClickEvent(event.rawX, event.rawY)
                    }
                    mClick = false
                    select(null, ACTION_STATE_IDLE)
                    mActivePointerId = ACTIVE_POINTER_ID_NONE
                }
                // fall through
                MotionEvent.ACTION_UP -> {
                    if (mClick) {
                        doChildClickEvent(event.rawX, event.rawY)
                    }
                    mClick = false
                    select(null, ACTION_STATE_IDLE)
                    mActivePointerId = ACTIVE_POINTER_ID_NONE
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    mClick = false
                    val pointerIndex = event.actionIndex
                    val pointerId = event.getPointerId(pointerIndex)
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        mActivePointerId = event.getPointerId(newPointerIndex)
                        updateDxDy(event, mSelectedFlags, pointerIndex)
                    }
                }
                else -> mClick = false
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            if (!disallowIntercept) {
                return
            }
            select(null, ACTION_STATE_IDLE)
        }
    }


    /**
     * Temporary rect instance that is used when we need to lookup Item decorations.
     */
    private var mTmpRect: Rect? = null

    /**
     * When user started to drag scroll. Reset when we don't scroll
     */
    private var mDragScrollStartTimeInMs: Long = 0

    private val swipeWidth: Float
        get() = if (mSelected is Expanding) {
            (mSelected as Expanding).actionWidth
        } else mRecyclerView!!.width.toFloat()

    /**
     * 判断当前侧滑布局的标记
     * [.SLIDE_ITEM_TYPE_ITEMVIEW]
     */
    private fun slideItemTypeIsItemView(): Boolean {
        return SLIDE_ITEM_TYPE_ITEMVIEW == mCallback.itemSlideType
    }

    /**
     * 判断当前侧滑布局的标记
     * [.SLIDE_ITEM_TYPE_SLIDECONTAINER]
     */
    private fun slideItemTypeIsContainerView(): Boolean {
        return SLIDE_ITEM_TYPE_SLIDECONTAINER == mCallback.itemSlideType
    }

    private fun hitTest(child: View, x: Float, y: Float, left: Float, top: Float, vh: RecyclerView.ViewHolder): Boolean {
        return (x >= left
                && x <= left + child.width
                && y >= top
                && y <= top + child.height)
    }

    /**
     * Attaches the WItemTouchHelperPlus to the provided RecyclerView. If TouchHelper is already
     * attached to a RecyclerView, it will first detach from the previous one. You can call this
     * method with `null` to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     * `null` if you want to remove WItemTouchHelperPlus from the current
     * RecyclerView.
     */
    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (mRecyclerView === recyclerView) {
            return  // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks()
        }
        mRecyclerView = recyclerView
        if (mRecyclerView != null) {
            val resources = recyclerView!!.resources
            mSwipeEscapeVelocity = resources
                    .getDimension(R.dimen.item_touch_helper_swipe_escape_velocity)
            mMaxSwipeVelocity = resources
                    .getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity)
            setupCallbacks()
            mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING && mPreOpened != null) {
                        closeOpenedPreItem(mPreOpened)
                    }
                }
            })
        }
    }

    /**
     * 关闭一个打开的Item
     *
     * @param viewHolder 要关闭的Item的ViewHolder
     */
    private fun closeOpenedPreItem(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder == null) return
        val view = getItemFrontView(viewHolder) ?: return
        val objectAnimator = ObjectAnimator.ofFloat(view, "translationX", view.translationX, 0f)
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                if (mPreOpened != null) {
                    if (mPendingCleanup.remove(mPreOpened!!.itemView)) {
                        mCallback.clearView(mRecyclerView, mPreOpened!!)
                    }
                }
                endRecoverAnimation(mPreOpened, true)
                mPreOpened = mSelected
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mRecoverAnimations.clear()
            }
        })

        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.start()
    }

    /**
     * @param viewHolder
     * @return
     */
    fun getItemFrontView(viewHolder: RecyclerView.ViewHolder?): View? {
        if (viewHolder == null) return null
        if (viewHolder.itemView is ViewGroup) {
            val viewGroup = viewHolder.itemView as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                val childAt = viewGroup.getChildAt(i)
                childAt.tag?.let {
                    val tag = childAt.tag as String
                    /*
                    这个标记必须再xml布局里面.
                    <RelativeLayout
                      android:id="@+id/slide_itemView"
                      android:clipChildren="false"
                      android:tag="slide_flag"
                      android:layout_width="match_parent"
                      android:layout_height="match_parent">
                     */
                    if ("slide_flag" == tag) {
                        return childAt
                    }
                }
            }
            return viewHolder.itemView
        } else {
            return viewHolder.itemView
        }
    }

    private fun setupCallbacks() {
        val vc = ViewConfiguration.get(mRecyclerView!!.context)
        mSlop = vc.scaledTouchSlop
        mRecyclerView!!.addItemDecoration(this)
        mRecyclerView!!.addOnItemTouchListener(mOnItemTouchListener)
        mRecyclerView!!.addOnChildAttachStateChangeListener(this)
        initGestureDetector()
    }

    private fun destroyCallbacks() {
        mRecyclerView!!.removeItemDecoration(this)
        mRecyclerView!!.removeOnItemTouchListener(mOnItemTouchListener)
        mRecyclerView!!.removeOnChildAttachStateChangeListener(this)
        // clean all attached
        val recoverAnimSize = mRecoverAnimations.size
        for (i in recoverAnimSize - 1 downTo 0) {
            val recoverAnimation = mRecoverAnimations[0]
            mCallback.clearView(mRecyclerView, recoverAnimation.mViewHolder)
        }
        mRecoverAnimations.clear()
        mOverdrawChild = null
        mOverdrawChildPosition = -1
        releaseVelocityTracker()
    }

    private fun initGestureDetector() {
        if (mGestureDetector != null) {
            return
        }
        mGestureDetector = GestureDetectorCompat(mRecyclerView!!.context,
                WItemTouchHelperPlusGestureListener())
    }

    private fun getSelectedDxDy(outPosition: FloatArray) {
        if (mSelectedFlags and (LEFT or RIGHT) != 0) {
            outPosition[0] = mSelectedStartX + mDx - mSelected!!.itemView.left
        } else {
            outPosition[0] = mSelected!!.itemView.translationX
        }
        if (mSelectedFlags and (UP or DOWN) != 0) {
            outPosition[1] = mSelectedStartY + mDy - mSelected!!.itemView.top
        } else {
            outPosition[1] = mSelected!!.itemView.translationY
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var dx = 0f
        var dy = 0f
        if (mSelected != null) {
            getSelectedDxDy(mTmpPosition)
            dx = mTmpPosition[0]
            dy = mTmpPosition[1]
        }
        mCallback.onDrawOver(c, parent, mSelected,
                mRecoverAnimations, mActionState, dx, dy)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // we don't know if RV changed something so we should invalidate this index.
        mOverdrawChildPosition = -1
        var dx = 0f
        var dy = 0f
        if (mSelected != null) {
            getSelectedDxDy(mTmpPosition)
            dx = mTmpPosition[0]
            dy = mTmpPosition[1]
        }
        mCallback.onDraw(c, parent, mSelected,
                mRecoverAnimations, mActionState, dx, dy)
    }

    /**
     * Starts dragging or swiping the given View. Call with null if you want to clear it.
     *
     * @param selected    The ViewHolder to drag or swipe. Can be null if you want to cancel the
     * current action
     * @param actionState The type of action
     */
    internal fun select(selected: RecyclerView.ViewHolder?, actionState: Int) {
        if (selected === mSelected && actionState == mActionState) {
            return
        }
        mDragScrollStartTimeInMs = java.lang.Long.MIN_VALUE
        val prevActionState = mActionState
        // prevent duplicate animations
        endRecoverAnimation(selected, true)
        mActionState = actionState
        if (actionState == ACTION_STATE_DRAG) {
            // we remove after animation is complete. this means we only elevate the last drag
            // child but that should perform good enough as it is very hard to start dragging a
            // new child before the previous one settles.
            mOverdrawChild = selected!!.itemView
            addChildDrawingOrderCallback()
        }
        val actionStateMask = (1 shl DIRECTION_FLAG_COUNT + DIRECTION_FLAG_COUNT * actionState) - 1
        var preventLayout = false

        if (mSelected != null) {
            val prevSelected = mSelected
            if (prevSelected!!.itemView.parent != null) {
                val swipeDir = if (prevActionState == ACTION_STATE_DRAG)
                    0
                else
                    swipeIfNecessary(prevSelected)
                releaseVelocityTracker()
                // find where we should animate to
                val targetTranslateX: Float
                val targetTranslateY: Float
                val animationType: Int
                when (swipeDir) {
                    LEFT, RIGHT, START, END -> {
                        targetTranslateY = 0f
                        val swipeWidth = swipeWidth
                        targetTranslateX = Math.signum(mDx) * swipeWidth
                    }
                    UP, DOWN -> {
                        targetTranslateX = 0f
                        targetTranslateY = Math.signum(mDy) * mRecyclerView!!.height
                    }
                    else -> {
                        targetTranslateX = 0f
                        targetTranslateY = 0f
                    }
                }
                if (prevActionState == ACTION_STATE_DRAG) {
                    animationType = ANIMATION_TYPE_DRAG
                } else if (swipeDir > 0) {
                    animationType = ANIMATION_TYPE_SWIPE_SUCCESS
                } else {
                    animationType = ANIMATION_TYPE_SWIPE_CANCEL
                }
                getSelectedDxDy(mTmpPosition)
                val currentTranslateX = mTmpPosition[0]
                val currentTranslateY = mTmpPosition[1]
                val rv = object : WItemTouchHelperPlus.RecoverAnimation(prevSelected, animationType,
                        prevActionState, currentTranslateX, currentTranslateY,
                        targetTranslateX, targetTranslateY) {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (this.mOverridden) {
                            return
                        }
                        if (swipeDir <= 0) {
                            mPreOpened = null
                            // this is a drag or failed swipe. recover immediately
                            mCallback.clearView(mRecyclerView, prevSelected)
                            // full cleanup will happen on onDrawOver
                        } else {
                            // wait until remove animation is complete.
                            mPendingCleanup.add(prevSelected.itemView)
                            mPreOpened = prevSelected
                            mIsPendingCleanup = true
                            if (swipeDir > 0) {
                                // Animation might be ended by other animators during a layout.
                                // We defer callback to avoid editing adapter during a layout.
                                postDispatchSwipe(this, swipeDir)
                            }
                        }
                        // removed from the list after it is drawn for the last time
                        if (mOverdrawChild === prevSelected.itemView) {
                            removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView)
                        }
                    }

                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        Log.e("WANG", "WItemTouchHelperPlus.onAnimationStart    Recover   ")
                    }
                }
                val duration = mCallback.getAnimationDuration(mRecyclerView!!, animationType,
                        targetTranslateX - currentTranslateX, targetTranslateY - currentTranslateY)
                rv.setDuration(duration)
                mRecoverAnimations.add(rv)
                rv.start()
                preventLayout = true
            } else {
                removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView)
                mCallback.clearView(mRecyclerView, prevSelected)
            }
            mSelected = null
        }
        if (selected != null) {
            mSelectedFlags = mCallback.getAbsoluteMovementFlags(mRecyclerView, selected) and actionStateMask shr mActionState * DIRECTION_FLAG_COUNT
            mSelectedStartX = selected.itemView.left.toFloat()
            mSelectedStartY = selected.itemView.top.toFloat()
            mSelected = selected

            if (actionState == ACTION_STATE_DRAG) {
                mSelected!!.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
        val rvParent = mRecyclerView!!.parent
        rvParent?.requestDisallowInterceptTouchEvent(mSelected != null)
        if (!preventLayout) {
            mRecyclerView!!.layoutManager!!.requestSimpleAnimationsInNextLayout()
        }
        mCallback.onSelectedChanged(mSelected, mActionState)
        mRecyclerView!!.invalidate()
    }

    internal fun postDispatchSwipe(anim: WItemTouchHelperPlus.RecoverAnimation, swipeDir: Int) {
        // wait until animations are complete.
        mRecyclerView!!.post(object : Runnable {
            override fun run() {
                if (mRecyclerView != null && mRecyclerView!!.isAttachedToWindow
                        && !anim.mOverridden
                        && anim.mViewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                    val animator = mRecyclerView!!.itemAnimator
                    // if animator is running or we have other active recover animations, we try
                    // not to call onSwiped because DefaultItemAnimator is not good at merging
                    // animations. Instead, we wait and batch.
                    if ((animator == null || !animator.isRunning(null)) && !hasRunningRecoverAnim()) {
                        mCallback.onSwiped(anim.mViewHolder, swipeDir)
                    } else {
                        mRecyclerView!!.post(this)
                    }
                }
            }
        })
    }

    internal fun hasRunningRecoverAnim(): Boolean {
        val size = mRecoverAnimations.size
        for (i in 0 until size) {
            if (!mRecoverAnimations[i].mEnded) {
                return true
            }
        }
        return false
    }

    /**
     * If user drags the view to the edge, trigger a scroll if necessary.
     */
    internal fun scrollIfNecessary(): Boolean {
        if (mSelected == null) {
            mDragScrollStartTimeInMs = java.lang.Long.MIN_VALUE
            return false
        }
        val now = System.currentTimeMillis()
        val scrollDuration = if (mDragScrollStartTimeInMs == java.lang.Long.MIN_VALUE)
            0
        else
            now - mDragScrollStartTimeInMs
        val lm = mRecyclerView!!.layoutManager
        if (mTmpRect == null) {
            mTmpRect = Rect()
        }
        var scrollX = 0
        var scrollY = 0
        lm!!.calculateItemDecorationsForChild(mSelected!!.itemView, mTmpRect!!)
        if (lm.canScrollHorizontally()) {
            val curX = (mSelectedStartX + mDx).toInt()
            val leftDiff = curX - mTmpRect!!.left - mRecyclerView!!.paddingLeft
            if (mDx < 0 && leftDiff < 0) {
                scrollX = leftDiff
            } else if (mDx > 0) {
                val rightDiff = curX + mSelected!!.itemView.width + mTmpRect!!.right - (mRecyclerView!!.width - mRecyclerView!!.paddingRight)
                if (rightDiff > 0) {
                    scrollX = rightDiff
                }
            }
        }
        if (lm.canScrollVertically()) {
            val curY = (mSelectedStartY + mDy).toInt()
            val topDiff = curY - mTmpRect!!.top - mRecyclerView!!.paddingTop
            if (mDy < 0 && topDiff < 0) {
                scrollY = topDiff
            } else if (mDy > 0) {
                val bottomDiff = curY + mSelected!!.itemView.height + mTmpRect!!.bottom - (mRecyclerView!!.height - mRecyclerView!!.paddingBottom)
                if (bottomDiff > 0) {
                    scrollY = bottomDiff
                }
            }
        }
        if (scrollX != 0) {
            scrollX = mCallback.interpolateOutOfBoundsScroll(mRecyclerView!!,
                    mSelected!!.itemView.width, scrollX,
                    mRecyclerView!!.width, scrollDuration)
        }
        if (scrollY != 0) {
            scrollY = mCallback.interpolateOutOfBoundsScroll(mRecyclerView!!,
                    mSelected!!.itemView.height, scrollY,
                    mRecyclerView!!.height, scrollDuration)
        }
        if (scrollX != 0 || scrollY != 0) {
            if (mDragScrollStartTimeInMs == java.lang.Long.MIN_VALUE) {
                mDragScrollStartTimeInMs = now
            }
            mRecyclerView!!.scrollBy(scrollX, scrollY)
            return true
        }
        mDragScrollStartTimeInMs = java.lang.Long.MIN_VALUE
        return false
    }

    private fun findSwapTargets(viewHolder: RecyclerView.ViewHolder): List<RecyclerView.ViewHolder> {
        if (mSwapTargets == null) {
            mSwapTargets = ArrayList()
            mDistances = ArrayList()
        } else {
            mSwapTargets!!.clear()
            mDistances!!.clear()
        }
        val margin = mCallback.boundingBoxMargin
        val left = Math.round(mSelectedStartX + mDx) - margin
        val top = Math.round(mSelectedStartY + mDy) - margin
        val right = left + viewHolder.itemView.width + 2 * margin
        val bottom = top + viewHolder.itemView.height + 2 * margin
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        val lm = mRecyclerView!!.layoutManager
        val childCount = lm!!.childCount
        for (i in 0 until childCount) {
            val other = lm.getChildAt(i)
            if (other === viewHolder.itemView) {
                continue //myself!
            }
            if (other!!.bottom < top || other.top > bottom
                    || other.right < left || other.left > right) {
                continue
            }
            val otherVh = mRecyclerView!!.getChildViewHolder(other)
            if (mCallback.canDropOver(mRecyclerView!!, mSelected, otherVh)) {
                // find the index to add
                val dx = Math.abs(centerX - (other.left + other.right) / 2)
                val dy = Math.abs(centerY - (other.top + other.bottom) / 2)
                val dist = dx * dx + dy * dy

                var pos = 0
                val cnt = mSwapTargets!!.size
                for (j in 0 until cnt) {
                    if (dist > mDistances!![j]) {
                        pos++
                    } else {
                        break
                    }
                }
                mSwapTargets!!.add(pos, otherVh)
                mDistances!!.add(pos, dist)
            }
        }
        return mSwapTargets as MutableList<RecyclerView.ViewHolder>
    }

    /**
     * Checks if we should swap w/ another view holder.
     */
    internal fun moveIfNecessary(viewHolder: RecyclerView.ViewHolder?) {
        if (mRecyclerView!!.isLayoutRequested) {
            return
        }
        if (mActionState != ACTION_STATE_DRAG) {
            return
        }

        val threshold = mCallback.getMoveThreshold(viewHolder)
        val x = (mSelectedStartX + mDx).toInt()
        val y = (mSelectedStartY + mDy).toInt()
        if (Math.abs(y - viewHolder!!.itemView.top) < viewHolder.itemView.height * threshold && Math.abs(x - viewHolder.itemView.left) < viewHolder.itemView.width * threshold) {
            return
        }
        val swapTargets = findSwapTargets(viewHolder)
        if (swapTargets.size == 0) {
            return
        }
        // may swap.
        val target = mCallback.chooseDropTarget(viewHolder, swapTargets, x, y)
        if (target == null) {
            mSwapTargets!!.clear()
            mDistances!!.clear()
            return
        }
        val toPosition = target.adapterPosition
        val fromPosition = viewHolder.adapterPosition
        if (mCallback.onMove(mRecyclerView!!, viewHolder, target)) {
            // keep target visible
            mCallback.onMoved(mRecyclerView!!, viewHolder, fromPosition,
                    target, toPosition, x, y)
        }
    }

    override fun onChildViewAttachedToWindow(view: View) {}

    override fun onChildViewDetachedFromWindow(view: View) {
        removeChildDrawingOrderCallbackIfNecessary(view)
        val holder = mRecyclerView!!.getChildViewHolder(view) ?: return
        if (mSelected != null && holder === mSelected) {
            select(null, ACTION_STATE_IDLE)
        } else {
            endRecoverAnimation(holder, false) // this may push it into pending cleanup list.
            if (mPendingCleanup.remove(holder.itemView)) {
                mCallback.clearView(mRecyclerView, holder)
            }
        }
    }

    /**
     * Returns the animation type or 0 if cannot be found.
     */
    internal fun endRecoverAnimation(viewHolder: RecyclerView.ViewHolder?, override: Boolean): Int {
        val recoverAnimSize = mRecoverAnimations.size
        for (i in recoverAnimSize - 1 downTo 0) {
            val anim = mRecoverAnimations[i]
            if (anim.mViewHolder === viewHolder) {
                anim.mOverridden = anim.mOverridden or override
                if (!anim.mEnded) {
                    anim.cancel()
                }
                mRecoverAnimations.removeAt(i)
                return anim.mAnimationType
            }
        }
        return 0
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.setEmpty()
    }

    internal fun obtainVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
        }
        mVelocityTracker = VelocityTracker.obtain()
    }

    private fun releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun findSwipedView(motionEvent: MotionEvent): RecyclerView.ViewHolder? {
        val lm = mRecyclerView!!.layoutManager
        if (mActivePointerId == ACTIVE_POINTER_ID_NONE) {
            return null
        }
        val pointerIndex = motionEvent.findPointerIndex(mActivePointerId)
        val dx = motionEvent.getX(pointerIndex) - mInitialTouchX
        val dy = motionEvent.getY(pointerIndex) - mInitialTouchY
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)

        if (absDx < mSlop && absDy < mSlop) {
            return null
        }
        if (absDx > absDy && lm!!.canScrollHorizontally()) {
            return null
        } else if (absDy > absDx && lm!!.canScrollVertically()) {
            return null
        }
        val child = findChildView(motionEvent) ?: return null
        return mRecyclerView!!.getChildViewHolder(child)
    }

    /**
     * Checks whether we should select a View for swiping.
     */
    internal fun checkSelectForSwipe(action: Int, motionEvent: MotionEvent, pointerIndex: Int): Boolean {
        if (mSelected != null || action != MotionEvent.ACTION_MOVE
                || mActionState == ACTION_STATE_DRAG || !mCallback.isItemViewSwipeEnabled) {
            return false
        }
        if (mRecyclerView!!.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
            return false
        }
        val vh = findSwipedView(motionEvent) ?: return false
        val movementFlags = mCallback.getAbsoluteMovementFlags(mRecyclerView, vh)

        val swipeFlags = movementFlags and ACTION_MODE_SWIPE_MASK shr DIRECTION_FLAG_COUNT * ACTION_STATE_SWIPE

        if (swipeFlags == 0) {
            return false
        }

        // mDx and mDy are only set in allowed directions. We use custom x/y here instead of
        // updateDxDy to avoid swiping if user moves more in the other direction
        val x = motionEvent.getX(pointerIndex)
        val y = motionEvent.getY(pointerIndex)

        // Calculate the distance moved
        val dx = x - mInitialTouchX
        val dy = y - mInitialTouchY
        // swipe target is chose w/o applying flags so it does not really check if swiping in that
        // direction is allowed. This why here, we use mDx mDy to check slope value again.
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)

        if (absDx < mSlop && absDy < mSlop) {
            return false
        }
        if (absDx > absDy) {
            if (dx < 0 && swipeFlags and LEFT == 0) {
                return false
            }
            if (dx > 0 && swipeFlags and RIGHT == 0) {
                return false
            }
        } else {
            if (dy < 0 && swipeFlags and UP == 0) {
                return false
            }
            if (dy > 0 && swipeFlags and DOWN == 0) {
                return false
            }
        }
        mDy = 0f
        mDx = mDy
        mActivePointerId = motionEvent.getPointerId(0)
        select(vh, ACTION_STATE_SWIPE)
        return true
    }

    internal fun findChildView(event: MotionEvent): View? {
        // first check elevated views, if none, then call RV
        val x = event.x
        val y = event.y
        if (mSelected != null) {
            val selectedView = mSelected!!.itemView
            if (hitTest(selectedView, x, y, mSelectedStartX + mDx, mSelectedStartY + mDy, mSelected!!)) {
                return selectedView
            }
        }
        for (i in mRecoverAnimations.indices.reversed()) {
            val anim = mRecoverAnimations[i]
            val view = anim.mViewHolder.itemView
            val hitTest = hitTest(view, x, y, anim.mX, anim.mY, anim.mViewHolder)
            if (hitTest) {
                return view
            }
        }
        return mRecyclerView!!.findChildViewUnder(x, y)
    }

    /**
     * Starts dragging the provided ViewHolder. By default, WItemTouchHelperPlus starts a drag when a
     * View is long pressed. You can disable that behavior by overriding
     * [WItemTouchHelperPlus.Callback.isLongPressDragEnabled].
     *
     *
     * For this method to work:
     *
     *  * The provided ViewHolder must be a child of the RecyclerView to which this
     * WItemTouchHelperPlus
     * is attached.
     *  * [WItemTouchHelperPlus.Callback] must have dragging enabled.
     *  * There must be a previous touch event that was reported to the WItemTouchHelperPlus
     * through RecyclerView's ItemTouchListener mechanism. As long as no other ItemTouchListener
     * grabs previous events, this should work as expected.
     *
     *
     *
     * For example, if you would like to let your user to be able to drag an Item by touching one
     * of its descendants, you may implement it as follows:
     * <pre>
     * viewHolder.dragButton.setOnTouchListener(new View.OnTouchListener() {
     * public boolean onTouch(View v, MotionEvent event) {
     * if (MotionEvent.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
     * mWItemTouchHelperPlus.startDrag(viewHolder);
     * }
     * return false;
     * }
     * });
    </pre> *
     *
     *
     *
     * @param viewHolder The ViewHolder to start dragging. It must be a direct child of
     * RecyclerView.
     * @see WItemTouchHelperPlus.Callback.isItemViewSwipeEnabled
     */
    fun startDrag(viewHolder: RecyclerView.ViewHolder) {
        if (!mCallback.hasDragFlag(mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but dragging is not enabled")
            return
        }
        if (viewHolder.itemView.parent !== mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of " + "the RecyclerView which is controlled by this WItemTouchHelperPlus.")
            return
        }
        obtainVelocityTracker()
        mDy = 0f
        mDx = mDy
        select(viewHolder, ACTION_STATE_DRAG)
    }

    /**
     * Starts swiping the provided ViewHolder. By default, WItemTouchHelperPlus starts swiping a View
     * when user swipes their finger (or mouse pointer) over the View. You can disable this
     * behavior
     * by overriding [WItemTouchHelperPlus.Callback]
     *
     *
     * For this method to work:
     *
     *  * The provided ViewHolder must be a child of the RecyclerView to which this
     * WItemTouchHelperPlus is attached.
     *  * [WItemTouchHelperPlus.Callback] must have swiping enabled.
     *  * There must be a previous touch event that was reported to the WItemTouchHelperPlus
     * through RecyclerView's ItemTouchListener mechanism. As long as no other ItemTouchListener
     * grabs previous events, this should work as expected.
     *
     *
     *
     * For example, if you would like to let your user to be able to swipe an Item by touching one
     * of its descendants, you may implement it as follows:
     * <pre>
     * viewHolder.dragButton.setOnTouchListener(new View.OnTouchListener() {
     * public boolean onTouch(View v, MotionEvent event) {
     * if (MotionEvent.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
     * mWItemTouchHelperPlus.startSwipe(viewHolder);
     * }
     * return false;
     * }
     * });
    </pre> *
     *
     * @param viewHolder The ViewHolder to start swiping. It must be a direct child of
     * RecyclerView.
     */
    fun startSwipe(viewHolder: RecyclerView.ViewHolder) {
        if (!mCallback.hasSwipeFlag(mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but swiping is not enabled")
            return
        }
        if (viewHolder.itemView.parent !== mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of " + "the RecyclerView controlled by this WItemTouchHelperPlus.")
            return
        }
        obtainVelocityTracker()
        mDy = 0f
        mDx = mDy
        select(viewHolder, ACTION_STATE_SWIPE)
    }

    internal fun findAnimation(event: MotionEvent): WItemTouchHelperPlus.RecoverAnimation? {
        if (mRecoverAnimations.isEmpty()) {
            return null
        }
        val target = findChildView(event)
        for (i in mRecoverAnimations.indices.reversed()) {
            val anim = mRecoverAnimations[i]
            if (anim.mViewHolder.itemView === target) {
                return anim
            }
        }
        return null
    }

    internal fun updateDxDy(ev: MotionEvent, directionFlags: Int, pointerIndex: Int) {
        val x = ev.getX(pointerIndex)
        val y = ev.getY(pointerIndex)

        // Calculate the distance moved
        mDx = x - mInitialTouchX
        mDy = y - mInitialTouchY
        if (directionFlags and LEFT == 0) {
            mDx = Math.max(0f, mDx)
        }
        if (directionFlags and RIGHT == 0) {
            mDx = Math.min(0f, mDx)
        }
        if (directionFlags and UP == 0) {
            mDy = Math.max(0f, mDy)
        }
        if (directionFlags and DOWN == 0) {
            mDy = Math.min(0f, mDy)
        }
    }

    private fun swipeIfNecessary(viewHolder: RecyclerView.ViewHolder): Int {
        if (mActionState == ACTION_STATE_DRAG) {
            return 0
        }
        val originalMovementFlags = mCallback.getMovementFlags(mRecyclerView, viewHolder)
        val absoluteMovementFlags = mCallback.convertToAbsoluteDirection(
                originalMovementFlags,
                ViewCompat.getLayoutDirection(mRecyclerView!!))
        val flags = absoluteMovementFlags and ACTION_MODE_SWIPE_MASK shr ACTION_STATE_SWIPE * DIRECTION_FLAG_COUNT
        if (flags == 0) {
            return 0
        }
        val originalFlags = originalMovementFlags and ACTION_MODE_SWIPE_MASK shr ACTION_STATE_SWIPE * DIRECTION_FLAG_COUNT
        var swipeDir: Int
        if (Math.abs(mDx) > Math.abs(mDy)) {
            swipeDir = checkHorizontalSwipe(viewHolder, flags)
            if (swipeDir > 0) {
                // if swipe dir is not in original flags, it should be the relative direction
                return if (originalFlags and swipeDir == 0) {
                    // convert to relative
                    WItemTouchHelperPlus.Callback.convertToRelativeDirection(swipeDir,
                            ViewCompat.getLayoutDirection(mRecyclerView!!))
                } else swipeDir
            }
            swipeDir = checkVerticalSwipe(viewHolder, flags)
            if (swipeDir > 0) {
                return swipeDir
            }
        } else {
            swipeDir = checkVerticalSwipe(viewHolder, flags)
            if (swipeDir > 0) {
                return swipeDir
            }
            swipeDir = checkHorizontalSwipe(viewHolder, flags)
            if (swipeDir > 0) {
                // if swipe dir is not in original flags, it should be the relative direction
                return if (originalFlags and swipeDir == 0) {
                    // convert to relative
                    WItemTouchHelperPlus.Callback.convertToRelativeDirection(swipeDir,
                            ViewCompat.getLayoutDirection(mRecyclerView!!))
                } else swipeDir
            }
        }
        return 0
    }

    private fun checkHorizontalSwipe(viewHolder: RecyclerView.ViewHolder, flags: Int): Int {
        if (flags and (LEFT or RIGHT) != 0) {
            val dirFlag = if (mDx > 0) RIGHT else LEFT
            if (mVelocityTracker != null && mActivePointerId > -1) {
                mVelocityTracker!!.computeCurrentVelocity(PIXELS_PER_SECOND,
                        mCallback.getSwipeVelocityThreshold(mMaxSwipeVelocity))
                val xVelocity = mVelocityTracker!!.getXVelocity(mActivePointerId)
                val yVelocity = mVelocityTracker!!.getYVelocity(mActivePointerId)
                val velDirFlag = if (xVelocity > 0f) RIGHT else LEFT
                val absXVelocity = Math.abs(xVelocity)
                if (velDirFlag and flags != 0 && dirFlag == velDirFlag
                        && absXVelocity >= mCallback.getSwipeEscapeVelocity(mSwipeEscapeVelocity)
                        && absXVelocity > Math.abs(yVelocity)) {
                    return velDirFlag
                }
            }
            var width = mRecyclerView!!.width
            if (viewHolder is Expanding && mCallback.itemSlideType == SLIDE_ITEM_TYPE_ITEMVIEW) {
                val expandsion = viewHolder as Expanding
                width += expandsion.actionWidth.toInt()
            }
            val threshold = width * mCallback
                    .getSwipeThreshold(viewHolder)

            if (flags and dirFlag != 0 && Math.abs(mDx) > threshold) {
                return dirFlag
            }
        }
        return 0
    }

    private fun checkVerticalSwipe(viewHolder: RecyclerView.ViewHolder, flags: Int): Int {
        if (flags and (UP or DOWN) != 0) {
            val dirFlag = if (mDy > 0) DOWN else UP
            if (mVelocityTracker != null && mActivePointerId > -1) {
                mVelocityTracker!!.computeCurrentVelocity(PIXELS_PER_SECOND,
                        mCallback.getSwipeVelocityThreshold(mMaxSwipeVelocity))
                val xVelocity = mVelocityTracker!!.getXVelocity(mActivePointerId)
                val yVelocity = mVelocityTracker!!.getYVelocity(mActivePointerId)
                val velDirFlag = if (yVelocity > 0f) DOWN else UP
                val absYVelocity = Math.abs(yVelocity)
                if (velDirFlag and flags != 0 && velDirFlag == dirFlag
                        && absYVelocity >= mCallback.getSwipeEscapeVelocity(mSwipeEscapeVelocity)
                        && absYVelocity > Math.abs(xVelocity)) {
                    return velDirFlag
                }
            }

            val threshold = mRecyclerView!!.height * mCallback
                    .getSwipeThreshold(viewHolder)
            if (flags and dirFlag != 0 && Math.abs(mDy) > threshold) {
                return dirFlag
            }
        }
        return 0
    }

    private fun addChildDrawingOrderCallback() {
        if (Build.VERSION.SDK_INT >= 21) {
            return  // we use elevation on Lollipop
        }
        if (mChildDrawingOrderCallback == null) {
            mChildDrawingOrderCallback = RecyclerView.ChildDrawingOrderCallback { childCount, i ->
                if (mOverdrawChild == null) {
                    return@ChildDrawingOrderCallback i
                }
                var childPosition = mOverdrawChildPosition
                if (childPosition == -1) {
                    childPosition = mRecyclerView!!.indexOfChild(mOverdrawChild)
                    mOverdrawChildPosition = childPosition
                }
                if (i == childCount - 1) {
                    return@ChildDrawingOrderCallback childPosition
                }
                if (i < childPosition) i else i + 1
            }
        }
        mRecyclerView!!.setChildDrawingOrderCallback(mChildDrawingOrderCallback)
    }

    internal fun removeChildDrawingOrderCallbackIfNecessary(view: View) {
        if (view === mOverdrawChild) {
            mOverdrawChild = null
            // only remove if we've added
            if (mChildDrawingOrderCallback != null) {
                mRecyclerView!!.setChildDrawingOrderCallback(null)
            }
        }
    }

    /**
     * An interface which can be implemented by LayoutManager for better integration with
     * [WItemTouchHelperPlus].
     */
    interface ViewDropHandler {

        /**
         * Called by the [WItemTouchHelperPlus] after a View is dropped over another View.
         *
         *
         * A LayoutManager should implement this interface to get ready for the upcoming move
         * operation.
         *
         *
         * For example, LinearLayoutManager sets up a "scrollToPositionWithOffset" calls so that
         * the View under drag will be used as an anchor View while calculating the next layout,
         * making layout stay consistent.
         *
         * @param view   The View which is being dragged. It is very likely that user is still
         * dragging this View so there might be other
         * [.prepareForDrop] after this one.
         * @param target The target view which is being dropped on.
         * @param x      The `left` offset of the View that is being dragged. This value
         * includes the movement caused by the user.
         * @param y      The `top` offset of the View that is being dragged. This value
         * includes the movement caused by the user.
         */
        fun prepareForDrop(view: View, target: View, x: Int, y: Int)
    }

    /**
     * This class is the contract between WItemTouchHelperPlus and your application. It lets you control
     * which touch behaviors are enabled per each ViewHolder and also receive callbacks when user
     * performs these actions.
     *
     *
     * To control which actions user can take on each view, you should override
     * [.getMovementFlags] and return appropriate set
     * of direction flags. ([.LEFT], [.RIGHT], [.START], [.END],
     * [.UP], [.DOWN]). You can use
     * [.makeMovementFlags] to easily construct it. Alternatively, you can use
     * [WItemTouchHelperPlus.SimpleCallback].
     *
     *
     * If user drags an item, WItemTouchHelperPlus will call
     * [ onMove(recyclerView, dragged, target)][WItemTouchHelperPlus.Callback.onMove].
     * Upon receiving this callback, you should move the item from the old position
     * (`dragged.getAdapterPosition()`) to new position (`target.getAdapterPosition()`)
     * in your adapter and also call [RecyclerView.Adapter.notifyItemMoved].
     * To control where a View can be dropped, you can override
     * [.canDropOver]. When a
     * dragging View overlaps multiple other views, Callback chooses the closest View with which
     * dragged View might have changed positions. Although this approach works for many use cases,
     * if you have a custom LayoutManager, you can override
     * [.chooseDropTarget] to select a
     * custom drop target.
     *
     *
     * When a View is swiped, WItemTouchHelperPlus animates it until it goes out of bounds, then calls
     * [.onSwiped]. At this point, you should update your
     * adapter (e.g. remove the item) and call related Adapter#notify event.
     */
    abstract class Callback {

        private var mCachedMaxScrollSpeed = -1

        internal abstract val slideViewWidth: Int

        abstract val itemSlideType: String

        /**
         * Returns whether WItemTouchHelperPlus should start a drag and drop operation if an item is
         * long pressed.
         *
         *
         * Default value returns true but you may want to disable this if you want to start
         * dragging on a custom view touch using [.startDrag].
         *
         * @return True if WItemTouchHelperPlus should start dragging an item when it is long pressed,
         * false otherwise. Default value is `true`.
         * @see .startDrag
         */
        val isLongPressDragEnabled: Boolean
            get() = true

        /**
         * Returns whether WItemTouchHelperPlus should start a swipe operation if a pointer is swiped
         * over the View.
         *
         *
         * Default value returns true but you may want to disable this if you want to start
         * swiping on a custom view touch using [.startSwipe].
         *
         * @return True if WItemTouchHelperPlus should start swiping an item when user swipes a pointer
         * over the View, false otherwise. Default value is `true`.
         * @see .startSwipe
         */
        open val isItemViewSwipeEnabled: Boolean
            get() = true

        /**
         * When finding views under a dragged view, by default, WItemTouchHelperPlus searches for views
         * that overlap with the dragged View. By overriding this method, you can extend or shrink
         * the search box.
         *
         * @return The extra margin to be added to the hit box of the dragged View.
         */
        val boundingBoxMargin: Int
            get() = 0

        /**
         * Should return a composite flag which defines the enabled move directions in each state
         * (idle, swiping, dragging).
         *
         *
         * Instead of composing this flag manually, you can use [.makeMovementFlags]
         * or [.makeFlag].
         *
         *
         * This flag is composed of 3 sets of 8 bits, where first 8 bits are for IDLE state, next
         * 8 bits are for SWIPE state and third 8 bits are for DRAG state.
         * Each 8 bit sections can be constructed by simply OR'ing direction flags defined in
         * [WItemTouchHelperPlus].
         *
         *
         * For example, if you want it to allow swiping LEFT and RIGHT but only allow starting to
         * swipe by swiping RIGHT, you can return:
         * <pre>
         * makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, LEFT | RIGHT);
        </pre> *
         * This means, allow right movement while IDLE and allow right and left movement while
         * swiping.
         *
         * @param recyclerView The RecyclerView to which WItemTouchHelperPlus is attached.
         * @param viewHolder   The ViewHolder for which the movement information is necessary.
         * @return flags specifying which movements are allowed on this ViewHolder.
         * @see .makeMovementFlags
         * @see .makeFlag
         */
        abstract fun getMovementFlags(recyclerView: RecyclerView?,
                                      viewHolder: RecyclerView.ViewHolder): Int

        /**
         * Converts a given set of flags to absolution direction which means [.START] and
         * [.END] are replaced with [.LEFT] and [.RIGHT] depending on the layout
         * direction.
         *
         * @param flags           The flag value that include any number of movement flags.
         * @param layoutDirection The layout direction of the RecyclerView.
         * @return Updated flags which includes only absolute direction values.
         */
        fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            var flags = flags
            val masked = flags and RELATIVE_DIR_FLAGS
            if (masked == 0) {
                return flags // does not have any relative flags, good.
            }
            flags = flags and masked.inv() //remove start / end
            if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
                // no change. just OR with 2 bits shifted mask and return
                flags = flags or (masked shr 2) // START is 2 bits after LEFT, END is 2 bits after RIGHT.
                return flags
            } else {
                // add START flag as RIGHT
                flags = flags or (masked shr 1 and RELATIVE_DIR_FLAGS.inv())
                // first clean start bit then add END flag as LEFT
                flags = flags or (masked shr 1 and RELATIVE_DIR_FLAGS shr 2)
            }
            return flags
        }

        internal fun getAbsoluteMovementFlags(recyclerView: RecyclerView?,
                                              viewHolder: RecyclerView.ViewHolder): Int {
            val flags = getMovementFlags(recyclerView, viewHolder)
            return convertToAbsoluteDirection(flags, ViewCompat.getLayoutDirection(recyclerView!!))
        }

        internal fun hasDragFlag(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Boolean {
            val flags = getAbsoluteMovementFlags(recyclerView, viewHolder)
            return flags and ACTION_MODE_DRAG_MASK != 0
        }

        internal fun hasSwipeFlag(recyclerView: RecyclerView?,
                                  viewHolder: RecyclerView.ViewHolder): Boolean {
            val flags = getAbsoluteMovementFlags(recyclerView, viewHolder)
            return flags and ACTION_MODE_SWIPE_MASK != 0
        }

        /**
         * Return true if the current ViewHolder can be dropped over the the target ViewHolder.
         *
         *
         * This method is used when selecting drop target for the dragged View. After Views are
         * eliminated either via bounds check or via this method, resulting set of views will be
         * passed to [.chooseDropTarget].
         *
         *
         * Default implementation returns true.
         *
         * @param recyclerView The RecyclerView to which WItemTouchHelperPlus is attached to.
         * @param current      The ViewHolder that user is dragging.
         * @param target       The ViewHolder which is below the dragged ViewHolder.
         * @return True if the dragged ViewHolder can be replaced with the target ViewHolder, false
         * otherwise.
         */
        fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder?,
                        target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        /**
         * Called when WItemTouchHelperPlus wants to move the dragged item from its old position to
         * the new position.
         *
         *
         * If this method returns true, WItemTouchHelperPlus assumes `viewHolder` has been moved
         * to the adapter position of `target` ViewHolder
         * ([ ViewHolder#getAdapterPosition()][RecyclerView.ViewHolder.getAdapterPosition]).
         *
         *
         * If you don't support drag & drop, this method will never be called.
         *
         * @param recyclerView The RecyclerView to which WItemTouchHelperPlus is attached to.
         * @param viewHolder   The ViewHolder which is being dragged by the user.
         * @param target       The ViewHolder over which the currently active item is being
         * dragged.
         * @return True if the `viewHolder` has been moved to the adapter position of
         * `target`.
         * @see .onMoved
         */
        abstract fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean

        /**
         * Returns the fraction that the user should move the View to be considered as swiped.
         * The fraction is calculated with respect to RecyclerView's bounds.
         *
         *
         * Default value is .5f, which means, to swipe a View, user must move the View at least
         * half of RecyclerView's width or height, depending on the swipe direction.
         *
         * @param viewHolder The ViewHolder that is being dragged.
         * @return A float value that denotes the fraction of the View size. Default value
         * is .5f .
         */
        fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return .5f
        }

        /**
         * Returns the fraction that the user should move the View to be considered as it is
         * dragged. After a view is moved this amount, WItemTouchHelperPlus starts checking for Views
         * below it for a possible drop.
         *
         * @param viewHolder The ViewHolder that is being dragged.
         * @return A float value that denotes the fraction of the View size. Default value is
         * .5f .
         */
        fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder?): Float {
            return .5f
        }

        /**
         * Defines the minimum velocity which will be considered as a swipe action by the user.
         *
         *
         * You can increase this value to make it harder to swipe or decrease it to make it easier.
         * Keep in mind that WItemTouchHelperPlus also checks the perpendicular velocity and makes sure
         * current direction velocity is larger then the perpendicular one. Otherwise, user's
         * movement is ambiguous. You can change the threshold by overriding
         * [.getSwipeVelocityThreshold].
         *
         *
         * The velocity is calculated in pixels per second.
         *
         *
         * The default framework value is passed as a parameter so that you can modify it with a
         * multiplier.
         *
         * @param defaultValue The default value (in pixels per second) used by the
         * WItemTouchHelperPlus.
         * @return The minimum swipe velocity. The default implementation returns the
         * `defaultValue` parameter.
         * @see .getSwipeVelocityThreshold
         * @see .getSwipeThreshold
         */
        fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return defaultValue
        }

        /**
         * Defines the maximum velocity WItemTouchHelperPlus will ever calculate for pointer movements.
         *
         *
         * To consider a movement as swipe, WItemTouchHelperPlus requires it to be larger than the
         * perpendicular movement. If both directions reach to the max threshold, none of them will
         * be considered as a swipe because it is usually an indication that user rather tried to
         * scroll then swipe.
         *
         *
         * The velocity is calculated in pixels per second.
         *
         *
         * You can customize this behavior by changing this method. If you increase the value, it
         * will be easier for the user to swipe diagonally and if you decrease the value, user will
         * need to make a rather straight finger movement to trigger a swipe.
         *
         * @param defaultValue The default value(in pixels per second) used by the WItemTouchHelperPlus.
         * @return The velocity cap for pointer movements. The default implementation returns the
         * `defaultValue` parameter.
         * @see .getSwipeEscapeVelocity
         */
        fun getSwipeVelocityThreshold(defaultValue: Float): Float {
            return defaultValue
        }

        /**
         * Called by WItemTouchHelperPlus to select a drop target from the list of ViewHolders that
         * are under the dragged View.
         *
         *
         * Default implementation filters the View with which dragged item have changed position
         * in the drag direction. For instance, if the view is dragged UP, it compares the
         * `view.getTop()` of the two views before and after drag started. If that value
         * is different, the target view passes the filter.
         *
         *
         * Among these Views which pass the test, the one closest to the dragged view is chosen.
         *
         *
         * This method is called on the main thread every time user moves the View. If you want to
         * override it, make sure it does not do any expensive operations.
         *
         * @param selected    The ViewHolder being dragged by the user.
         * @param dropTargets The list of ViewHolder that are under the dragged View and
         * candidate as a drop.
         * @param curX        The updated left value of the dragged View after drag translations
         * are applied. This value does not include margins added by
         * [RecyclerView.ItemDecoration]s.
         * @param curY        The updated top value of the dragged View after drag translations
         * are applied. This value does not include margins added by
         * [RecyclerView.ItemDecoration]s.
         * @return A ViewHolder to whose position the dragged ViewHolder should be
         * moved to.
         */
        fun chooseDropTarget(selected: RecyclerView.ViewHolder,
                             dropTargets: List<RecyclerView.ViewHolder>, curX: Int, curY: Int): RecyclerView.ViewHolder? {
            val right = curX + selected.itemView.width
            val bottom = curY + selected.itemView.height
            var winner: RecyclerView.ViewHolder? = null
            var winnerScore = -1
            val dx = curX - selected.itemView.left
            val dy = curY - selected.itemView.top
            val targetsSize = dropTargets.size
            for (i in 0 until targetsSize) {
                val target = dropTargets[i]
                if (dx > 0) {
                    val diff = target.itemView.right - right
                    if (diff < 0 && target.itemView.right > selected.itemView.right) {
                        val score = Math.abs(diff)
                        if (score > winnerScore) {
                            winnerScore = score
                            winner = target
                        }
                    }
                }
                if (dx < 0) {
                    val diff = target.itemView.left - curX
                    if (diff > 0 && target.itemView.left < selected.itemView.left) {
                        val score = Math.abs(diff)
                        if (score > winnerScore) {
                            winnerScore = score
                            winner = target
                        }
                    }
                }
                if (dy < 0) {
                    val diff = target.itemView.top - curY
                    if (diff > 0 && target.itemView.top < selected.itemView.top) {
                        val score = Math.abs(diff)
                        if (score > winnerScore) {
                            winnerScore = score
                            winner = target
                        }
                    }
                }

                if (dy > 0) {
                    val diff = target.itemView.bottom - bottom
                    if (diff < 0 && target.itemView.bottom > selected.itemView.bottom) {
                        val score = Math.abs(diff)
                        if (score > winnerScore) {
                            winnerScore = score
                            winner = target
                        }
                    }
                }
            }
            return winner
        }

        /**
         * Called when a ViewHolder is swiped by the user.
         *
         *
         * If you are returning relative directions ([.START] , [.END]) from the
         * [.getMovementFlags] method, this method
         * will also use relative directions. Otherwise, it will use absolute directions.
         *
         *
         * If you don't support swiping, this method will never be called.
         *
         *
         * WItemTouchHelperPlus will keep a reference to the View until it is detached from
         * RecyclerView.
         * As soon as it is detached, WItemTouchHelperPlus will call
         * [.clearView].
         *
         * @param viewHolder The ViewHolder which has been swiped by the user.
         * @param direction  The direction to which the ViewHolder is swiped. It is one of
         * [.UP], [.DOWN],
         * [.LEFT] or [.RIGHT]. If your
         * [.getMovementFlags]
         * method
         * returned relative flags instead of [.LEFT] / [.RIGHT];
         * `direction` will be relative as well. ([.START] or [                   ][.END]).
         */
        abstract fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)

        /**
         * Called when the ViewHolder swiped or dragged by the WItemTouchHelperPlus is changed.
         *
         *
         * If you override this method, you should call super.
         *
         * @param viewHolder  The new ViewHolder that is being swiped or dragged. Might be null if
         * it is cleared.
         * @param actionState One of [WItemTouchHelperPlus.ACTION_STATE_IDLE],
         * [WItemTouchHelperPlus.ACTION_STATE_SWIPE] or
         * [WItemTouchHelperPlus.ACTION_STATE_DRAG].
         * @see .clearView
         */
        fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder != null) {
                defaultUIUtil.onSelected(viewHolder.itemView)
            }
        }

        private fun getMaxDragScroll(recyclerView: RecyclerView): Int {
            if (mCachedMaxScrollSpeed == -1) {
                mCachedMaxScrollSpeed = recyclerView.resources.getDimensionPixelSize(
                        R.dimen.item_touch_helper_max_drag_scroll_per_frame)
            }
            return mCachedMaxScrollSpeed
        }

        /**
         * Called when [.onMove] returns true.
         *
         *
         * WItemTouchHelperPlus does not create an extra Bitmap or View while dragging, instead, it
         * modifies the existing View. Because of this reason, it is important that the View is
         * still part of the layout after it is moved. This may not work as intended when swapped
         * Views are close to RecyclerView bounds or there are gaps between them (e.g. other Views
         * which were not eligible for dropping over).
         *
         *
         * This method is responsible to give necessary hint to the LayoutManager so that it will
         * keep the View in visible area. For example, for LinearLayoutManager, this is as simple
         * as calling [LinearLayoutManager.scrollToPositionWithOffset].
         *
         *
         * Default implementation calls [RecyclerView.scrollToPosition] if the View's
         * new position is likely to be out of bounds.
         *
         *
         * It is important to ensure the ViewHolder will stay visible as otherwise, it might be
         * removed by the LayoutManager if the move causes the View to go out of bounds. In that
         * case, drag will end prematurely.
         *
         * @param recyclerView The RecyclerView controlled by the WItemTouchHelperPlus.
         * @param viewHolder   The ViewHolder under user's control.
         * @param fromPos      The previous adapter position of the dragged item (before it was
         * moved).
         * @param target       The ViewHolder on which the currently active item has been dropped.
         * @param toPos        The new adapter position of the dragged item.
         * @param x            The updated left value of the dragged View after drag translations
         * are applied. This value does not include margins added by
         * [RecyclerView.ItemDecoration]s.
         * @param y            The updated top value of the dragged View after drag translations
         * are applied. This value does not include margins added by
         * [RecyclerView.ItemDecoration]s.
         */
        fun onMoved(recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int,
                    y: Int) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is WItemTouchHelperPlus.ViewDropHandler) {
                (layoutManager as WItemTouchHelperPlus.ViewDropHandler).prepareForDrop(viewHolder.itemView,
                        target.itemView, x, y)
                return
            }

            // if layout manager cannot handle it, do some guesswork
            if (layoutManager!!.canScrollHorizontally()) {
                val minLeft = layoutManager.getDecoratedLeft(target.itemView)
                if (minLeft <= recyclerView.paddingLeft) {
                    recyclerView.scrollToPosition(toPos)
                }
                val maxRight = layoutManager.getDecoratedRight(target.itemView)
                if (maxRight >= recyclerView.width - recyclerView.paddingRight) {
                    recyclerView.scrollToPosition(toPos)
                }
            }

            if (layoutManager.canScrollVertically()) {
                val minTop = layoutManager.getDecoratedTop(target.itemView)
                if (minTop <= recyclerView.paddingTop) {
                    recyclerView.scrollToPosition(toPos)
                }
                val maxBottom = layoutManager.getDecoratedBottom(target.itemView)
                if (maxBottom >= recyclerView.height - recyclerView.paddingBottom) {
                    recyclerView.scrollToPosition(toPos)
                }
            }
        }

        internal fun onDraw(c: Canvas, parent: RecyclerView, selected: RecyclerView.ViewHolder?,
                            recoverAnimationList: List<RecoverAnimation>,
                            actionState: Int, dX: Float, dY: Float) {
            val recoverAnimSize = recoverAnimationList.size
            for (i in 0 until recoverAnimSize) {
                val anim = recoverAnimationList[i]
                anim.update()
                val count = c.save()
                onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState,
                        false)
                c.restoreToCount(count)
            }
            if (selected != null) {
                val count = c.save()
                onChildDraw(c, parent, selected, dX, dY, actionState, true)
                c.restoreToCount(count)
            }
        }

        internal fun onDrawOver(c: Canvas, parent: RecyclerView, selected: RecyclerView.ViewHolder?,
                                recoverAnimationList: MutableList<RecoverAnimation>,
                                actionState: Int, dX: Float, dY: Float) {
            val recoverAnimSize = recoverAnimationList.size
            for (i in 0 until recoverAnimSize) {
                val anim = recoverAnimationList[i]
                val count = c.save()
                onChildDrawOver(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState,
                        false)
                c.restoreToCount(count)
            }
            if (selected != null) {
                val count = c.save()
                onChildDrawOver(c, parent, selected, dX, dY, actionState, true)
                c.restoreToCount(count)
            }
            var hasRunningAnimation = false
            for (i in recoverAnimSize - 1 downTo 0) {
                val anim = recoverAnimationList[i]
                if (anim.mEnded && !anim.mIsPendingCleanup) {
                    recoverAnimationList.removeAt(i)
                } else if (!anim.mEnded) {
                    hasRunningAnimation = true
                }
            }
            if (hasRunningAnimation) {
                parent.invalidate()
            }
        }

        /**
         * Called by the WItemTouchHelperPlus when the user interaction with an element is over and it
         * also completed its animation.
         *
         *
         * This is a good place to clear all changes on the View that was done in
         * [.onSelectedChanged],
         * [.onChildDraw] or
         * [.onChildDrawOver].
         *
         * @param recyclerView The RecyclerView which is controlled by the WItemTouchHelperPlus.
         * @param viewHolder   The View that was interacted by the user.
         */
        open fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
            defaultUIUtil.clearView(viewHolder.itemView)
        }

        /**
         * Called by WItemTouchHelperPlus on RecyclerView's onDraw callback.
         *
         *
         * If you would like to customize how your View's respond to user interactions, this is
         * a good place to override.
         *
         *
         * Default implementation translates the child by the given `dX`,
         * `dY`.
         * WItemTouchHelperPlus also takes care of drawing the child after other children if it is being
         * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
         * is
         * achieved via [ViewGroup.getChildDrawingOrder] and on L
         * and after, it changes View's elevation value to be greater than all other children.)
         *
         * @param c                 The canvas which RecyclerView is drawing its children
         * @param recyclerView      The RecyclerView to which WItemTouchHelperPlus is attached to
         * @param viewHolder        The ViewHolder which is being interacted by the User or it was
         * interacted and simply animating to its original position
         * @param dX                The amount of horizontal displacement caused by user's action
         * @param dY                The amount of vertical displacement caused by user's action
         * @param actionState       The type of interaction on the View. Is either [                          ][.ACTION_STATE_DRAG] or [.ACTION_STATE_SWIPE].
         * @param isCurrentlyActive True if this view is currently being controlled by the user or
         * false it is simply animating back to its original state.
         * @see .onChildDrawOver
         */
        open fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            defaultUIUtil.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                    isCurrentlyActive)
        }

        /**
         * Called by WItemTouchHelperPlus on RecyclerView's onDraw callback.
         *
         *
         * If you would like to customize how your View's respond to user interactions, this is
         * a good place to override.
         *
         *
         * Default implementation translates the child by the given `dX`,
         * `dY`.
         * WItemTouchHelperPlus also takes care of drawing the child after other children if it is being
         * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
         * is
         * achieved via [ViewGroup.getChildDrawingOrder] and on L
         * and after, it changes View's elevation value to be greater than all other children.)
         *
         * @param c                 The canvas which RecyclerView is drawing its children
         * @param recyclerView      The RecyclerView to which WItemTouchHelperPlus is attached to
         * @param viewHolder        The ViewHolder which is being interacted by the User or it was
         * interacted and simply animating to its original position
         * @param dX                The amount of horizontal displacement caused by user's action
         * @param dY                The amount of vertical displacement caused by user's action
         * @param actionState       The type of interaction on the View. Is either [                          ][.ACTION_STATE_DRAG] or [.ACTION_STATE_SWIPE].
         * @param isCurrentlyActive True if this view is currently being controlled by the user or
         * false it is simply animating back to its original state.
         * @see .onChildDrawOver
         */
        fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            defaultUIUtil.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                    isCurrentlyActive)
        }

        /**
         * Called by the WItemTouchHelperPlus when user action finished on a ViewHolder and now the View
         * will be animated to its final position.
         *
         *
         * Default implementation uses ItemAnimator's duration values. If
         * `animationType` is [.ANIMATION_TYPE_DRAG], it returns
         * [RecyclerView.ItemAnimator.getMoveDuration], otherwise, it returns
         * [RecyclerView.ItemAnimator.getRemoveDuration]. If RecyclerView does not have
         * any [RecyclerView.ItemAnimator] attached, this method returns
         * `DEFAULT_DRAG_ANIMATION_DURATION` or `DEFAULT_SWIPE_ANIMATION_DURATION`
         * depending on the animation type.
         *
         * @param recyclerView  The RecyclerView to which the WItemTouchHelperPlus is attached to.
         * @param animationType The type of animation. Is one of [.ANIMATION_TYPE_DRAG],
         * [.ANIMATION_TYPE_SWIPE_CANCEL] or
         * [.ANIMATION_TYPE_SWIPE_SUCCESS].
         * @param animateDx     The horizontal distance that the animation will offset
         * @param animateDy     The vertical distance that the animation will offset
         * @return The duration for the animation
         */
        fun getAnimationDuration(recyclerView: RecyclerView, animationType: Int,
                                 animateDx: Float, animateDy: Float): Long {
            val itemAnimator = recyclerView.itemAnimator
            return if (itemAnimator == null) {
                (if (animationType == ANIMATION_TYPE_DRAG)
                    DEFAULT_DRAG_ANIMATION_DURATION
                else
                    DEFAULT_SWIPE_ANIMATION_DURATION).toLong()
            } else {
                if (animationType == ANIMATION_TYPE_DRAG)
                    itemAnimator.moveDuration
                else
                    itemAnimator.removeDuration
            }
        }

        /**
         * Called by the WItemTouchHelperPlus when user is dragging a view out of bounds.
         *
         *
         * You can override this method to decide how much RecyclerView should scroll in response
         * to this action. Default implementation calculates a value based on the amount of View
         * out of bounds and the time it spent there. The longer user keeps the View out of bounds,
         * the faster the list will scroll. Similarly, the larger portion of the View is out of
         * bounds, the faster the RecyclerView will scroll.
         *
         * @param recyclerView        The RecyclerView instance to which WItemTouchHelperPlus is
         * attached to.
         * @param viewSize            The total size of the View in scroll direction, excluding
         * item decorations.
         * @param viewSizeOutOfBounds The total size of the View that is out of bounds. This value
         * is negative if the View is dragged towards left or top edge.
         * @param totalSize           The total size of RecyclerView in the scroll direction.
         * @param msSinceStartScroll  The time passed since View is kept out of bounds.
         * @return The amount that RecyclerView should scroll. Keep in mind that this value will
         * be passed to [RecyclerView.scrollBy] method.
         */
        fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView,
                                         viewSize: Int, viewSizeOutOfBounds: Int,
                                         totalSize: Int, msSinceStartScroll: Long): Int {
            val maxScroll = getMaxDragScroll(recyclerView)
            val absOutOfBounds = Math.abs(viewSizeOutOfBounds)
            val direction = Math.signum(viewSizeOutOfBounds.toFloat()).toInt()
            // might be negative if other direction
            val outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize)
            val cappedScroll = (direction.toFloat() * maxScroll.toFloat()
                    * sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio)).toInt()
            val timeRatio: Float
            if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
                timeRatio = 1f
            } else {
                timeRatio = msSinceStartScroll.toFloat() / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS
            }
            val value = (cappedScroll * sDragScrollInterpolator
                    .getInterpolation(timeRatio)).toInt()
            return if (value == 0) {
                if (viewSizeOutOfBounds > 0) 1 else -1
            } else value
        }

        companion object {

            val DEFAULT_DRAG_ANIMATION_DURATION = 200

            val DEFAULT_SWIPE_ANIMATION_DURATION = 250

            internal val RELATIVE_DIR_FLAGS = (START or END
                    or (START or END shl DIRECTION_FLAG_COUNT)
                    or (START or END shl 2 * DIRECTION_FLAG_COUNT))

            /**
             * Returns the [ItemTouchUIUtil] that is used by the [WItemTouchHelperPlus.Callback] class for
             * visual
             * changes on Views in response to user interactions. [ItemTouchUIUtil] has different
             * implementations for different platform versions.
             *
             *
             * By default, [WItemTouchHelperPlus.Callback] applies these changes on
             * [RecyclerView.ViewHolder.itemView].
             *
             *
             * For example, if you have a use case where you only want the text to move when user
             * swipes over the view, you can do the following:
             * <pre>
             * public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
             * getDefaultUIUtil().clearView(((ItemTouchViewHolder) viewHolder).textView);
             * }
             * public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
             * if (viewHolder != null){
             * getDefaultUIUtil().onSelected(((ItemTouchViewHolder) viewHolder).textView);
             * }
             * }
             * public void onChildDraw(Canvas c, RecyclerView recyclerView,
             * RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
             * boolean isCurrentlyActive) {
             * getDefaultUIUtil().onDraw(c, recyclerView,
             * ((ItemTouchViewHolder) viewHolder).textView, dX, dY,
             * actionState, isCurrentlyActive);
             * return true;
             * }
             * public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
             * RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
             * boolean isCurrentlyActive) {
             * getDefaultUIUtil().onDrawOver(c, recyclerView,
             * ((ItemTouchViewHolder) viewHolder).textView, dX, dY,
             * actionState, isCurrentlyActive);
             * return true;
             * }
            </pre> *
             *
             * @return The [ItemTouchUIUtil] instance that is used by the [WItemTouchHelperPlus.Callback]
             */
            val defaultUIUtil: ItemTouchUIUtil

            private val ABS_HORIZONTAL_DIR_FLAGS = (LEFT or RIGHT
                    or (LEFT or RIGHT shl DIRECTION_FLAG_COUNT)
                    or (LEFT or RIGHT shl 2 * DIRECTION_FLAG_COUNT))

            private val sDragScrollInterpolator = Interpolator { t -> t * t * t * t * t }

            private val sDragViewScrollCapInterpolator = Interpolator { t ->
                var t = t
                t -= 1.0f
                t * t * t * t * t + 1.0f
            }

            /**
             * Drag scroll speed keeps accelerating until this many milliseconds before being capped.
             */
            private val DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS: Long = 2000

            init {
                if (Build.VERSION.SDK_INT >= 21) {
                    defaultUIUtil = ItemTouchUIUtilImpl.Lollipop()
                } else {
                    defaultUIUtil = ItemTouchUIUtilImpl.Honeycomb()
                }
            }

            /**
             * Replaces a movement direction with its relative version by taking layout direction into
             * account.
             *
             * @param flags           The flag value that include any number of movement flags.
             * @param layoutDirection The layout direction of the View. Can be obtained from
             * [ViewCompat.getLayoutDirection].
             * @return Updated flags which uses relative flags ([.START], [.END]) instead
             * of [.LEFT], [.RIGHT].
             * @see .convertToAbsoluteDirection
             */
            fun convertToRelativeDirection(flags: Int, layoutDirection: Int): Int {
                var flags = flags
                val masked = flags and ABS_HORIZONTAL_DIR_FLAGS
                if (masked == 0) {
                    return flags // does not have any abs flags, good.
                }
                flags = flags and masked.inv() //remove left / right.
                if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
                    // no change. just OR with 2 bits shifted mask and return
                    flags = flags or (masked shl 2) // START is 2 bits after LEFT, END is 2 bits after RIGHT.
                    return flags
                } else {
                    // add RIGHT flag as START
                    flags = flags or (masked shl 1 and ABS_HORIZONTAL_DIR_FLAGS.inv())
                    // first clean RIGHT bit then add LEFT flag as END
                    flags = flags or (masked shl 1 and ABS_HORIZONTAL_DIR_FLAGS shl 2)
                }
                return flags
            }

            /**
             * Convenience method to create movement flags.
             *
             *
             * For instance, if you want to let your items be drag & dropped vertically and swiped
             * left to be dismissed, you can call this method with:
             * `makeMovementFlags(UP | DOWN, LEFT);`
             *
             * @param dragFlags  The directions in which the item can be dragged.
             * @param swipeFlags The directions in which the item can be swiped.
             * @return Returns an integer composed of the given drag and swipe flags.
             */
            fun makeMovementFlags(dragFlags: Int, swipeFlags: Int): Int {
                return (makeFlag(ACTION_STATE_IDLE, swipeFlags or dragFlags)
                        or makeFlag(ACTION_STATE_SWIPE, swipeFlags)
                        or makeFlag(ACTION_STATE_DRAG, dragFlags))
            }

            /**
             * Shifts the given direction flags to the offset of the given action state.
             *
             * @param actionState The action state you want to get flags in. Should be one of
             * [.ACTION_STATE_IDLE], [.ACTION_STATE_SWIPE] or
             * [.ACTION_STATE_DRAG].
             * @param directions  The direction flags. Can be composed from [.UP], [.DOWN],
             * [.RIGHT], [.LEFT] [.START] and [.END].
             * @return And integer that represents the given directions in the provided actionState.
             */
            fun makeFlag(actionState: Int, directions: Int): Int {
                return directions shl actionState * DIRECTION_FLAG_COUNT
            }
        }
    }

    /**
     * A simple wrapper to the default Callback which you can construct with drag and swipe
     * directions and this class will handle the flag callbacks. You should still override onMove
     * or
     * onSwiped depending on your use case.
     *
     *
     * <pre>
     * WItemTouchHelperPlus mIth = new WItemTouchHelperPlus(
     * new WItemTouchHelperPlus.SimpleCallback(WItemTouchHelperPlus.UP | WItemTouchHelperPlus.DOWN,
     * WItemTouchHelperPlus.LEFT) {
     * public abstract boolean onMove(RecyclerView recyclerView,
     * ViewHolder viewHolder, ViewHolder target) {
     * final int fromPos = viewHolder.getAdapterPosition();
     * final int toPos = target.getAdapterPosition();
     * // move item in `fromPos` to `toPos` in adapter.
     * return true;// true if moved, false otherwise
     * }
     * public void onSwiped(ViewHolder viewHolder, int direction) {
     * // remove from adapter
     * }
     * });
    </pre> *
     */
    abstract class SimpleCallback
    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * [.getSwipeDirs]
     * and / or [.getDragDirs].
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     * composed of [.LEFT], [.RIGHT], [.START], [                  ][.END],
     * [.UP] and [.DOWN].
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     * composed of [.LEFT], [.RIGHT], [.START], [                  ][.END],
     * [.UP] and [.DOWN].
     */
    (private var mDefaultDragDirs: Int, private var mDefaultSwipeDirs: Int) : WItemTouchHelperPlus.Callback() {

        /**
         * Updates the default swipe directions. For example, you can use this method to toggle
         * certain directions depending on your use case.
         *
         * @param defaultSwipeDirs Binary OR of directions in which the ViewHolders can be swiped.
         */
        fun setDefaultSwipeDirs(defaultSwipeDirs: Int) {
            mDefaultSwipeDirs = defaultSwipeDirs
        }

        /**
         * Updates the default drag directions. For example, you can use this method to toggle
         * certain directions depending on your use case.
         *
         * @param defaultDragDirs Binary OR of directions in which the ViewHolders can be dragged.
         */
        fun setDefaultDragDirs(defaultDragDirs: Int) {
            mDefaultDragDirs = defaultDragDirs
        }

        /**
         * Returns the swipe directions for the provided ViewHolder.
         * Default implementation returns the swipe directions that was set via constructor or
         * [.setDefaultSwipeDirs].
         *
         * @param recyclerView The RecyclerView to which the WItemTouchHelperPlus is attached to.
         * @param viewHolder   The RecyclerView for which the swipe direction is queried.
         * @return A binary OR of direction flags.
         */
        fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
            return mDefaultSwipeDirs
        }

        /**
         * Returns the drag directions for the provided ViewHolder.
         * Default implementation returns the drag directions that was set via constructor or
         * [.setDefaultDragDirs].
         *
         * @param recyclerView The RecyclerView to which the WItemTouchHelperPlus is attached to.
         * @param viewHolder   The RecyclerView for which the swipe direction is queried.
         * @return A binary OR of direction flags.
         */
        fun getDragDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
            return mDefaultDragDirs
        }

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
            return WItemTouchHelperPlus.Callback.makeMovementFlags(getDragDirs(recyclerView, viewHolder),
                    getSwipeDirs(recyclerView, viewHolder))
        }
    }

    private inner class WItemTouchHelperPlusGestureListener internal constructor() : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val child = findChildView(e)
            if (child != null) {
                val vh = mRecyclerView!!.getChildViewHolder(child)
                if (vh != null) {
                    if (!mCallback.hasDragFlag(mRecyclerView, vh)) {
                        return
                    }
                    val pointerId = e.getPointerId(0)
                    // Long press is deferred.
                    // Check w/ active pointer id to avoid selecting after motion
                    // event is canceled.
                    if (pointerId == mActivePointerId) {
                        val index = e.findPointerIndex(mActivePointerId)
                        val x = e.getX(index)
                        val y = e.getY(index)
                        mInitialTouchX = x
                        mInitialTouchY = y
                        mDy = 0f
                        mDx = mDy
                        if (DEBUG) {
                            Log.d(TAG,
                                    "onlong press: x:$mInitialTouchX,y:$mInitialTouchY")
                        }
                        if (mCallback.isLongPressDragEnabled) {
                            select(vh, ACTION_STATE_DRAG)
                        }
                    }
                }
            }
        }
    }

    open class RecoverAnimation internal constructor(internal val mViewHolder: RecyclerView.ViewHolder, internal val mAnimationType: Int,
                                                     internal val mActionState: Int, internal val mStartDx: Float, internal val mStartDy: Float, internal val mTargetX: Float, internal val mTargetY: Float) : Animator.AnimatorListener {

        private val mValueAnimator: ValueAnimator

        var mIsPendingCleanup: Boolean = false

        internal var mX: Float = 0.toFloat()

        internal var mY: Float = 0.toFloat()

        // if user starts touching a recovering view, we put it into interaction mode again,
        // instantly.
        internal var mOverridden = false

        internal var mEnded = false

        private var mFraction: Float = 0.toFloat()

        init {
            mValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            mValueAnimator.addUpdateListener { animation -> setFraction(animation.animatedFraction) }
            mValueAnimator.setTarget(mViewHolder.itemView)
            mValueAnimator.addListener(this)
            setFraction(0f)
        }

        fun setDuration(duration: Long) {
            mValueAnimator.duration = duration
        }

        fun start() {
            mViewHolder.setIsRecyclable(false)
            mValueAnimator.start()
        }

        fun cancel() {
            mValueAnimator.cancel()
        }

        fun setFraction(fraction: Float) {
            mFraction = fraction
        }

        /**
         * We run updates on onDraw method but use the fraction from animator callback.
         * This way, we can sync translate x/y values w/ the animators to avoid one-off frames.
         */
        fun update() {
            if (mStartDx == mTargetX) {
                mX = mViewHolder.itemView.translationX
            } else {
                mX = mStartDx + mFraction * (mTargetX - mStartDx)
            }
            if (mStartDy == mTargetY) {
                mY = mViewHolder.itemView.translationY
            } else {
                mY = mStartDy + mFraction * (mTargetY - mStartDy)
            }
        }

        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            if (!mEnded) {
                mViewHolder.setIsRecyclable(true)
            }
            mEnded = true
        }

        override fun onAnimationCancel(animation: Animator) {
            setFraction(1f) //make sure we recover the view's state.
        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    companion object {
        /**
         * 侧滑显示的布局 跟随 在滑动布局的下面的标记  看场景选择
         */
        val SLIDE_ITEM_TYPE_ITEMVIEW = "itemView"

        /**
         * 侧滑显示的布局 隐藏 在滑动布局的下面的标记  看场景选择
         */
        val SLIDE_ITEM_TYPE_SLIDECONTAINER = "slideContainer"

        /**
         * Up direction, used for swipe & drag control.
         */
        val UP = 1

        /**
         * Down direction, used for swipe & drag control.
         */
        val DOWN = 1 shl 1

        /**
         * Left direction, used for swipe & drag control.
         */
        val LEFT = 1 shl 2

        /**
         * Right direction, used for swipe & drag control.
         */
        val RIGHT = 1 shl 3

        // If you change these relative direction values, update Callback#convertToAbsoluteDirection,
        // Callback#convertToRelativeDirection.
        /**
         * Horizontal start direction. Resolved to LEFT or RIGHT depending on RecyclerView's layout
         * direction. Used for swipe & drag control.
         */
        val START = LEFT shl 2

        /**
         * Horizontal end direction. Resolved to LEFT or RIGHT depending on RecyclerView's layout
         * direction. Used for swipe & drag control.
         */
        val END = RIGHT shl 2

        /**
         * WItemTouchHelperPlus is in idle state. At this state, either there is no related motion event by
         * the user or latest motion events have not yet triggered a swipe or drag.
         */
        val ACTION_STATE_IDLE = 0

        /**
         * A View is currently being swiped.
         */
        val ACTION_STATE_SWIPE = 1

        /**
         * A View is currently being dragged.
         */
        val ACTION_STATE_DRAG = 2

        /**
         * Animation type for views which are swiped successfully.
         */
        val ANIMATION_TYPE_SWIPE_SUCCESS = 1 shl 1

        /**
         * Animation type for views which are not completely swiped thus will animate back to their
         * original position.
         */
        val ANIMATION_TYPE_SWIPE_CANCEL = 1 shl 2

        /**
         * Animation type for views that were dragged and now will animate to their final position.
         */
        val ANIMATION_TYPE_DRAG = 1 shl 3

        internal val TAG = "WItemTouchHelperPlus"

        internal val DEBUG = false

        internal val ACTIVE_POINTER_ID_NONE = -1

        internal val DIRECTION_FLAG_COUNT = 8

        private val ACTION_MODE_IDLE_MASK = (1 shl DIRECTION_FLAG_COUNT) - 1

        internal val ACTION_MODE_SWIPE_MASK = ACTION_MODE_IDLE_MASK shl DIRECTION_FLAG_COUNT

        internal val ACTION_MODE_DRAG_MASK = ACTION_MODE_SWIPE_MASK shl DIRECTION_FLAG_COUNT

        /**
         * The unit we are using to track velocity
         */
        private val PIXELS_PER_SECOND = 1000
    }
}
