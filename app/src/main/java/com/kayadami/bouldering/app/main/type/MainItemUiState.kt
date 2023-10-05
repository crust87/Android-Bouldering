package com.kayadami.bouldering.app.main.type

import com.kayadami.bouldering.constants.Orientation
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.kayadami.bouldering.data.getDate

sealed class MainItemUiState(
    val id: Long,
    val thumb: String,
    val isSolved: Boolean,
    val displayDate: String,
    val updatedAt: Long,
    val type: Int,
    val onClick: () -> Unit
)

class BoulderingItemUiState(bouldering: BoulderingEntity, onClick: () -> Unit) :
    MainItemUiState(
        bouldering.id,
        bouldering.thumb,
        bouldering.isSolved,
        bouldering.getDate(),
        bouldering.updatedAt,
        bouldering.viewType,
        onClick)

object EmptyItemUiState : MainItemUiState(-1, "", false, "", 0, Orientation.NONE, {})

