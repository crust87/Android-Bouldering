package com.kayadami.bouldering.data

import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.data.bouldering.BoulderingDao
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.comment.CommentDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingRepository @Inject constructor(
    val boulderingDao: BoulderingDao,
    val commentDao: CommentDao,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
) : BoulderingDataSource {

    private var _sort = BoulderingDataSource.ListSort.DESC

    private var _listFlow = MutableStateFlow(emptyList<Bouldering>())

    override fun list(sort: BoulderingDataSource.ListSort): Flow<List<Bouldering>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
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

    private fun invalidate() = CoroutineScope(mainDispatcher).launch {
        _listFlow.emit(when (_sort) {
            BoulderingDataSource.ListSort.ASC -> boulderingDao.getAllASC()
            BoulderingDataSource.ListSort.DESC -> boulderingDao.getAllDESC()
        })
    }
}