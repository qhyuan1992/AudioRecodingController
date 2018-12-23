package com.test.voicecontroller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.test.voicecontroller.VoiceSeperatorView.Companion.SEPERATOR_TYPE_CENTER
import com.test.voicecontroller.VoiceSeperatorView.Companion.SEPERATOR_TYPE_LEFT
import com.test.voicecontroller.VoiceSeperatorView.Companion.SEPERATOR_TYPE_RIGHT
import com.test.voicecontroller.data.*
import com.test.voicecontroller.data.VoiceSegment.Companion.TYPE_NORMAL
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seperaorView1.setSeperatorType(SEPERATOR_TYPE_LEFT)
        seperaorView2.setSeperatorType(SEPERATOR_TYPE_CENTER)
        seperaorView3.setSeperatorType(SEPERATOR_TYPE_RIGHT)

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

        voiceController.setData(getTestData())
    }
    // for test
    fun getTestData(): ArrayList<BaseVoiceItem > {
        val data = ArrayList<BaseVoiceItem >()
        for (j in 1..8) {
            data.add(VoiceSeperator(VoiceSeperator.TYPE_START))
            for (i in 1..4) {
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
                data.add(VoiceSeperator())
            }
            data.removeAt(data.size - 1)
            data.add(VoiceSeperator(VoiceSeperator.TYPE_END))
            data.add(VoiceSpaceItem())
        }
        return data
    }

}
