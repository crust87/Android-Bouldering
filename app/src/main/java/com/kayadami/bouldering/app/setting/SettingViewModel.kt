package com.kayadami.bouldering.app.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kayadami.bouldering.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    val progressVisibility = MutableLiveData<Int>()

    val eventChannel = MutableSharedFlow<SettingViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    fun navigateUp() {
        eventChannel.tryEmit(NavigateUpEvent)
    }

    fun openOpenSourceLicense() {
        eventChannel.tryEmit(OpenOpensourceLicenseEvent)
    }
}