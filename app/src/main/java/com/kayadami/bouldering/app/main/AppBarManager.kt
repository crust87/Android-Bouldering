package com.kayadami.bouldering.app.main

import android.app.Activity
import com.google.android.material.appbar.AppBarLayout
import com.kayadami.bouldering.R
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppBarManager @Inject constructor(val activity: Activity) :
    AppBarLayout.OnOffsetChangedListener {

    private val offsetBound = activity.resources.getDimension(R.dimen.header_height) / 2

    var appBarExpanded = false

    override fun onOffsetChanged(appBar: AppBarLayout?, verticalOffset: Int) {
        val expanded = Math.abs(verticalOffset) < offsetBound

        if (appBarExpanded != expanded) {
            appBarExpanded = expanded

            activity.invalidateOptionsMenu()
        }
    }
}