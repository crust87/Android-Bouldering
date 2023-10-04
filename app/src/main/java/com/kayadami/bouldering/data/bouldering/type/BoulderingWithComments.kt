package com.kayadami.bouldering.data.bouldering.type

import androidx.room.Embedded
import androidx.room.Relation
import com.kayadami.bouldering.data.comment.type.Comment

class BoulderingWithComments(
    @Embedded val bouldering: BoulderingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "boulderingId"
    )
    val comments: List<Comment>
)