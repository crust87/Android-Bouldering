package com.crust87.bouldering.app.viewer.domain

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class OpenKeyboardUseCase @Inject constructor(@ActivityContext val context: Context) {

    operator fun invoke(view: View) {
        if (!view.isFocusableInTouchMode) {
            view.isFocusableInTouchMode = true
            if (view.requestFocus()) {
                val imm = ContextCompat.getSystemService(
                    context,
                    InputMethodManager::class.java
                )
                imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
}