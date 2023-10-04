package com.kayadami.bouldering.app.viewer.type

import android.view.View
import com.kayadami.bouldering.data.bouldering.type.Bouldering

data class ViewerUIState(
    val id: Long = 0,
    val data: Bouldering? = null,
    val title: String = "",
    val lastModify: String = "",
    val isSolved: Boolean = false,
    val infoVisibility: Int = View.VISIBLE,
    val progressVisibility: Int = View.GONE,
)