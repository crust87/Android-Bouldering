package com.kayadami.bouldering.app.editor

import android.content.Context
import com.kayadami.bouldering.R

class EditorResourceManager(val context: Context) {

    val editText: String
        get() = context.resources.getString(R.string.editor_edit)
    val createText: String
        get() = context.resources.getString(R.string.editor_create)
}