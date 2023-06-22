package org.l3ger0j.simpledimpledraw.utils;

import android.graphics.Color;
import android.graphics.Paint;

public class DrawPaint extends Paint {

    public DrawPaint () {
        setColor(Color.BLACK);
        setStrokeWidth(30);
        setStrokeJoin(Paint.Join.ROUND);
        setStrokeCap(Paint.Cap.ROUND);
        setStyle(Style.STROKE);
    }
}