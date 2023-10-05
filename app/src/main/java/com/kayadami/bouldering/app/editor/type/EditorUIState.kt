package com.kayadami.bouldering.app.editor.type

import android.view.View
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.editor.data.Holder

data class EditorUIState(
    val id: Long = 0,
    val data: BoulderingEntity? = null,
    val selected: Holder? = null,
    val title: String = "",
    val isNumberHolder: Boolean = false,
    val isSpecialHolder: Boolean = false,
    val isNumberEnabled: Boolean = false,
    val problemToolVisibility: Int = View.VISIBLE,
    val holderToolVisibility: Int = View.GONE,
    val progressVisibility: Int = View.GONE,
)