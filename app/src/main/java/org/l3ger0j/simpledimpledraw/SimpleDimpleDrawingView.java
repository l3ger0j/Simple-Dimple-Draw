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
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SimpleDimpleDrawingView extends View {

    public int paintColor = Color.BLACK;
    public DrawPaint drawPaint = new DrawPaint();
    static Bitmap myCanvasBitmap;
    Canvas drawCanvas = new Canvas();
    Matrix identityMatrix = new Matrix();
    SpecialPath specialPath = new SpecialPath();
    SpecialPath clearPath = new SpecialPath();

    int stroke = 30;
    int id = 0;
    int active = 0;
    float drawRadius = 100;

    public SimpleDimpleDrawingView(Context context , AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCanvas.drawPath(specialPath, drawPaint);
        drawCanvas.drawPath(clearPath, drawPaint);
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
                            specialPath.addRect(pointX , pointX , pointY , pointY , Path.Direction.CW);
                            break;
                        case 3:
                            float piX = pointX + 100;
                            float piY = pointY + 100;
                            RectF rectF = new RectF(pointX , piY , piX , piY);
                            specialPath.addOval(rectF , Path.Direction.CW);
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
                            drawCanvas.drawCircle(pointX , pointY , drawRadius , drawPaint);
                            break;
                        case 2:
                            drawCanvas.drawRect(pointX , pointX , pointY , pointY , drawPaint);
                            break;
                        case 3:
                            float piX = pointX + 30;
                            float piY = pointY + 30;
                            RectF rectF = new RectF(pointY , piX , pointX , piY);
                            drawCanvas.drawOval(rectF, drawPaint);
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
