package com.crust87.bouldering.editor.canvas

import android.graphics.*
import com.crust87.bouldering.editor.EditorConfigurations
import com.crust87.bouldering.editor.Options
import com.crust87.bouldering.editor.data.Holder

internal class HolderBox constructor(private var options: Options) : Holder(), Comparable<HolderBox> {

    enum class Action {
        RESIZE, MOVE, NONE
    }

    var isSelected: Boolean = false

    private val paint: Paint = with(Paint()) {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = (EditorConfigurations.DEFAULT_LINE_WIDTH / 2)

        this
    }
    private val textPaint: Paint = with(Paint()) {
        color = Color.WHITE
        isAntiAlias = true
        textSize = EditorConfigurations.DEFAULT_TEXT_SIZE
        textAlign = Paint.Align.CENTER

        this
    }
    private val anchor: Anchor = Anchor(options)

    private var lastPosition: PointF = PointF(0f, 0f)
    private var anchorPosition: PointF = PointF(Math.cos(45 * Math.PI / 180).toFloat(), Math.sin(45 * Math.PI / 180).toFloat())
    private var action: Action = Action.NONE

    init {
        x = (options.bound.width() / 2).toFloat()
        y = (options.bound.height() / 2).toFloat()
        radius = EditorConfigurations.DEFAULT_BOX_SIZE

        setAnchorPosition()
    }

    fun contains(x: Float, y: Float): Boolean {
        return x >= this.x - radius && x <= this.x + radius && y >= this.y - radius && y <= this.y + radius
    }

    fun draw(canvas: Canvas) {
        paint.color = options.color
        textPaint.color = options.color

        if (isSelected) {
            paint.strokeWidth = EditorConfigurations.DEFAULT_LINE_WIDTH / options.scale
        } else {
            paint.strokeWidth = EditorConfigurations.DEFAULT_LINE_WIDTH / 2 / options.scale
        }

        canvas.drawCircle(x, y, radius, paint)

        if (isSpecial) {
            canvas.drawCircle(x, y, radius - EditorConfigurations.SPECIAL_LINE_GAP, paint)
        } else {
            if (isInOrder) {
                canvas.drawText(index.toString(), x, y - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint)
            }
        }

        if (isSelected) {
            anchor.draw(canvas)
        }
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y

        optimizePosition()
        setAnchorPosition()
    }

    private fun optimizePosition() {
        val top = y - radius
        if (top < options.bound.top) {
            y = options.bound.top + radius
        }

        val bottom = y + radius
        if (bottom > options.bound.bottom) {
            y = options.bound.bottom - radius
        }

        val left = x - radius
        if (left < options.bound.left) {
            x = options.bound.left + radius
        }

        val right = x + radius
        if (right > options.bound.right) {
            x = options.bound.right - radius
        }
    }

    private fun setAnchorPosition() {
        anchor.setPosition(x + anchorPosition.x * radius, y - anchorPosition.y * radius)
    }

    fun startTransforming(x: Float, y: Float) {
        lastPosition.x = x
        lastPosition.y = y

        action = if (anchor.contains(x, y)) {
            Action.RESIZE
        } else {
            Action.MOVE
        }
    }

    fun keepTransforming(x: Float, y: Float) {
        val dx = lastPosition.x - x
        val dy = lastPosition.y - y

        var actionResult = false
        if (action == Action.MOVE) {
            actionResult = move(dx, dy)
        }
        if (action == Action.RESIZE) {
            actionResult = scale(dx)
        }

        if (actionResult) {
            setAnchorPosition()
        }

        lastPosition.x = x
        lastPosition.y = y
    }

    fun finishTransforming() {
        lastPosition.x = 0f
        lastPosition.y = 0f

        action = Action.NONE
    }

    private fun move(x: Float, y: Float): Boolean {
        this.x -= x
        this.y -= y

        optimizePosition()

        return true
    }

    // FIXME this method needs to change
    private fun scale(d: Float): Boolean {
        val lRadius = radius - d

        if (lRadius > EditorConfigurations.MIN_BOX_SIZE) {
            val lLeft = x - lRadius > options.bound.left
            val lTop = y - lRadius > options.bound.top
            val lRight = x + lRadius < options.bound.right
            val lBottom = y + lRadius < options.bound.bottom

            val lLeftNot = x + d - lRadius > options.bound.left
            val lTopNot = y + d - lRadius > options.bound.top
            val lRightNot = x - d + lRadius < options.bound.right
            val lBottomNot = y - d + lRadius < options.bound.bottom

            if (lLeft && lTop && lRight && lBottom) {
                radius = lRadius
            } else if (!lLeft && lTop && lRight && lBottom) {
                // Left
                if (lRightNot) {
                    radius = lRadius
                    x -= d
                }
            } else if (!lLeft && !lTop && lRight && lBottom) {
                // Left & Top
                if (lRightNot && lBottomNot) {
                    radius = lRadius
                    x -= d
                    y -= d
                }
            } else if (lLeft && !lTop && lRight && lBottom) {
                // Top
                if (lBottomNot) {
                    radius = lRadius
                    y -= d
                }
            } else if (lLeft && !lTop && !lRight && lBottom) {
                // Top & Right
                if (lBottomNot && lLeftNot) {
                    radius = lRadius
                    x += d
                    y -= d
                }
            } else if (lLeft && lTop && !lRight && lBottom) {
                // Right
                if (lLeftNot) {
                    radius = lRadius
                    x += d
                }
            } else if (lLeft && lTop && !lRight && !lBottom) {
                // Right & Bottom
                if (lLeftNot && lTopNot) {
                    radius = lRadius
                    x += d
                    y += d
                }
            } else if (lLeft && lTop && lRight && !lBottom) {
                // Bottom
                if (lTopNot) {
                    radius = lRadius
                    y += d
                }
            } else if (!lLeft && lTop && lRight && !lBottom) {
                // Left & Bottom
                if (lRightNot && lTopNot) {
                    radius = lRadius
                    x -= d
                    y += d
                }
            }
        } else {
            radius = EditorConfigurations.MIN_BOX_SIZE
        }

        return true
    }

    fun convert(scale: Float): Holder {
        return Holder(x / scale, y / scale, radius / scale, isSpecial, isInOrder, index)
    }

    override fun compareTo(other: HolderBox): Int {
        return index - other.index
    }
}
