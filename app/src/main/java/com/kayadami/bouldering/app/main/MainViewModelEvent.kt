package com.kayadami.bouldering.app.main

import android.content.Intent
import com.kayadami.bouldering.data.bouldering.type.Bouldering

sealed interface MainViewModelEvent

object OpenSettingEvent: MainViewModelEvent

class OpenViewerEvent(val data: Bouldering): MainViewModelEvent

class OpenEditorEvent(val path: String): MainViewModelEvent

class OpenCameraEvent(val intent: Intent): MainViewModelEvent

class OpenGalleryEvent(val intent: Intent): MainViewModelEvent