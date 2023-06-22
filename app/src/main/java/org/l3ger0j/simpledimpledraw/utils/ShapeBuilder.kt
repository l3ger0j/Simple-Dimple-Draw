package org.l3ger0j.simpledimpledraw.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.l3ger0j.simpledimpledraw.R
import kotlin.math.sqrt

class ShapeBuilder @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val points = arrayOfNulls<Point>(4)
    var groupId = -1
    private val cornballs = ArrayList<RoundBall>()
    @JvmField
    var mStrokeColor = Color.BLACK
    @JvmField
    var mStrokeWidth = 10
    private val mFillColor = Color.TRANSPARENT
    @JvmField
    val mCropRect = Rect()
    @JvmField
    var selectShape = 0
    @JvmField
    var radiusRect = 0

    private var balID = 0

    var paint: Paint? = null

    init {
        init()
    }

    private fun init() {
        paint = Paint()
        isFocusable = true
    }

    private fun initRectangle(X: Int, Y: Int) {
        points[0] = Point()
        points[0]!!.x = X
        points[0]!!.y = Y
        points[1] = Point()
        points[1]!!.x = X
        points[1]!!.y = Y + 30
        points[2] = Point()
        points[2]!!.x = X + 30
        points[2]!!.y = Y + 30
        points[3] = Point()
        points[3]!!.x = X + 30
        points[3]!!.y = Y
        balID = 2
        groupId = 1
        for (i in points.indices) {
            cornballs.add(RoundBall(context, R.drawable.circle, points[i]!!, i))
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (points[3] == null) {
            initRectangle(width / 2, height / 2)
        }
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        left = points[0]!!.x
        top = points[0]!!.y
        right = points[0]!!.x
        bottom = points[0]!!.y
        for (i in 1 until points.size) {
            left = left.coerceAtMost(points[i]!!.x)
            top = top.coerceAtMost(points[i]!!.y)
            right = right.coerceAtLeast(points[i]!!.x)
            bottom = bottom.coerceAtLeast(points[i]!!.y)
        }
        paint!!.isAntiAlias = true
        paint!!.isDither = true
        paint!!.strokeJoin = Paint.Join.ROUND
        paint!!.style = Paint.Style.STROKE
        paint!!.color = mStrokeColor
        paint!!.strokeWidth = mStrokeWidth.toFloat()
        mCropRect.left = left + cornballs[0].widthOfBall / 2
        mCropRect.top = top + cornballs[0].widthOfBall / 2
        mCropRect.right = right + cornballs[2].widthOfBall / 2
        mCropRect.bottom = bottom + cornballs[3].widthOfBall / 2
        when (selectShape) {
            1 -> {
                radiusRect = mCropRect.width().coerceAtMost(mCropRect.height()) / 2
                canvas.drawCircle(mCropRect.centerX().toFloat(), mCropRect.centerY().toFloat(), radiusRect.toFloat(), paint!!)
            }
            2 -> {
                canvas.drawRect(mCropRect, paint!!)
            }
            3 -> {
                @SuppressLint("DrawAllocation")
                val ovalRectF = RectF(mCropRect)
                canvas.drawOval(ovalRectF, paint!!)
            }
        }

        paint!!.style = Paint.Style.FILL
        paint!!.color = mFillColor
        paint!!.strokeWidth = 0f
        canvas.drawRect(mCropRect, paint!!)

        paint!!.color = Color.RED
        paint!!.textSize = 35f
        paint!!.strokeWidth = 0f
        for (i in cornballs.indices) {
            val ball = cornballs[i]
            canvas.drawBitmap(ball.bitmap, ball.x.toFloat(), ball.y.toFloat(),
                    paint)
            canvas.drawText("" + (i + 1), ball.x.toFloat(), ball.y.toFloat(), paint!!)
        }
    }

    // events when touching the screen
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventAction = event.action
        val xPos = event.x.toInt()
        val yPos = event.y.toInt()
        when (eventAction) {
            MotionEvent.ACTION_DOWN ->
                if (points[0] == null) {
                    initRectangle(xPos, yPos)
                } else {
                    balID = -1
                    groupId = -1
                    var i = cornballs.size - 1
                    while (i >= 0) {
                        val ball = cornballs[i]
                        val centerX = ball.x + ball.widthOfBall
                        val centerY = ball.y + ball.heightOfBall
                        paint!!.color = Color.CYAN
                        val radCircle = sqrt(((centerX - xPos)
                                * (centerX - xPos)
                                + (centerY - yPos)
                                * (centerY - yPos)).toDouble())
                        if (radCircle < ball.widthOfBall) {
                            balID = ball.iD
                            groupId = if (balID == 1 || balID == 3) {
                                2
                            } else {
                                1
                            }
                            invalidate()
                            break
                        }
                        invalidate()
                        i--
                    }
                }

            MotionEvent.ACTION_MOVE -> if (balID > -1) {
                // move the balls the same as the finger
                cornballs[balID].x = xPos
                cornballs[balID].y = yPos
                paint!!.color = Color.CYAN
                if (groupId == 1) {
                    cornballs[1].x = cornballs[0].x
                    cornballs[1].y = cornballs[2].y
                    cornballs[3].x = cornballs[2].x
                    cornballs[3].y = cornballs[0].y
                } else {
                    cornballs[0].x = cornballs[1].x
                    cornballs[0].y = cornballs[3].y
                    cornballs[2].x = cornballs[3].x
                    cornballs[2].y = cornballs[1].y
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {}
        }
        // redraw the canvas
        invalidate()
        return true
    }
}