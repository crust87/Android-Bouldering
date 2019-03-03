package com.kayadami.bouldering.app

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.kayadami.bouldering.R

fun Fragment.navigate(@IdRes id: Int) {
    activity?.let {
        Navigation.findNavController(it, R.id.nav_host_fragment).navigate(id)
    }
}

fun Fragment.navigate(@IdRes id: Int, @Nullable bundle: Bundle) {
    activity?.let {
        Navigation.findNavController(it, R.id.nav_host_fragment).navigate(id, bundle)
    }
}

fun Fragment.navigate(@NonNull directions: NavDirections) {
    activity?.let {
        Navigation.findNavController(it, R.id.nav_host_fragment).navigate(directions)
    }
}

fun Fragment.navigateUp() {
    activity?.let {
        Navigation.findNavController(it, R.id.nav_host_fragment).navigateUp()
    }
}

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    val actionBarActivity = activity
    if (actionBarActivity is AppCompatActivity) {
        actionBarActivity.setSupportActionBar(toolbar)
    }
}

val Fragment.supportActionBar: ActionBar?
    get() {
        val actionBarActivity = activity
        if (actionBarActivity is AppCompatActivity) {
            return actionBarActivity.supportActionBar
        }

        return null
    }