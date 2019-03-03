package com.kayadami.bouldering.app.main

import android.app.Activity
import com.google.android.material.appbar.AppBarLayout

class AppBarManager(var activity: Activity?) : AppBarLayout.OnOffsetChangedListener {

    var appBarExpanded = false
    var offsetBound = 0f

    override fun onOffsetChanged(appBar: AppBarLayout?, verticalOffset: Int) {
        val expanded = Math.abs(verticalOffset) < offsetBound

        if (appBarExpanded != expanded) {
            appBarExpanded = expanded

            activity?.invalidateOptionsMenu()
        }
    }
}