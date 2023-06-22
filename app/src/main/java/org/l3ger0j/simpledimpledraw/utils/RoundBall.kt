package org.l3ger0j.simpledimpledraw.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.*

class RoundBall(context: Context, resourceId: Int, point: Point, val iD: Int) {
    val bitmap: Bitmap
    val drawable: Drawable?
    private val canvas: Canvas
    private val mContext: Context
    private val point: Point

    init {
        drawable = ContextCompat.getDrawable(context, resourceId)
        bitmap = Bitmap.createBitmap(Objects.requireNonNull(drawable)!!.intrinsicWidth, drawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        mContext = context
        this.point = point
    }

    val widthOfBall: Int
        get() = bitmap.width
    val heightOfBall: Int
        get() = bitmap.height
    var x: Int
        get() = point.x
        set(x) {
            point.x = x
        }
    var y: Int
        get() = point.y
        set(y) {
            point.y = y
        }
}