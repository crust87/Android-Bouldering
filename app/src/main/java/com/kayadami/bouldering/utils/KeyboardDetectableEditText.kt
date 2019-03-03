package com.kayadami.bouldering.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View

class KeyboardDetectableEditText : AppCompatEditText {

    var listener: EditTextListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            listener?.onKeyboardHide(this, this.text.toString())
        }

        return super.dispatchKeyEvent(event)
    }

    fun setListener(action: (v:View, text:String) -> Unit) {
        listener = object: EditTextListener {
            override fun onKeyboardHide(v: View, text: String) {
                action(v, text)
            }
        }
    }

    interface EditTextListener {
        fun onKeyboardHide(v: View, text: String)
    }
}
