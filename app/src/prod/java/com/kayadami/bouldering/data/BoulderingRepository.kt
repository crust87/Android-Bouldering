package com.kayadami.bouldering.data

import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.data.bouldering.BoulderingDao
import com.kayadami.bouldering.data.bouldering.ListSort
import com.kayadami.bouldering.data.bouldering.type.BoulderingEntity
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
    private val boulderingDao: BoulderingDao,
    private val commentDao: CommentDao,
    private @MainDispatcher val mainDispatcher: CoroutineDispatcher,
) {

    private var _sort = ListSort.DESC

    private var _listFlow = MutableStateFlow(emptyList<BoulderingEntity>())

    fun list(sort: ListSort): Flow<List<BoulderingEntity>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
    }

    suspend fun get(id: Long): BoulderingEntity? = boulderingDao.get(id)

    suspend fun add(bouldering: BoulderingEntity) {
        boulderingDao.insertAll(bouldering)

        invalidate()
    }

    suspend fun update(bouldering: BoulderingEntity) {
        bouldering.updatedAt = System.currentTimeMillis()
        boulderingDao.update(bouldering)

        invalidate()
    }

    suspend fun update(id: Long, title: String? = null, isSolved: Boolean? = null) {
        get(id)?.let {
            it.title = title ?: it.title
            it.isSolved = isSolved ?: it.isSolved

            update(it)
        }
    }

    suspend fun remove(id: Long) {
        boulderingDao.deleteById(id)
        commentDao.deleteByBoulderingId(id)

        invalidate()
    }

    private fun invalidate() = CoroutineScope(mainDispatcher).launch {
        _listFlow.emit(when (_sort) {
            ListSort.ASC -> boulderingDao.getAllASC()
            ListSort.DESC -> boulderingDao.getAllDESC()
        })
    }
}