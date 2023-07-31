package com.kayadami.bouldering.app.editor

import android.content.Context
import com.kayadami.bouldering.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorResourceManager @Inject constructor(@ApplicationContext val context: Context) {

    val editText: String
        get() = context.resources.getString(R.string.editor_edit)
    val createText: String
        get() = context.resources.getString(R.string.editor_create)
}