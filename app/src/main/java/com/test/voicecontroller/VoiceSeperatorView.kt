package com.test.voicecontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View

class VoiceSeperatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val SEPERATOR_TYPE_CENTER = 0
        const val SEPERATOR_TYPE_LEFT = 1
        const val SEPERATOR_TYPE_RIGHT = 2

    }

    @JvmField
    var seperatorType = 0
    /**
     * 分割点颜色
     */
    private val dotColor = Color.RED
    /**
     * 点宽度
     */
    private val dotWidth: Int = ConvertUtil.dp2pxInt(2f)

    /**
     * 默认左边padding
     */
    private val defaultLeftPadding: Int = ConvertUtil.dp2pxInt(2f)

    /**
     * 默认右边padding
     */
    private val defaultRightPadding: Int = ConvertUtil.dp2pxInt(2f)

    /**
     * 背景圆角颜色
     */
    private val roundedRectColor = Color.BLACK

    /**
     * 圆角
     */
    private val roundedRectConerSize = ConvertUtil.dp2px(3f)

    var leftRoundedRectCorner = floatArrayOf(roundedRectConerSize, roundedRectConerSize, 0f, 0f, 0f, 0f, roundedRectConerSize, roundedRectConerSize)
    var rightRoundedRectCorner = floatArrayOf(0f, 0f, roundedRectConerSize, roundedRectConerSize, roundedRectConerSize, roundedRectConerSize, 0f, 0f)

    private val roundedRectShape by lazy {
        GradientDrawable().apply {
            setColor(roundedRectColor)
        }
    }

    private fun getCornerRadii(seperatorType: Int): FloatArray? {
        return when (seperatorType) {
            SEPERATOR_TYPE_LEFT -> leftRoundedRectCorner
            SEPERATOR_TYPE_RIGHT -> rightRoundedRectCorner
            else -> null
        }
    }

    /**
     * 画笔
     */
    private val dotPaint = Paint()

    init {
        dotPaint.color = dotColor
        dotPaint.isAntiAlias = true
        dotPaint.style = Paint.Style.FILL
        dotPaint.strokeWidth = dotWidth.toFloat()
        dotPaint.strokeCap = Paint.Cap.ROUND
        setPadding(defaultLeftPadding, 0, defaultRightPadding, 0)
    }

    /**
     * 设置分割点类型
     */
    fun setSeperatorType(type: Int) {
        this.seperatorType = type
        background = roundedRectShape.apply { cornerRadii = getCornerRadii(seperatorType) }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if(heightSpecMode == MeasureSpec.AT_MOST) {
            throw RuntimeException("height must be exactly")
        }
        // 宽度固定
        val widthSpecSize = paddingLeft + dotWidth + paddingRight
        setMeasuredDimension(widthSpecSize, heightSpecSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate((paddingLeft + (measuredWidth-paddingLeft-paddingRight)/2).toFloat(), (measuredHeight / 2).toFloat())
        // 背景
        canvas.drawPoint(0f, 0f, dotPaint)
        canvas.restore()
    }

}