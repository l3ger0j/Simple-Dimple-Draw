package org.l3ger0j.simpledimpledraw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ShapeManager extends View {

    Point[] points = new Point[4];
    int groupId = -1;
    private final ArrayList<RoundBall> cornballs = new ArrayList<>();

    public final int mStrokeColor = Color.BLACK;
    public final int mFillColor = Color.TRANSPARENT;
    public final Rect mCropRect = new Rect();

    int selectShape;
    int radiusRect;
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;

    public ShapeManager(Context context) {
        this(context, null);
    }

    public ShapeManager(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ShapeManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        setFocusable(true);
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
        for (int i = 0; i < points.length; i++) {
            cornballs.add(new RoundBall(getContext(), R.drawable.gray_circle, points[i], i));
        }
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {

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
        paint.setStrokeWidth(10);

        mCropRect.left = left + cornballs.get(0).getWidthOfBall() / 2;
        mCropRect.top = top + cornballs.get(0).getWidthOfBall() / 2;
        mCropRect.right = right + cornballs.get(2).getWidthOfBall() / 2;
        mCropRect.bottom = bottom + cornballs.get(3).getWidthOfBall() / 2;

        if (selectShape == 1) {
            radiusRect = Math.min(mCropRect.width(),mCropRect.height()) / 2;
            canvas.drawCircle(mCropRect.centerX(), mCropRect.centerY(),radiusRect, paint);
        } else if (selectShape == 2) {
            canvas.drawRect(mCropRect, paint);
        } else if (selectShape == 3) {
            @SuppressLint("DrawAllocation") RectF ovalRectF = new RectF(mCropRect);
            canvas.drawOval(ovalRectF, paint);
        }

        //fill the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mFillColor);
        paint.setStrokeWidth(0);
        canvas.drawRect(mCropRect, paint);

        // draw the balls on the canvas
        paint.setColor(Color.RED);
        paint.setTextSize(35);
        paint.setStrokeWidth(0);
        for (int i = 0; i < cornballs.size(); i ++) {
            RoundBall ball = cornballs.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);

            canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
        }
    }

    // events when touching the screen
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int eventAction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventAction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                if (points[0] == null) {
                    initRectangle(X, Y);
                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = cornballs.size()-1; i>=0; i--) {
                        RoundBall ball = cornballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt(((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y));

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

            case MotionEvent.ACTION_MOVE: // touch drag with the ball

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

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping
                break;
        }
        // redraw the canvas
        invalidate();
        return true;
    }
}

class RoundBall {
    Bitmap bitmap;
    Context mContext;
    Point point;
    int id;

    public RoundBall(@NonNull Context context, int resourceId, Point point, int id) {
        this.id = id;
        bitmap = BitmapFactory.decodeResource(context.getResources(),
                resourceId);
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