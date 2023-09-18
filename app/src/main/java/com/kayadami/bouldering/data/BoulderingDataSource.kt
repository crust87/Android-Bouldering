package com.kayadami.bouldering.data

import com.kayadami.bouldering.app.setting.opensourcelicense.OpenSourceLicense
import com.kayadami.bouldering.data.type.Bouldering
import kotlinx.coroutines.flow.Flow

interface BoulderingDataSource {

    fun list(): Flow<List<Bouldering>>

    operator fun get(id: Int): Bouldering?

    fun add(bouldering: Bouldering)

    fun update(bouldering: Bouldering)

    fun remove(bouldering: Bouldering)

    fun getOpenSourceList(): List<OpenSourceLicense>
}