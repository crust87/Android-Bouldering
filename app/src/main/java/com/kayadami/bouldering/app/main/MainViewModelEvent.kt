package com.kayadami.bouldering.app.main

sealed interface MainViewModelEvent

class OpenViewerEvent(val id: Long): MainViewModelEvent

object ListSortChangeEvent: MainViewModelEvent