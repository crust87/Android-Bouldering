package com.kayadami.bouldering.data.type

import androidx.room.Embedded
import androidx.room.Relation

class BoulderingWithComments(
    @Embedded val bouldering: Bouldering,
    @Relation(
        parentColumn = "id",
        entityColumn = "boulderingId"
    )
    val comments: List<Comment>
)