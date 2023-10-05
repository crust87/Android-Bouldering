package com.crust87.bouldering.editor.canvas

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import com.crust87.bouldering.editor.EditorConfigurations
import com.crust87.bouldering.editor.Options

internal class Anchor(var options: Options) {

    private val mPaint: Paint = Paint()
    private var point: PointF = PointF(0f, 0f)
    private val radius: Float
    private val mTouchArea: Float

    init {
        mPaint.isAntiAlias = true
        radius = EditorConfigurations.DEFAULT_ANCHOR_SIZE
        mTouchArea = radius * 2
    }

    fun draw(pCanvas: Canvas) {
        mPaint.color = options.color
        pCanvas.drawCircle(point.x, point.y, radius / options.scale, mPaint)
    }

    fun setPosition(x: Float, y: Float) {
        point.x = x
        point.y = y
    }

    fun contains(x: Float, y: Float): Boolean {
        val touchArea = mTouchArea / options.scale
        return x >= point.x - touchArea && x <= point.x + touchArea && y >= point.y - touchArea && y <= point.y + touchArea
    }
}
