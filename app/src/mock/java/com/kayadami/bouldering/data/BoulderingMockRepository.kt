package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingMockRepository @Inject constructor(
    mockDataInitializer: MockDataInitializer,
) : BoulderingDataSource {

    private var _sort = BoulderingDataSource.ListSort.DESC

    private val boulderingList = ArrayList<Bouldering>()

    private var _listFlow = MutableStateFlow(emptyList<Bouldering>())

    init {
        val initialData = mockDataInitializer.getMockList()

        boulderingList.addAll(initialData)
    }

    override fun list(sort: BoulderingDataSource.ListSort): Flow<List<Bouldering>> {
        _sort = sort

        return _listFlow.asStateFlow().also {
            invalidate()
        }
    }

    override suspend fun get(id: Long): Bouldering? {
        return boulderingList.find { it.id == id }
    }

    override suspend fun add(bouldering: Bouldering) {
        boulderingList.add(0, bouldering)

        invalidate()
    }

    override suspend fun update(bouldering: Bouldering) {
        val index = boulderingList.indexOf(bouldering)
        boulderingList[index] = bouldering

        invalidate()
    }

    override suspend fun remove(bouldering: Bouldering) {
        boulderingList.remove(bouldering)

        invalidate()
    }

    private fun invalidate() {
        when (_sort) {
            BoulderingDataSource.ListSort.DESC -> boulderingList.sort()
            BoulderingDataSource.ListSort.ASC -> boulderingList.apply {
                sort()
                reverse()
            }
        }

        _listFlow.tryEmit(ArrayList(boulderingList))
    }
}