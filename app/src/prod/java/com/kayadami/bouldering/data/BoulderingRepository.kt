package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.bouldering.BoulderingDao
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.comment.CommentDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingRepository @Inject constructor(
    val boulderingDao: BoulderingDao,
    val commentDao: CommentDao,
) : BoulderingDataSource {

    private var _listFlow = MutableStateFlow(emptyList<Bouldering>())

    override fun list() = _listFlow.asStateFlow().also {
        invalidate()
    }

    override suspend fun get(id: Long): Bouldering? = boulderingDao.get(id)

    override suspend fun add(bouldering: Bouldering) {
        boulderingDao.insertAll(bouldering)

        invalidate()
    }

    override suspend fun update(bouldering: Bouldering) {
        bouldering.updatedAt = System.currentTimeMillis()
        boulderingDao.update(bouldering)

        invalidate()
    }

    override suspend fun remove(bouldering: Bouldering) {
        boulderingDao.delete(bouldering)
        commentDao.deleteByBoulderingId(bouldering.id)

        invalidate()
    }

    private fun invalidate() = CoroutineScope(Dispatchers.Main).launch {
        _listFlow.emit(boulderingDao.getAll())
    }
}