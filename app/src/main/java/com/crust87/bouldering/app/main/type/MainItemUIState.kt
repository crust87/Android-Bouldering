package com.crust87.bouldering.app.main.type

data class MainItemUIState(
    val id: Long = 0,
    val thumb: String = "",
    val isSolved: Boolean = false,
    val displayDate: String = "",
    val updatedAt: Long = -1,
    val type: MainItemType = MainItemType.Empty,
    val onClick: () -> Unit = {}
)

