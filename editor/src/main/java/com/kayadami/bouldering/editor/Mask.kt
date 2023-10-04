package com.kayadami.bouldering.editor

import android.graphics.*

class Mask (private var options: Options) {

    private var backgroundPaint = Paint()
    private var maskPaint = Paint()
    private var bitmapPaint = Paint()

    private var bitmap: Bitmap = Bitmap.createBitmap(options.bound.width(), options.bound.height(), Bitmap.Config.ARGB_8888)
    private var canvas: Canvas = Canvas(bitmap)

    init {
        backgroundPaint.color = Color.BLACK
        backgroundPaint.isAntiAlias = true

        maskPaint.color = Color.WHITE
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        bitmapPaint.isFilterBitmap = true
        bitmapPaint.alpha = 128

        options.addOptionObserver {
            bitmap = Bitmap.createBitmap(options.bound.width(), options.bound.height(), Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap)
        }
    }

    fun <T : Holder> draw(viewCanvas: Canvas, holderBoxList: List<T>) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)

        for (h in holderBoxList) {
            canvas.drawCircle(h.x - options.bound.left, h.y - options.bound.top, h.radius, maskPaint)
        }

        viewCanvas.drawBitmap(bitmap, null, options.bound, bitmapPaint)
    }
}