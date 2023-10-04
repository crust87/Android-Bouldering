package com.kayadami.bouldering.app.main.type

import com.kayadami.bouldering.constants.Orientation
import com.kayadami.bouldering.data.bouldering.type.Bouldering

sealed class MainListItem(
    val id: Long,
    val updatedAt: Long,
    val type: Int
)

class BoulderingListItem(val bouldering: Bouldering) :
    MainListItem(bouldering.id, bouldering.updatedAt, bouldering.viewType)

object EmptyListItem : MainListItem(-1, 0, Orientation.NONE)

