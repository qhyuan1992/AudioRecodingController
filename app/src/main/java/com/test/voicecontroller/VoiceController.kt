package com.test.voicecontroller

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.VoiceLinearSnapHelper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.test.voicecontroller.data.BaseVoiceItem
import com.test.voicecontroller.data.VoiceSegment
import com.test.voicecontroller.data.VoiceSeperator

class VoiceController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var playIv: ImageView
    private var centerLine: View
    private var deleteSegmentIv: ImageView
    private var recyclerView: RecyclerView

    private var layoutManager: LinearLayoutManager

    private var snapHelper: VoiceLinearSnapHelper

    private val adapter :VoiceAdapter
    private var headerAndFooterRecyclerViewAdapter: HeaderAndFooterRecyclerViewAdapter

    private var centerPosition = Point(0,0)
    private val centerLineConerSize = ConvertUtil.dp2px(2f)

    init {
        LayoutInflater.from(context).inflate(R.layout.voice_controller_layout, this, true)
        playIv = findViewById(R.id.play)
        centerLine = findViewById(R.id.centerLine)
        deleteSegmentIv = findViewById(R.id.deleteSegment)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = null

        centerLine.background = GradientDrawable().apply {
            setColor(Color.RED)
            cornerRadii = floatArrayOf(centerLineConerSize, centerLineConerSize, centerLineConerSize, centerLineConerSize, centerLineConerSize, centerLineConerSize, centerLineConerSize, centerLineConerSize)
        }
        snapHelper = VoiceLinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        adapter = VoiceAdapter(recyclerView)
        headerAndFooterRecyclerViewAdapter = HeaderAndFooterRecyclerViewAdapter(adapter)
        headerAndFooterRecyclerViewAdapter.addHeaderView(getEmptyView(context))
        headerAndFooterRecyclerViewAdapter.addFooterView(getEmptyView(context))
        recyclerView.adapter = headerAndFooterRecyclerViewAdapter
        deleteSegmentIv.setOnClickListener(this)
    }

    /**
     * 获取当前光标所在的Item的位置
     */
    fun getCursorPosition(): Int {
        val view = recyclerView.findChildViewUnder(centerPosition.x.toFloat(),centerPosition.y.toFloat())
        return if (view != null) {
            layoutManager.getPosition(view)
        } else {
            -1
        } - headerAndFooterRecyclerViewAdapter.headerViewsCount
    }

    /**
     * 播放时向左移动
     */
    fun play(dx: Int) {
        recyclerView.smoothScrollBy(dx, 0)
    }

    fun setData(voiceData: ArrayList<BaseVoiceItem>) {
        adapter.setData(voiceData)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerPosition.set(measuredWidth/2, measuredHeight/2)
    }

    override fun onClick(v: View?) {
        when (v) {
            deleteSegmentIv -> {
                // 删除片段
                adapter.onDeleteClick(getCursorPosition())
            }
        }
    }

    private fun getEmptyView(context: Context): View {
        return View(context).apply {
            val dm = context.resources.displayMetrics
            val screenWidth = Math.min(dm.widthPixels, dm.heightPixels)
            layoutParams = ViewGroup.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    class VoiceAdapter(val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            private const val VIEW_TYPE_VOICE_SEGMENT = 0
            private const val VIEW_TYPE_VOICE_SEPERATOR = 1
            private const val VIEW_TYPE_VOICE_SPACE = 2
        }
        val data = ArrayList<BaseVoiceItem>()

        fun setData(d: ArrayList<BaseVoiceItem>) {
            data.clear()
            data.addAll(d)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            val dataItem = data[position]
            if (viewHolder is VoiceSegmentViewHolder) {
                viewHolder.voiceView.apply {
                    setVoiceData(dataItem as VoiceSegment)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            } else if (viewHolder is VoiceSeperatorViewHolder) {
                viewHolder.seperatorView.setSeperatorType((dataItem as VoiceSeperator).type)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_VOICE_SEGMENT -> VoiceSegmentViewHolder(VoiceSegmentView(parent.context).apply {
                    setPadding(0,ConvertUtil.dp2pxInt(3f), 0, ConvertUtil.dp2pxInt(3f))
                    setBackgroundColor(Color.BLACK)
                })
                VIEW_TYPE_VOICE_SEPERATOR -> VoiceSeperatorViewHolder(VoiceSeperatorView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                })
                else -> VoiceSpaceViewHolder(View(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(ConvertUtil.dp2pxInt(1f), ViewGroup.LayoutParams.MATCH_PARENT)
                })
            }
        }

        override fun getItemViewType(position: Int): Int {
            val itemData = data[position]
            return when (itemData) {
                is VoiceSegment -> VIEW_TYPE_VOICE_SEGMENT
                is VoiceSeperator -> VIEW_TYPE_VOICE_SEPERATOR
                else -> VIEW_TYPE_VOICE_SPACE
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun onDeleteClick(cursorPosition: Int) {
            //
            if (cursorPosition >= 0 && cursorPosition < data.size) {
                val itemData = data[cursorPosition]
                if (itemData is VoiceSeperator) {
                    if (itemData.type == VoiceSeperator.TYPE_START) {
                        Toast.makeText(BaseApplication.application, "该位置无法删除", Toast.LENGTH_SHORT).show()
                    } else {
                        // 删除数据源直到遇到上一个 普通分隔符或者起始分隔符
                        val pendingDeleteIndexList = ArrayList<Int>()
                        val startItemData = data[cursorPosition]
                        pendingDeleteIndexList.add(cursorPosition)
                        for (i in cursorPosition-1 downTo 0) {
                            val currentItemData = data[i]
                            pendingDeleteIndexList.add(i)
                            if (currentItemData is VoiceSeperator) {
                                if (currentItemData.type == VoiceSeperator.TYPE_START) {
                                    // 遇到上一个start
                                    if (startItemData is VoiceSeperator && startItemData.type == VoiceSeperator.TYPE_NORMAL) {
                                        pendingDeleteIndexList.removeAt(pendingDeleteIndexList.size - 1)
                                    }
                                    break
                                } else if (currentItemData.type == VoiceSeperator.TYPE_NORMAL) {
                                    pendingDeleteIndexList.removeAt(0)
                                    break
                                }
                            }
                        }
                        //
                        Log.d("weiers", "delete index is $pendingDeleteIndexList")
                        val from = pendingDeleteIndexList[pendingDeleteIndexList.size - 1]
                        for (i in pendingDeleteIndexList) {// 逆序
                            data.removeAt(i)
                        }
                        notifyItemRangeRemoved(from, pendingDeleteIndexList.size)
                        // 滚动
                        var totalScrollWidth = 0
                        for (i in pendingDeleteIndexList) {
                            recyclerView.findViewHolderForLayoutPosition(i)?.let {
                                totalScrollWidth += it.itemView.width
                            }
                        }
                        Log.d("weiers", "totalScrollWidth=$totalScrollWidth")
                        recyclerView.smoothScrollBy(-totalScrollWidth, 0)
                    }
                }
            } else {
                Log.d("weiers", "onDeleteClick, cursorPosition error, cursorPosition is $cursorPosition")
            }
        }
    }

    class VoiceSegmentViewHolder(val voiceView: VoiceSegmentView): RecyclerView.ViewHolder(voiceView)

    class VoiceSeperatorViewHolder(val seperatorView: VoiceSeperatorView): RecyclerView.ViewHolder(seperatorView)

    class VoiceSpaceViewHolder(spaceView: View): RecyclerView.ViewHolder(spaceView)

    class VoiceSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            if (view is VoiceSeperatorView) {
                if (view.seperatorType == VoiceSeperatorView.SEPERATOR_TYPE_LEFT) {
                    outRect.left = space / 2
                } else if (view.seperatorType == VoiceSeperatorView.SEPERATOR_TYPE_RIGHT) {
                    outRect.right = space / 2
                }
            }
        }
    }

}

