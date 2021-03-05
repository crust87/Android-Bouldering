package com.kayadami.bouldering.data

import android.util.Log
import com.kayadami.bouldering.app.setting.opensourcelicense.OpenSourceLicense
import com.kayadami.bouldering.editor.data.Bouldering
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BoulderingRepository(var preferences: PreferencesDataSource, var storage: StorageDataSource) : BoulderingDataSource {

    private var _listChannel: Channel<List<Bouldering>>? = null

    override fun list() = flow {
        _listChannel = Channel()

        emit(preferences.get())

        _listChannel?.consumeEach {
            emit(it)
        }
    }

    override operator fun get(createDate: Long): Bouldering? {
        return preferences[createDate]
    }

    override fun add(bouldering: Bouldering) {
        preferences.add(bouldering)
    }

    override fun remove(bouldering: Bouldering) {
        preferences.remove(bouldering)
    }

    override fun restore() {
        preferences.restore()
    }

    override fun exportAll(): Boolean {
        try {
            storage.exportAll(preferences.get())

            return true
        } catch (e: Throwable) {
            return false
        }
    }

    override fun importAll(): Boolean {
        try {
            val boulderingList = storage.importAll()
            boulderingList.forEach {
                preferences.add(it)
            }

            return true
        } catch (e: Throwable) {
            return false
        }
    }

    override fun getOpenSourceList() : List<OpenSourceLicense> {
        return ArrayList<OpenSourceLicense>().apply {
            add(OpenSourceLicense("Android Architecture Blueprints",
                    "https://github.com/googlesamples/android-architecture",
                    "Copyright 2019 Google Inc.\nApache License, Version 2.0"))

            add(OpenSourceLicense("PhotoView",
                    "https://github.com/chrisbanes/PhotoView",
                    "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"))

            add(OpenSourceLicense("Color Picker",
                    "https://github.com/QuadFlask/colorpicker",
                    "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"))
        }
    }
}