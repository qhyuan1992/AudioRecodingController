package com.test.voicecontroller

import android.app.Application

class BaseApplication : Application() {
    companion object {
        lateinit var application : Application
    }
    override fun onCreate() {
        super.onCreate()
        application = this
    }
}