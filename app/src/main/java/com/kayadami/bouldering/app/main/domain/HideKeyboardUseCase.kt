package com.kayadami.bouldering.app.main.domain

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class HideKeyboardUseCase @Inject constructor(@ActivityContext val context: Context) {

    operator fun invoke(view: View) {
        (context as Activity).currentFocus?.let { view ->
            val imm = ContextCompat.getSystemService(
                context,
                InputMethodManager::class.java
            )
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        view.isFocusable = false
    }
}