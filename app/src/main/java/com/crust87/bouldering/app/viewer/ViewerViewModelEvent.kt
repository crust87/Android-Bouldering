package com.crust87.bouldering.app.viewer

import android.content.Intent

sealed interface ViewerViewModelEvent

class OpenEditorEvent(val id: Long): ViewerViewModelEvent

object OpenCommentEvent: ViewerViewModelEvent

class OpenShareEvent(val intent: Intent): ViewerViewModelEvent