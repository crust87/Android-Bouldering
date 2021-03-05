package com.kayadami.bouldering.app

import android.app.Application
import com.kayadami.bouldering.utils.DateUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoulderingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DateUtils.initialize(applicationContext)
    }
}
