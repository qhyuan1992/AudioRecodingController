package com.test.voicecontroller.data

class VoiceSeperator(val type: Int = TYPE_NORMAL): BaseVoiceItem {
    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_START = 1
        const val TYPE_END = 2
    }
}