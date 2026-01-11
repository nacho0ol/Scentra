package com.example.scentra

import android.app.Application
import com.example.scentra.repositori.AppContainer
import com.example.scentra.repositori.ScentraContainer

class ScentraApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = ScentraContainer()
    }
}