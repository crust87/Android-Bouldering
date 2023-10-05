package com.kayadami.bouldering.data

import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.data.bouldering.type.HolderData
import com.kayadami.bouldering.editor.Bouldering
import com.kayadami.bouldering.editor.Holder
import com.kayadami.bouldering.utils.DateUtils

fun BoulderingEntity.asEditorBouldering() = Bouldering(
    path = path,
    thumb = thumb,
    color = color,
    holderList = holderList.map { it.asEditorHolder() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    orientation = orientation,
)

fun Bouldering.asBoulderingEntity(old: BoulderingEntity?) = BoulderingEntity(
    id = old?.id ?: 0,
    path = path,
    thumb = thumb,
    title = old?.title ?: "",
    isSolved = old?.isSolved ?: false,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
    holderList = holderList.map { it.asHolderData() },
    orientation = orientation,
)

fun HolderData.asEditorHolder() = Holder(
    x = x,
    y = y,
    radius = radius,
    isSpecial = isSpecial,
    isInOrder = isInOrder,
    index = index,
)

fun Holder.asHolderData() = HolderData(
    x = x,
    y = y,
    radius = radius,
    isSpecial = isSpecial,
    isInOrder = isInOrder,
    index = index,
)

fun BoulderingEntity.getDate(): String {
    return DateUtils.convertDate(createdAt)
}