/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crust87.bouldering.editor

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * Does a whole lot of gesture detecting.
 */
internal class CustomGestureDetector(context: Context, private val mListener: OnGestureListener) {

    private var activePointerId = INVALID_POINTER_ID
    private var activePointerIndex = 0
    private var velocityTracker: VelocityTracker? = null
    private var isDragging = false
    private var lastTouchX = 0.toFloat()
    private var lastTouchY = 0.toFloat()
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private val minimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity.toFloat()

    private val scaleListener = object : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor

            if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor))
                return false

            mListener.onScale(scaleFactor,
                    detector.focusX, detector.focusY)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // NO-OP
        }
    }

    private val detector: ScaleGestureDetector = ScaleGestureDetector(context, scaleListener)

    private fun getActiveX(ev: MotionEvent): Float {
        try {
            return ev.getX(activePointerIndex)
        } catch (e: Exception) {
            return ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float {
        try {
            return ev.getY(activePointerIndex)
        } catch (e: Exception) {
            return ev.y
        }
    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            detector.onTouchEvent(ev)
            return processTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is called
            return true
        }
    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = ev.getPointerId(0)

                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(ev)

                lastTouchX = getActiveX(ev)
                lastTouchY = getActiveY(ev)
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - lastTouchX
                val dy = y - lastTouchY

                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= touchSlop
                }

                if (isDragging) {
                    mListener.onDrag(dx, dy)
                    lastTouchX = x
                    lastTouchY = y

                    velocityTracker?.addMovement(ev)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = INVALID_POINTER_ID
                // Recycle Velocity Tracker
                velocityTracker?.recycle()
                velocityTracker = null
            }
            MotionEvent.ACTION_UP -> {
                activePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    velocityTracker?.let {
                        lastTouchX = getActiveX(ev)
                        lastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        it.addMovement(ev)
                        it.computeCurrentVelocity(1000)

                        val vX = it.xVelocity
                        val vY = it.yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= minimumVelocity) {
                            mListener.onFling(lastTouchX, lastTouchY, -vX, -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                velocityTracker?.recycle()
                velocityTracker = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    activePointerId = ev.getPointerId(newPointerIndex)
                    lastTouchX = ev.getX(newPointerIndex)
                    lastTouchY = ev.getY(newPointerIndex)
                }
            }
        }

        activePointerIndex = ev.findPointerIndex(when {
            activePointerId != INVALID_POINTER_ID -> activePointerId
            else -> 0
        })

        return true
    }

    companion object {
        const val INVALID_POINTER_ID = -1
    }
}
