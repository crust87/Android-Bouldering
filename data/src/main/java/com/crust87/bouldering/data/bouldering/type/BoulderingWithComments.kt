package com.crust87.bouldering.data.bouldering.type

import androidx.room.Embedded
import androidx.room.Relation
import com.crust87.bouldering.data.comment.type.Comment

class BoulderingWithComments(
    @Embedded val bouldering: BoulderingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "boulderingId"
    )
    val comments: List<Comment>
)