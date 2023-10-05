package com.crust87.bouldering.data

import com.crust87.bouldering.data.bouldering.ListSort
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BoulderingRepository(
    mockDataInitializer: MockDataInitializer,
) {

    private var _sort = ListSort.DESC

    private val boulderingList = ArrayList<BoulderingEntity>()

    private var _listFlow = MutableStateFlow(emptyList<BoulderingEntity>())

    init {
        val initialData = mockDataInitializer.getMockList()

        boulderingList.addAll(initialData)
    }

    fun list(sort: ListSort): Flow<List<BoulderingEntity>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
    }

    suspend fun get(id: Long): BoulderingEntity? {
        return boulderingList.find { it.id == id }
    }

    suspend fun add(bouldering: BoulderingEntity) {
        boulderingList.add(0, bouldering)

        invalidate()
    }

    suspend fun update(bouldering: BoulderingEntity) {
        val index = boulderingList.indexOf(bouldering)
        boulderingList[index] = bouldering

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
        boulderingList.removeAll { it.id == id }

        invalidate()
    }

    private fun invalidate() {
        when (_sort) {
            ListSort.DESC -> boulderingList.sort()
            ListSort.ASC -> boulderingList.apply {
                sort()
                reverse()
            }
        }

        _listFlow.tryEmit(ArrayList(boulderingList))
    }
}