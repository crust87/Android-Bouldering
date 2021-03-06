package com.kayadami.bouldering.editor

import android.graphics.Color
import android.graphics.Rect

class Options {
    companion object {
        const val MAX_SCALE = 3.0f
        const val MIN_SCALE = 1.0f
    }

    val observers: ArrayList<OptionObserver> = ArrayList()
    var scale: Float = 0f
    var color: Int = Color.WHITE
    var bound: Rect = Rect(0, 0, 1, 1)
    set(value) {
        field = value
        notifyBoundChange()
    }

    private fun notifyBoundChange() {
        for (o in observers) {
            o.onBoundChanged()
        }
    }

    fun addOptionObserver(o: OptionObserver) {
        observers.add(o)
    }

    fun addOptionObserver(action: () -> Unit) {
        observers.add(object: OptionObserver {
            override fun onBoundChanged() {
                action()
            }
        })
    }

    interface OptionObserver {
        fun onBoundChanged()
    }
}