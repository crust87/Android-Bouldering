package com.crust87.bouldering.app.editor

sealed interface EditorViewModelEvent

object OpenColorPickerEvent : EditorViewModelEvent

object NavigateUpEvent : EditorViewModelEvent

class ToastEvent(val message: String?): EditorViewModelEvent