package com.fphoenixcorneae.arclayout

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import androidx.annotation.IntDef
import com.fphoenixcorneae.arclayout.ClipPathManagerImpl.ClipPathCreator

/**
 * @desc：弧形布局
 */
class ArcLayout : AbstractClipShapeLayout {
    @ArcPosition
    private var arcPosition = POSITION_TOP

    @CropDirection
    private var cropDirection = CROP_INSIDE
    private var arcHeight = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs != null) {
            val attributes =
                context.obtainStyledAttributes(attrs, R.styleable.ArcLayout)
            arcHeight =
                attributes.getDimensionPixelSize(R.styleable.ArcLayout_arc_height, arcHeight)
            arcPosition = attributes.getInteger(R.styleable.ArcLayout_arc_position, arcPosition)
            cropDirection =
                attributes.getInteger(R.styleable.ArcLayout_arc_cropDirection, cropDirection)
            attributes.recycle()
        }
        super.setClipPathCreator(object : ClipPathCreator {
            override fun createClipPath(width: Int, height: Int): Path {
                val path = Path()
                val isCropInside = cropDirection == CROP_INSIDE
                when (arcPosition) {
                    POSITION_BOTTOM -> {
                        if (isCropInside) {
                            path.moveTo(0f, 0f)
                            path.lineTo(0f, height.toFloat())
                            path.quadTo(
                                width / 2.toFloat(),
                                height - 2 * arcHeight.toFloat(),
                                width.toFloat(),
                                height.toFloat()
                            )
                            path.lineTo(width.toFloat(), 0f)
                            path.close()
                        } else {
                            path.moveTo(0f, 0f)
                            path.lineTo(0f, height - arcHeight.toFloat())
                            path.quadTo(
                                width / 2.toFloat(),
                                height + arcHeight.toFloat(),
                                width.toFloat(),
                                height - arcHeight.toFloat()
                            )
                            path.lineTo(width.toFloat(), 0f)
                            path.close()
                        }
                    }
                    POSITION_TOP -> {
                        if (isCropInside) {
                            path.moveTo(0f, height.toFloat())
                            path.lineTo(0f, 0f)
                            path.quadTo(
                                width / 2.toFloat(),
                                2 * arcHeight.toFloat(),
                                width.toFloat(),
                                0f
                            )
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(0f, arcHeight.toFloat())
                            path.quadTo(
                                width / 2.toFloat(),
                                -arcHeight.toFloat(),
                                width.toFloat(),
                                arcHeight.toFloat()
                            )
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        }
                    }
                    POSITION_LEFT -> {
                        if (isCropInside) {
                            path.moveTo(width.toFloat(), 0f)
                            path.lineTo(0f, 0f)
                            path.quadTo(
                                arcHeight * 2.toFloat(),
                                height / 2.toFloat(),
                                0f,
                                height.toFloat()
                            )
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(width.toFloat(), 0f)
                            path.lineTo(arcHeight.toFloat(), 0f)
                            path.quadTo(
                                -arcHeight.toFloat(),
                                height / 2.toFloat(),
                                arcHeight.toFloat(),
                                height.toFloat()
                            )
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        }
                    }
                    POSITION_RIGHT -> {
                        if (isCropInside) {
                            path.moveTo(0f, 0f)
                            path.lineTo(width.toFloat(), 0f)
                            path.quadTo(
                                width - arcHeight * 2.toFloat(),
                                height / 2.toFloat(),
                                width.toFloat(),
                                height.toFloat()
                            )
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(0f, 0f)
                            path.lineTo(width - arcHeight.toFloat(), 0f)
                            path.quadTo(
                                width + arcHeight.toFloat(),
                                height / 2.toFloat(),
                                width - arcHeight.toFloat(),
                                height.toFloat()
                            )
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        }
                    }
                }
                return path
            }

            override fun requiresBitmap(): Boolean {
                return false
            }
        })
    }

    fun getArcPosition(): Int {
        return arcPosition
    }

    fun setArcPosition(@ArcPosition arcPosition: Int) {
        this.arcPosition = arcPosition
        requiresShapeUpdate()
    }

    fun getCropDirection(): Int {
        return cropDirection
    }

    fun setCropDirection(@CropDirection cropDirection: Int) {
        this.cropDirection = cropDirection
        requiresShapeUpdate()
    }

    fun getArcHeight(): Int {
        return arcHeight
    }

    fun setArcHeight(arcHeight: Int) {
        this.arcHeight = arcHeight
        requiresShapeUpdate()
    }

    @IntDef(value = [POSITION_BOTTOM, POSITION_TOP, POSITION_LEFT, POSITION_RIGHT])
    annotation class ArcPosition

    @IntDef(value = [CROP_INSIDE, CROP_OUTSIDE])
    annotation class CropDirection

    companion object {
        const val POSITION_BOTTOM = 1
        const val POSITION_TOP = 2
        const val POSITION_LEFT = 3
        const val POSITION_RIGHT = 4
        const val CROP_INSIDE = 1
        const val CROP_OUTSIDE = 2
    }
}