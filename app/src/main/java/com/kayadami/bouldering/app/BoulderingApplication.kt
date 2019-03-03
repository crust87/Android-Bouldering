package com.kayadami.bouldering.app

import android.app.Application
import com.kayadami.bouldering.utils.DateUtils
import org.koin.android.ext.android.startKoin

class BoulderingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DateUtils.initialize(applicationContext)

        startKoin(this, listOf(Injection.get(this)))
    }
}
