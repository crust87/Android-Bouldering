package com.kayadami.bouldering.editor.data

import com.google.gson.annotations.SerializedName

open class Holder {
    @field:SerializedName("x")
    var x: Float = 0f
    @field:SerializedName("y")
    var y: Float = 0f
    @field:SerializedName("radius")
    var radius: Float = 0f
    @field:SerializedName("isSpecial")
    var isSpecial: Boolean = false
        set(value) {
            field = value
            if (value) {
                isInOrder = false
                index = 0
            }
        }

    @field:SerializedName("isInOrder")
    var isInOrder: Boolean = false
    @field:SerializedName("index")
    var index: Int = 0

    constructor(x: Float = 0f,
                y: Float = 0f,
                radius: Float = 0f,
                isSpecial: Boolean = false,
                isInOrder: Boolean = false,
                index: Int = 0) {
        this@Holder.x = x
        this@Holder.y = y
        this@Holder.radius = radius
        this@Holder.isSpecial = isSpecial
        this@Holder.isInOrder = isInOrder
        this@Holder.index = index
    }

    val isNotSpecial: Boolean
        get() = !isSpecial
}