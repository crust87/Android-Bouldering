package com.kayadami.bouldering.app.bindings

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter


object ToolBarBindings {

    @BindingAdapter("onNavigationClick")
    @JvmStatic
    fun setBouldering(view: Toolbar, listener: View.OnClickListener?) {
        view.setNavigationOnClickListener(listener)
    }


    @BindingAdapter("title")
    @JvmStatic
    fun setTitle(view: Toolbar, @StringRes res: Int) {
        view.setTitle(res)
    }

    @BindingAdapter("navigationIcon")
    @JvmStatic
    fun setNavigationIcon(view: Toolbar, @DrawableRes res: Int) {
        view.setNavigationIcon(res)
    }


}