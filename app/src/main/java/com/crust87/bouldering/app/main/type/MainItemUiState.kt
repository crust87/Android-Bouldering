package com.crust87.bouldering.app.main.type

import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.data.getDate
import com.crust87.bouldering.editor.Orientation

enum class MainItemType {
    Empty,
    Square,
    Landscape,
    Portrait,
}

sealed class MainItemUiState(
    val id: Long,
    val thumb: String,
    val isSolved: Boolean,
    val displayDate: String,
    val updatedAt: Long,
    val type: MainItemType,
    val onClick: () -> Unit
)

class BoulderingItemUiState(bouldering: BoulderingEntity, onClick: () -> Unit) :
    MainItemUiState(
        bouldering.id,
        bouldering.thumb,
        bouldering.isSolved,
        bouldering.getDate(),
        bouldering.updatedAt,
        when (bouldering.orientation) {
            Orientation.ORIENTATION_LAND -> MainItemType.Landscape
            Orientation.ORIENTATION_PORT -> MainItemType.Portrait
            else -> MainItemType.Square
        },
        onClick
    )

object EmptyItemUiState : MainItemUiState(-1, "", false, "", 0, MainItemType.Empty, {})

