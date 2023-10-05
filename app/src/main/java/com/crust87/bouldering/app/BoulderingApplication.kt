package com.crust87.bouldering.app

import android.app.Application
import com.crust87.util.DateUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoulderingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        com.crust87.util.DateUtils.initialize(applicationContext)
    }
}
