package com.kayadami.bouldering.data

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
}