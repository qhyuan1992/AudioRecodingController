package com.test.voicecontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.test.voicecontroller.data.VoiceItem
import com.test.voicecontroller.data.VoiceSegment
import java.lang.RuntimeException

/**
 * 声音片段
 */
class VoiceSegmentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    /**
     * 线宽1dp
     */
    private var lineWidth = ConvertUtil.dp2px(1f)

    /**
     * 线颜色
     */
    private var lineColor = Color.WHITE

    /**
     * 线颜色
     */
    private var lineMarkedColor = Color.RED

    /**
     * 线条之间的间距 1dp
     */
    private var lineSpace = ConvertUtil.dp2px(1f)

    /**
     * 线条和点之间的间距 2dp
     */
    private var lineDotSpace = ConvertUtil.dp2px(2f)

    /**
     * 矩形线条画笔
     */
    private val linePaint = Paint()

    /**
     * 点画笔
     */
    private val dotPaint = Paint()

    /**
     * 分割点Color
     */
    private val seperatorDotColor = Color.RED

    /**
     * 分割点宽度
     */
    private val dotWidth = ConvertUtil.dp2px(2f)

    init {
        linePaint.color = lineColor
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = lineWidth
        linePaint.strokeCap = Paint.Cap.ROUND


        dotPaint.color = seperatorDotColor
        dotPaint.isAntiAlias = true
        dotPaint.style = Paint.Style.FILL
        dotPaint.strokeWidth = dotWidth
        dotPaint.strokeCap = Paint.Cap.ROUND
    }

    private var voiceSegment: VoiceSegment = VoiceSegment()

    // 每隔0.5s一个数据, 间隔1dp  [0-1]
    fun setVoiceData(voiceSegment: VoiceSegment) {
        this.voiceSegment = voiceSegment
    }

    /**
     * 设置宽度wrap_content根据内容计算其宽度
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if(heightSpecMode == MeasureSpec.AT_MOST) {
            // 高度必须为精确值
            throw RuntimeException("height must be exactly")
        }
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)

        // RecyclerView中的item使用包裹内容得到的是UNSPECIFIED模式
        if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            widthSpecSize = ((dotWidth + lineDotSpace) * 2 + (2*voiceSegment.voiceData.size - 1) * lineWidth).toInt() + paddingLeft + paddingRight
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制所有的数据
        val maxHeight = measuredHeight - paddingTop - paddingBottom
        canvas.save()
        canvas.translate(paddingLeft + dotWidth/2, (measuredHeight / 2).toFloat())
        // 绘制分割点
        canvas.drawPoint(0f, 0f, dotPaint)
        canvas.translate(dotWidth/2 + lineWidth/2 + lineDotSpace, 0f)

        val voiceData = voiceSegment.voiceData
        for (i in voiceData.indices) {
            val voice = voiceData[i]
            // 绘制线条
            val startY = voice.amplitude * maxHeight / 2
            if (voice.marked) {
                linePaint.color = lineMarkedColor
            } else {
                linePaint.color = lineColor
            }
            canvas.drawLine(0f, -startY, 0f, startY, linePaint)
            if (i == voiceData.size - 1) {
                canvas.translate(dotWidth/2 + lineWidth/2 + lineDotSpace, 0f)
            } else {
                canvas.translate(lineWidth + lineSpace, 0f)
            }
        }
        // 绘制分割点
        canvas.drawPoint(0f, 0f, dotPaint)
        canvas.restore()
    }

}