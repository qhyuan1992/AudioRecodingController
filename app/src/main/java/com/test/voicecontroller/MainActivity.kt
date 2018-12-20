package com.test.voicecontroller

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.FooterAlignLinearSnapHelper
import android.support.v7.widget.SnapHelper
import android.util.Log
import com.test.voicecontroller.data.VoiceItem
import com.test.voicecontroller.data.VoiceSegment
import com.test.voicecontroller.data.VoiceSegment.Companion.TYPE_NORMAL


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val adapter = VoiceAdapter()
    private lateinit var snapHelper: SnapHelper
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var headerAndFooterRecyclerViewAdapter: HeaderAndFooterRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = ArrayList<VoiceItem>().apply {
            add(VoiceItem(0f,true))
            add(VoiceItem(0.3f,true))
            add(VoiceItem(0.1f,true))
            add(VoiceItem(0.2f,true))
            add(VoiceItem(0.3f,true))
            add(VoiceItem(0.4f,true))
            add(VoiceItem(0.5f,true))
            add(VoiceItem(1f,true))
            add(VoiceItem(0.9f,true))
            add(VoiceItem(0.8f,false))
            add(VoiceItem(0.9f,false))
            add(VoiceItem(0.8f,false))
            add(VoiceItem(0.5f,false))
            add(VoiceItem(0.2f,false))
            add(VoiceItem(0.1f,false))
            add(VoiceItem(0.3f,false))
        }
        voiceView.setVoiceData(VoiceSegment(TYPE_NORMAL, list))

        val data = ArrayList< VoiceSegment >()
        for (i in 1..10) {
            val l = ArrayList<VoiceItem>().apply {
                add(VoiceItem(0.3f,true))
                add(VoiceItem(0.1f,true))
                add(VoiceItem(0.2f,true))
                add(VoiceItem(0.3f,true))
                add(VoiceItem(0.4f,true))
                add(VoiceItem(0.5f,true))
                add(VoiceItem(1f,true))
                add(VoiceItem(0.9f,true))
                add(VoiceItem(0.8f,false))
                add(VoiceItem(0.9f,false))
                add(VoiceItem(0.1f,false))
                add(VoiceItem(0.5f,false))
                add(VoiceItem(0.2f,false))
                add(VoiceItem(0.3f,false))
                add(VoiceItem(0.8f,false))
                add(VoiceItem(0.5f,false))
                add(VoiceItem(0.2f,false))
                add(VoiceItem(0.1f,false))
                add(VoiceItem(0.3f,false))
            }
            data.add(VoiceSegment(TYPE_NORMAL, l))
        }
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        snapHelper = FooterAlignLinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        headerAndFooterRecyclerViewAdapter = HeaderAndFooterRecyclerViewAdapter(adapter)
        headerAndFooterRecyclerViewAdapter.addHeaderView(getEmptyView(this))
        headerAndFooterRecyclerViewAdapter.addFooterView(getEmptyView(this))
        recyclerView.adapter = headerAndFooterRecyclerViewAdapter
        adapter.setData(data)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position = getCursorPosition()
                    Log.d("weiers", "onScrollStateChanged idle, current position is $position")
                }
            }
        })
        deleteSegment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            deleteSegment -> {
                // 删除片段
                adapter.onDeleteClick(getCursorPosition())
            }
        }

    }

    /**
     * 获取当前光标所在的位置：表示左边的音频片段在RecyclerView中的位置，右边的音频片段在RecyclerView中的位置+1
     * 没有滚动时调用该函数
     */
    fun getCursorPosition(): Int {
        val view = snapHelper.findSnapView(layoutManager)
        return if (view != null) {
            layoutManager.getPosition(view)
        } else {
            -1
        } - headerAndFooterRecyclerViewAdapter.headerViewsCount
    }

    private fun getEmptyView(context: Context): View {
        return View(context).apply {
            val dm = context.resources.displayMetrics
            val screenWidth = Math.min(dm.widthPixels, dm.heightPixels)
            layoutParams = ViewGroup.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    class VoiceAdapter: RecyclerView.Adapter<ViewHolder>() {
        val data = ArrayList<VoiceSegment>()

        fun setData(d: ArrayList<VoiceSegment>) {
            data.clear()
            data.addAll(d)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val dataItem = data[position]
            viewHolder.voiceView.apply {
                setVoiceData(dataItem)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBackgroundColor(if (dataItem.selected) Color.parseColor("#FF0069") else Color.TRANSPARENT)
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(VoiceSegmentView(p0.context).apply {
                setBackgroundColor(Color.BLACK)
            })
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun onDeleteClick(cursorPosition: Int) {
            //
            if (cursorPosition >= 0 && cursorPosition < data.size) {
                val segment = data[cursorPosition]
                if (segment.selected) {
                    // 删除
                    data.removeAt(cursorPosition)
                    notifyItemRemoved(cursorPosition)
                } else {
                    segment.selected = true
                    notifyItemChanged(cursorPosition)
                }
            } else {
                Log.d("weiers", "onDeleteClick, cursorPosition error, cursorPosition is $cursorPosition")
            }
        }
    }

    class ViewHolder(val voiceView: VoiceSegmentView): RecyclerView.ViewHolder(voiceView)
}
