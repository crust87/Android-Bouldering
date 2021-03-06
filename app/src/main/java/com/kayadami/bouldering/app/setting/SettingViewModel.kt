package com.kayadami.bouldering.app.setting

import android.app.Application
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import com.kayadami.bouldering.data.BoulderingDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(var context: Application, var repository: BoulderingDataSource) : AndroidViewModel(context) {

    val isProgress: ObservableBoolean = ObservableBoolean(false)

    fun exportAll() = CoroutineScope(Dispatchers.Main).launch {
        isProgress.set(true)

        val isSuccess = withContext(Dispatchers.Default) { repository.exportAll() }

        isProgress.set(false)

        if (isSuccess) {
            Toast.makeText(context, "SUCCESS!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "FAIL!", Toast.LENGTH_SHORT).show()
        }
    }

    fun importAll() = CoroutineScope(Dispatchers.Main).launch {
        isProgress.set(true)

        val isSuccess = withContext(Dispatchers.Default) { repository.importAll() }

        isProgress.set(false)

        if (isSuccess) {
            Toast.makeText(context, "SUCCESS!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "FAIL!", Toast.LENGTH_SHORT).show()
        }
    }
}