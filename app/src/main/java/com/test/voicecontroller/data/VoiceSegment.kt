package com.test.voicecontroller.data

class VoiceSegment(val type: Int = TYPE_NORMAL, val voiceData: List<VoiceItem> = ArrayList(), var selected: Boolean = false) {
    companion object {
        const val TYPE_NORMAL = 0 // 普通的音谱
        const val TYPE_DAXI = 1 // 搭戏
    }

}

/**
 * 每条音谱
 */
class VoiceItem( val amplitude: Float, val marked: Boolean)