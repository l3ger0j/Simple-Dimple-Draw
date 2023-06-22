package org.l3ger0j.simpledimpledraw.utils

import android.graphics.Color
import android.graphics.Paint

class DrawPaint : Paint() {
    init {
        color = Color.BLACK
        strokeWidth = 30f
        strokeJoin = Join.ROUND
        strokeCap = Cap.ROUND
        style = Style.STROKE
    }
}