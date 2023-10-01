package com.kayadami.bouldering.app.viewer

import android.content.Intent
import android.widget.EditText
import com.kayadami.bouldering.data.type.Bouldering

sealed interface ViewerViewModelEvent

class OpenEditorEvent(val data: Bouldering): ViewerViewModelEvent

class OpenShareEvent(val intent: Intent): ViewerViewModelEvent

class FinishSaveEvent(val path: String?): ViewerViewModelEvent

object NavigateUpEvent : ViewerViewModelEvent

class OpenKeyboardEvent(val editText: EditText): ViewerViewModelEvent

class HideKeyboardEvent(val editText: EditText) : ViewerViewModelEvent

class ToastEvent(val message: String?): ViewerViewModelEvent