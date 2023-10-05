package com.crust87.bouldering.app.main

sealed interface MainViewModelEvent

class OpenViewerEvent(val id: Long): MainViewModelEvent

object ListSortChangeEvent: MainViewModelEvent