package com.kayadami.bouldering.editor

class Bouldering(
    var path: String,
    var thumb: String,
    var color: String?,
    var createdAt: Long,
    var updatedAt: Long,
    var holderList: List<Holder>,
    var orientation: Int
)