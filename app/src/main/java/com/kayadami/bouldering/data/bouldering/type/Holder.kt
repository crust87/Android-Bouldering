package com.kayadami.bouldering.data.bouldering.type

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
open class Holder(
    @Json(name = "x")
    var x: Float = 0f,
    @Json(name = "y")
    var y: Float = 0f,
    @Json(name = "radius")
    var radius: Float = 0f,
    @Json(name = "isSpecial")
    var isSpecial: Boolean = false,
    @Json(name = "isInOrder")
    var isInOrder: Boolean = false,
    @Json(name = "index")
    var index: Int = 0
) {

    val isNotSpecial: Boolean
        get() = !isSpecial
}