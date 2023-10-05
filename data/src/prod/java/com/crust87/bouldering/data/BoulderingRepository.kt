package com.crust87.bouldering.data

import com.crust87.bouldering.data.bouldering.BoulderingDao
import com.crust87.bouldering.data.comment.CommentDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoulderingRepository(
    private val boulderingDao: BoulderingDao,
    private val commentDao: CommentDao,
    private val mainDispatcher: CoroutineDispatcher,
) {

    private var _sort = com.crust87.bouldering.data.bouldering.ListSort.DESC

    private var _listFlow = MutableStateFlow(emptyList<com.crust87.bouldering.data.bouldering.type.BoulderingEntity>())

    fun list(sort: com.crust87.bouldering.data.bouldering.ListSort): Flow<List<com.crust87.bouldering.data.bouldering.type.BoulderingEntity>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
    }

    suspend fun get(id: Long): com.crust87.bouldering.data.bouldering.type.BoulderingEntity? = boulderingDao.get(id)

    suspend fun add(bouldering: com.crust87.bouldering.data.bouldering.type.BoulderingEntity) {
        boulderingDao.insertAll(bouldering)

        invalidate()
    }

    suspend fun update(bouldering: com.crust87.bouldering.data.bouldering.type.BoulderingEntity) {
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
            com.crust87.bouldering.data.bouldering.ListSort.ASC -> boulderingDao.getAllASC()
            com.crust87.bouldering.data.bouldering.ListSort.DESC -> boulderingDao.getAllDESC()
        })
    }
}