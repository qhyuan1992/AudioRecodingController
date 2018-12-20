package com.test.voicecontroller

import android.content.Context

object ConvertUtil {
    fun dp2px(dp: Float, context: Context = BaseApplication.application): Float =
        dp * context.resources.displayMetrics.density

    fun dp2pxInt(dp: Float, context: Context = BaseApplication.application): Int =
        Math.round(dp * context.resources.displayMetrics.density)
}