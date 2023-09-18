package com.kayadami.bouldering.app.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kayadami.bouldering.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    val progressVisibility = MutableLiveData<Int>()
}