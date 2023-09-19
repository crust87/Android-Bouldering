package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.type.Bouldering
import kotlinx.coroutines.flow.Flow

interface BoulderingDataSource {

    fun list(): Flow<List<Bouldering>>

    suspend fun get(id: Long): Bouldering?

    suspend fun add(bouldering: Bouldering)

    suspend fun update(bouldering: Bouldering)

    suspend fun remove(bouldering: Bouldering)
}