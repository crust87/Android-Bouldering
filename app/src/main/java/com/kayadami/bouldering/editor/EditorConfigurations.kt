package com.kayadami.bouldering.editor

import android.content.Context

import com.kayadami.bouldering.R

object EditorConfigurations {

    // Constants
    var DEFAULT_ANCHOR_SIZE = 0f
    var DEFAULT_BOX_SIZE = 0f
    var DEFAULT_LINE_WIDTH = 0f
    var MIN_BOX_SIZE = 0f
    var SPECIAL_LINE_GAP = 0f
    var DEFAULT_TEXT_SIZE = 0f

    fun init(context: Context) {
        DEFAULT_ANCHOR_SIZE = context.resources.getDimensionPixelSize(R.dimen.default_anchor_size).toFloat()
        DEFAULT_BOX_SIZE = context.resources.getDimensionPixelSize(R.dimen.default_box_size).toFloat()
        DEFAULT_LINE_WIDTH = context.resources.getDimensionPixelSize(R.dimen.default_line_width).toFloat()
        MIN_BOX_SIZE = context.resources.getDimensionPixelSize(R.dimen.min_box_Size).toFloat()
        SPECIAL_LINE_GAP = context.resources.getDimensionPixelSize(R.dimen.special_line_gap).toFloat()
        DEFAULT_TEXT_SIZE = context.resources.getDimensionPixelSize(R.dimen.default_text_size).toFloat()
    }
}
