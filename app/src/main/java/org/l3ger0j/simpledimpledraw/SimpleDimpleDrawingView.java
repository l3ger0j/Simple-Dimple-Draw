package org.l3ger0j.simpledimpledraw;

import static android.graphics.Bitmap.Config;
import static android.graphics.Bitmap.createBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class SimpleDimpleDrawingView extends View {

    int id = 0;
    int active = 0;

    private DrawPaint drawPaint;
    static Bitmap myCanvasBitmap;
    public Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    public SpecialPath specialPath;
    public SpecialPath clearPath;

    public static Bitmap getCanvasBitmap(){
        return myCanvasBitmap;
    }

    public void setDrawPaint(DrawPaint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public void eraseCanvas (@NonNull PorterDuffXfermode porterDuffXfermode, View v) {
        if (drawPaint.getXfermode() == null) {
            drawPaint.setXfermode(porterDuffXfermode);
            specialPath.reset();
            clearPath.reset();
            id = 1;
            v.setRotation(180);
        } else {
            drawPaint.setXfermode(null);
            specialPath.reset();
            clearPath.reset();
            id = 0;
            v.setRotation(0);
        }
    }

    public void clearCanvas () {
        drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
        specialPath.reset();
        clearPath.reset();
        invalidate();
    }

    public SimpleDimpleDrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);

        drawPaint = new DrawPaint();
        drawCanvas = new Canvas();
        specialPath = new SpecialPath();
        clearPath = new SpecialPath();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCanvas.drawPath(specialPath, drawPaint);
        if (id == 1) {
            drawCanvas.drawPath(clearPath, drawPaint);
        }
        canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super .onTouchEvent(event);

        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                specialPath.moveTo(pointX , pointY);
                clearPath.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (active == 0) {
                    switch (id) {
                        case 0:
                            specialPath.lineTo(pointX , pointY);
                            break;
                        case 1:
                            clearPath.lineTo(pointX , pointY);
                            break;
                    }
                } else if (id == 0) {
                    specialPath.lineTo(pointX , pointY);
                }
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        myCanvasBitmap = createBitmap(w, h, Config.ARGB_8888);
        drawCanvas.setBitmap(myCanvasBitmap);

        setMeasuredDimension(w, h);
    }
}