package com.kayadami.bouldering.app.viewer.type

import android.view.View
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity

data class ViewerUIState(
    val id: Long = 0,
    val data: BoulderingEntity? = null,
    val title: String = "",
    val lastModify: String = "",
    val isSolved: Boolean = false,
    val infoVisibility: Int = View.VISIBLE,
    val progressVisibility: Int = View.GONE,
)