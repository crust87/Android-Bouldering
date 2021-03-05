package com.kayadami.bouldering.app.setting

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.utils.PermissionChecker2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SettingViewModel @ViewModelInject constructor(
        val repository: BoulderingDataSource,
        val permissionChecker: PermissionChecker2
) : ViewModel() {

    val progressVisibility = MutableLiveData<Int>()

    val toastEvent = SingleLiveEvent<String>()

    val requestExportPermissionEvent = SingleLiveEvent<Unit>()

    val requestImportPermissionEvent = SingleLiveEvent<Unit>()

    fun exportAll() = viewModelScope.launch(Dispatchers.Main) {
        if (permissionChecker.checkReadWrite()) {
            progressVisibility.value = View.VISIBLE

            val isSuccess = try {
                withContext(Dispatchers.IO) { repository.exportAll() }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

                false
            }

            progressVisibility.value = View.GONE

            toastEvent.value = if (isSuccess) {
                "SUCCESS!"
            } else {
                "FAIL!"
            }
        } else {
            requestExportPermissionEvent.call()
        }
    }

    fun importAll() = viewModelScope.launch(Dispatchers.Main) {
        if (permissionChecker.checkReadWrite()) {
            progressVisibility.value = View.VISIBLE

            val isSuccess = try {
                withContext(Dispatchers.IO) { repository.importAll() }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

                false
            }

            progressVisibility.value = View.GONE

            toastEvent.value = if (isSuccess) {
                "SUCCESS!"
            } else {
                "FAIL!"
            }
        } else {
            requestImportPermissionEvent.call()
        }
    }
}