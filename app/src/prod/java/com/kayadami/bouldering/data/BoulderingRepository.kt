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

    override suspend fun get(id: Long): Bouldering? {
        return withContext(Dispatchers.IO) {
            database.boulderingDao().get(id)
        }
    }

    override suspend fun add(bouldering: Bouldering) {
        withContext(Dispatchers.IO) {
            database.boulderingDao().insertAll(bouldering)
        }

        invalidate()
    }

    override suspend fun update(bouldering: Bouldering) {
        withContext(Dispatchers.IO) {
            database.boulderingDao().update(bouldering)
        }

        invalidate()
    }

    override suspend fun remove(bouldering: Bouldering) {
        withContext(Dispatchers.IO) {
            database.boulderingDao().delete(bouldering)
        }

        invalidate()
    }

    private suspend fun invalidate() {
        withContext(Dispatchers.IO) {
            database.boulderingDao().getAll()
        }.let {
            _listChannel?.trySend(it)
        }
    }
}