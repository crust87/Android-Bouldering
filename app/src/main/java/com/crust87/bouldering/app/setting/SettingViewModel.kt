package com.crust87.bouldering.app.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    val progressVisibility = MutableLiveData<Int>()

    private val _eventChannel = MutableSharedFlow<SettingViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<SettingViewModelEvent> = _eventChannel

    fun navigateUp() {
        _eventChannel.tryEmit(NavigateUpEvent)
    }

    fun openOpenSourceLicense() {
        _eventChannel.tryEmit(OpenOpensourceLicenseEvent)
    }
}