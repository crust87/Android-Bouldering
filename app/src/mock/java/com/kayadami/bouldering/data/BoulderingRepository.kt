package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.bouldering.ListSort
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingRepository @Inject constructor(
    mockDataInitializer: MockDataInitializer,
) {

    private var _sort = ListSort.DESC

    private val boulderingList = ArrayList<Bouldering>()

    private var _listFlow = MutableStateFlow(emptyList<Bouldering>())

    init {
        val initialData = mockDataInitializer.getMockList()

        boulderingList.addAll(initialData)
    }

    fun list(sort: ListSort): Flow<List<Bouldering>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
    }

    suspend fun get(id: Long): Bouldering? {
        return boulderingList.find { it.id == id }
    }

    suspend fun add(bouldering: Bouldering) {
        boulderingList.add(0, bouldering)

        invalidate()
    }

    suspend fun update(bouldering: Bouldering) {
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