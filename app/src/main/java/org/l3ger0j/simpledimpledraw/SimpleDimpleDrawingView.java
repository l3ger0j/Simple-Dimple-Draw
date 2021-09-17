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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SimpleDimpleDrawingView extends View {

    public int paintColor = Color.BLACK;
    public DrawPaint drawPaint;
    static Bitmap myCanvasBitmap;
    Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    SpecialPath specialPath;
    SpecialPath clearPath;

    int stroke = 30;
    int id = 0;
    int active = 0;
    float drawRadius = 100;

    Point[] points = new Point[4];
    int groupId = -1;
    private final ArrayList<ColorBall> cornballs = new ArrayList<>();

    private final int mStrokeColor = paintColor;
    private final int mFillColor = Color.parseColor("#55DB1255");
    private final Rect mCropRect = new Rect();

    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;

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

        // Modules ShapeBuilder
        paint = new Paint();
    }

    private void initRectangle(int X, int Y) {
        //initialize rectangle.
        points[0] = new Point();
        points[0].x = X;
        points[0].y = Y;

        points[1] = new Point();
        points[1].x = X;
        points[1].y = Y + 30;

        points[2] = new Point();
        points[2].x = X + 30;
        points[2].y = Y + 30;

        points[3] = new Point();
        points[3].x = X +30;
        points[3].y = Y;

        balID = 2;
        groupId = 1;
        // declare each ball with the ColorBall class
        for (int i = 0; i < points.length; i++) {
            cornballs.add(new ColorBall(getContext(), points[i], i));
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCanvas.drawPath(specialPath, drawPaint);
        if (id == 4) {
            drawCanvas.drawPath(clearPath, drawPaint);
        } else if (id == 2) {
            if(points[3]==null) {
                //point4 null when view first create
                initRectangle(getWidth() / 2, getHeight() / 2);
            }

            int left, top, right, bottom;
            left = points[0].x;
            top = points[0].y;
            right = points[0].x;
            bottom = points[0].y;
            for (int i = 1; i < points.length; i++) {
                left = Math.min(left , points[i].x);
                top = Math.min(top , points[i].y);
                right = Math.max(right , points[i].x);
                bottom = Math.max(bottom , points[i].y);
            }
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(5);

            //draw stroke
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mStrokeColor);
            paint.setStrokeWidth(5);

            mCropRect.left = left + cornballs.get(0).getWidthOfBall() / 2;
            mCropRect.top = top + cornballs.get(0).getWidthOfBall() / 2;
            mCropRect.right = right + cornballs.get(2).getWidthOfBall() / 2;
            mCropRect.bottom = bottom + cornballs.get(3).getWidthOfBall() / 2;
            canvas.drawRect(mCropRect, paint);

            //fill the rectangle
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mFillColor);
            paint.setStrokeWidth(0);
            canvas.drawRect(mCropRect, paint);

            // draw the balls on the canvas
            paint.setColor(Color.RED);
            paint.setTextSize(18);
            paint.setStrokeWidth(0);
            for (int i = 0; i < cornballs.size(); i ++) {
                ColorBall ball = cornballs.get(i);
                canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                        paint);

                canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
            }
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

        int X = (int) event.getX();
        int Y = (int) event.getY();

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
                            if (points[0] == null) {
                                initRectangle(X, Y);
                            } else {
                                //resize rectangle
                                balID = -1;
                                groupId = -1;
                                for (int i = cornballs.size()-1; i>=0; i--) {
                                    ColorBall ball = cornballs.get(i);
                                    // check if inside the bounds of the ball (circle)
                                    // get the center for the ball
                                    int centerX = ball.getX() + ball.getWidthOfBall();
                                    int centerY = ball.getY() + ball.getHeightOfBall();
                                    paint.setColor(Color.CYAN);
                                    // calculate the radius from the touch to the center of the
                                    // ball
                                    double radCircle = Math
                                            .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                                    * (centerY - Y)));

                                    if (radCircle < ball.getWidthOfBall()) {

                                        balID = ball.getID();
                                        if (balID == 1 || balID == 3) {
                                            groupId = 2;
                                        } else {
                                            groupId = 1;
                                        }
                                        invalidate();
                                        break;
                                    }
                                    invalidate();
                                }
                            }
                            break;
                        case 3:
                            float piX = pointX + 100;
                            float piY = pointY + 100;
                            RectF rectF = new RectF(pointX , piY , piX , piY);
                            specialPath.addOval(rectF , Path.Direction.CW);
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
                            if (balID > -1) {
                                // move the balls the same as the finger
                                cornballs.get(balID).setX(X);
                                cornballs.get(balID).setY(Y);
                                paint.setColor(Color.CYAN);
                                if (groupId == 1) {
                                    cornballs.get(1).setX(cornballs.get(0).getX());
                                    cornballs.get(1).setY(cornballs.get(2).getY());
                                    cornballs.get(3).setX(cornballs.get(2).getX());
                                    cornballs.get(3).setY(cornballs.get(0).getY());
                                } else {
                                    cornballs.get(0).setX(cornballs.get(1).getX());
                                    cornballs.get(0).setY(cornballs.get(3).getY());
                                    cornballs.get(2).setX(cornballs.get(3).getX());
                                    cornballs.get(2).setY(cornballs.get(1).getY());
                                }
                                invalidate();
                            }
                            break;
                        case 3:
                            float piX = pointX + 30;
                            float piY = pointY + 30;
                            RectF rectF = new RectF(pointY , piX , pointX , piY);
                            specialPath.addOval(rectF, Path.Direction.CW);
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

    public static class ColorBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;

        public ColorBall(@NonNull Context context , Point point , int id) {
            this.id = id;
            bitmap = SimpleDimpleDrawingView.getCanvasBitmap();
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }
    }
}
