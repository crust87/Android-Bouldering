package com.kayadami.bouldering.app

import android.app.Application
import com.kayadami.bouldering.utils.DateUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BoulderingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DateUtils.initialize(applicationContext)

        startKoin {
            androidContext(this@BoulderingApplication)
            modules(
                Injection.get(this@BoulderingApplication)
            )
        }
    }
}
