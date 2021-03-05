package com.kayadami.bouldering.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(position: Int)
    abstract fun recycle()
}