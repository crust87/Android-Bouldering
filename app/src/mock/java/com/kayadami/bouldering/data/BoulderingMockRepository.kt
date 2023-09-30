package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.type.Bouldering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingMockRepository @Inject constructor(
    mockDataInitializer: MockDataInitializer,
) : BoulderingDataSource {

    private val boulderingList = ArrayList<Bouldering>()

    private var _listFlow = MutableStateFlow(boulderingList)

    init {
        val initialData = mockDataInitializer.getMockList()

        boulderingList.addAll(initialData)

        _listFlow.tryEmit(boulderingList)
    }

    override fun list() = _listFlow.asStateFlow()

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

    private suspend fun invalidate() {
        _listFlow.emit(boulderingList)
    }
}