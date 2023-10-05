package com.crust87.bouldering.app.main.type

import com.crust87.bouldering.data.bouldering.ListSort

data class MainUIState(
    val sort: ListSort = ListSort.DESC
)