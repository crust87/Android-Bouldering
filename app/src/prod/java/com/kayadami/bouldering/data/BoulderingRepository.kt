package com.kayadami.bouldering.data

import com.kayadami.bouldering.app.setting.opensourcelicense.OpenSourceLicense
import com.kayadami.bouldering.data.type.Bouldering
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingRepository @Inject constructor(
    var database: AppDatabase
) : BoulderingDataSource {

    private var _listChannel: Channel<List<Bouldering>>? = null

    override fun list() = flow {
        _listChannel = Channel()

        val data = withContext(Dispatchers.IO) {
            database.boulderingDao().getAll()
        }

        emit(data)

        _listChannel?.consumeEach {
            emit(it)
        }
    }

    override operator fun get(id: Int): Bouldering? {
        return database.boulderingDao().get(id)
    }

    override fun add(bouldering: Bouldering) {
        database.boulderingDao().insertAll(bouldering)

        database.boulderingDao().getAll().let {
            _listChannel?.trySend(it)
        }
    }

    override fun update(bouldering: Bouldering) {
        database.boulderingDao().update(bouldering)

        database.boulderingDao().getAll().let {
            _listChannel?.trySend(it)
        }
    }

    override fun remove(bouldering: Bouldering) {
        database.boulderingDao().delete(bouldering)

        database.boulderingDao().getAll().let {
            _listChannel?.trySend(it)
        }
    }

    override fun getOpenSourceList(): List<OpenSourceLicense> {
        return ArrayList<OpenSourceLicense>().apply {
            add(
                OpenSourceLicense(
                    "Android Architecture Blueprints",
                    "https://github.com/googlesamples/android-architecture",
                    "Copyright 2019 Google Inc.\nApache License, Version 2.0"
                )
            )

            add(
                OpenSourceLicense(
                    "PhotoView",
                    "https://github.com/chrisbanes/PhotoView",
                    "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"
                )
            )

            add(
                OpenSourceLicense(
                    "Color Picker",
                    "https://github.com/QuadFlask/colorpicker",
                    "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"
                )
            )
        }
    }
}