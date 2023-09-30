package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.type.Bouldering
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingRepository @Inject constructor(
    var database: AppDatabase
) : BoulderingDataSource {

    private var _listFlow = MutableStateFlow(emptyList<Bouldering>())

    override fun list() = _listFlow.asStateFlow().also {
        invalidate()
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

    private fun invalidate() = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            database.boulderingDao().getAll()
        }.let {
            _listFlow.emit(it)
        }
    }
}