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
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/*
    // Stores Points to draw circles each time user touches
    private List<Point> circlePoints;
    circlePoints = new ArrayList<>();
    for (Point p : circlePoints) { canvas.drawCircle(p.x, p.y, 5, drawPaint); }

    canvas.drawPath(path, drawPaint);

    // Append new circle each time user presses on screen
    float touchX = event.getX();
    float touchY = event.getY();

    circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
 */

public class SimpleDimpleDrawingView extends View {

    // Initial Color
    public int paintColor = Color.BLACK;
    // Define Paint and Canvas
    public DrawPaint drawPaint = new DrawPaint();

    Canvas drawCanvas = new Canvas();
    static Bitmap myCanvasBitmap;
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

        // TODO Rewrite this corner
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                specialPath.moveTo(pointX , pointY);
                clearPath.moveTo(pointX, pointY);
                if (active == 1) {
                    switch (id) {
                        case 1:
                            drawCanvas.drawCircle(pointX , pointY , drawRadius , drawPaint);
                            break;
                        case 2:
                            drawCanvas.drawRect(pointX , pointX , pointY , pointY , drawPaint);
                            break;
                        case 3:
                            float piX = pointX + 10;
                            float piY = pointY + 10;
                            RectF rectF = new RectF(pointX , piY , pointX , piX);
                            drawCanvas.drawOval(rectF , drawPaint);
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
            default:
                return false;
        }
        // indicate view should be redrawn
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

    // Setup Paint with color and stroke style
    private void setupPaint() {
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(stroke);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

}
