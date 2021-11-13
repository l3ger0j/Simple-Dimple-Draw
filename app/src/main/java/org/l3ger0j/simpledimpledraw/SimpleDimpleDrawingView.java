package org.l3ger0j.simpledimpledraw;

import static android.graphics.Bitmap.Config;
import static android.graphics.Bitmap.createBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class SimpleDimpleDrawingView extends View {

    public int paintColor = Color.BLACK;
    public DrawPaint drawPaint;
    static Bitmap myCanvasBitmap;
    public Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    public SpecialPath specialPath;
    public SpecialPath clearPath;

    int stroke = 30;
    int id = 0;
    int active = 0;
    float drawRadius = 100;

    public SimpleDimpleDrawingView(Context context , AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
        setupPaint();
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
        if (id == 4) {
            drawCanvas.drawPath(clearPath, drawPaint);
        }
        canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);
        setupPaint();
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
                if (active == 1) {
                    switch (id) {
                        case 1:
                            specialPath.addCircle(pointX , pointY , drawRadius , Path.Direction.CW);
                            break;
                        case 2:
                        case 3:
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (active == 0) {
                    switch (id) {
                        case 0:
                            specialPath.lineTo(pointX , pointY);
                            break;
                        case 1:
                            specialPath.addCircle(pointX , pointY , drawRadius , Path.Direction.CW);
                            break;
                        case 2:
                        case 3:
                        case 4:
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

    public static Bitmap getCanvasBitmap(){

        return myCanvasBitmap;
    }

    private void setupPaint() {
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(stroke);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}