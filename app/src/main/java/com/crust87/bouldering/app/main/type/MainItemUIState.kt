package com.crust87.bouldering.app.main.type

enum class MainItemType {
    Empty,
    Square,
    Landscape,
    Portrait,
}

sealed class MainItemUIState(
    val id: Long,
    val updatedAt: Long,
    val type: MainItemType,
)

class BoulderingItemUIState(
    id: Long,
    val thumb: String,
    val isSolved: Boolean,
    val displayDate: String,
    updatedAt: Long,
    type: MainItemType,
    val onClick: () -> Unit
) :
    MainItemUIState(id, updatedAt, type)

object EmptyItemUIState : MainItemUIState(0, -1, MainItemType.Empty)

