package com.kayadami.bouldering.app.main

import com.kayadami.bouldering.data.type.Bouldering

sealed class MainViewModelEvent

object OpenSettingEvent: MainViewModelEvent()

class OpenViewerEvent(val data: Bouldering): MainViewModelEvent()

class OpenEditorEvent(val path: String): MainViewModelEvent()

object OpenCameraEvent: MainViewModelEvent()

object OpenGalleryEvent: MainViewModelEvent()