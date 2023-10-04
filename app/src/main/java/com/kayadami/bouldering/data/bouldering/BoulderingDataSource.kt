package com.kayadami.bouldering.data.bouldering

import com.kayadami.bouldering.data.bouldering.type.Bouldering
import kotlinx.coroutines.flow.Flow

interface BoulderingDataSource {

    enum class ListSort {
        ASC,
        DESC
    }

    fun list(sort: ListSort): Flow<List<Bouldering>>

    suspend fun get(id: Long): Bouldering?

    suspend fun add(bouldering: Bouldering)

    suspend fun update(bouldering: Bouldering)

    suspend fun update(id: Long, title: String? = null, isSolved: Boolean? = null)

    suspend fun remove(bouldering: Bouldering)

    suspend fun remove(id: Long)
}