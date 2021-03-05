package com.kayadami.bouldering.data

import com.kayadami.bouldering.app.setting.opensourcelicense.OpenSourceLicense
import com.kayadami.bouldering.editor.data.Bouldering
import kotlinx.coroutines.flow.Flow

interface BoulderingDataSource {

    fun list(): Flow<List<Bouldering>>

    operator fun get(createDate: Long): Bouldering?

    fun add(bouldering: Bouldering)

    fun remove(bouldering: Bouldering)

    fun restore()

    fun exportAll(): Boolean

    fun importAll(): Boolean

    fun getOpenSourceList(): List<OpenSourceLicense>
}