package org.l3ger0j.simpledimpledraw;

import static android.graphics.Bitmap.Config;
import static android.graphics.Bitmap.createBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleDimpleDrawingView extends View {

    private DrawPaint drawPaint;
    private static Bitmap myCanvasBitmap;
    public Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    private SpecialPath specialPath;

    private final ArrayList<SpecialPath> paths = new ArrayList<>();
    private final ArrayList<SpecialPath> undo = new ArrayList<>();

    private final Map<SpecialPath, Integer> colorsMap = new HashMap<>();
    private final Map<SpecialPath, Float> strokeMap = new HashMap<>();
    private final Map<SpecialPath, Xfermode> xfermodeMap = new HashMap<>();

    private int mBackColor = 0;
    private int mColor = Color.BLACK;
    private float mStroke = 30;
    private final Xfermode mXfermode = null;
    private boolean eraserOn = false;

    public static Bitmap getCanvasBitmap(){
        return myCanvasBitmap;
    }

    public int getBackgroundColor() {
        int color = Color.WHITE;
        Drawable background = getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        invalidate();
        return color;
    }

    public boolean isEraserOn() {
        return !eraserOn;
    }

    public void setDrawPaint(DrawPaint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public void setColor (int color) {
        this.mColor = color;
        invalidate();
    }

    public void setStroke (int stroke) {
        this.mStroke = stroke;
        invalidate();
    }

    // FIXME: 01.04.2022
    public void eraseCanvas (boolean isEraserOn) {
        if (isEraserOn) {
            eraserOn = true;
            mBackColor = mColor;
            mColor = getBackgroundColor();
        }
        else {
            eraserOn = false;
            mColor = mBackColor;
        }
        invalidate();
    }

    public void clearCanvas () {
        drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
        paths.clear();
        undo.clear();
        invalidate();
    }

    public void drawShape (int id , @NonNull ShapeManager shapeManager) {
        RectF mRectF = new RectF(shapeManager.mCropRect);
        switch (id) {
            case 0:
                specialPath.addCircle(
                        mRectF.centerX(),
                        mRectF.centerY(),
                        shapeManager.radiusRect,
                        Path.Direction.CW);
                paths.add(specialPath);
                colorsMap.put(specialPath, mColor);
                strokeMap.put(specialPath, mStroke);
                specialPath = new SpecialPath();
                invalidate();
                break;
            case 1:
                specialPath.addRect(
                        mRectF,
                        Path.Direction.CW);
                paths.add(specialPath);
                colorsMap.put(specialPath, mColor);
                strokeMap.put(specialPath, mStroke);
                specialPath = new SpecialPath();
                invalidate();
                break;
            case 2:
                specialPath.addOval(
                        mRectF,
                        Path.Direction.CW);
                paths.add(specialPath);
                colorsMap.put(specialPath, mColor);
                strokeMap.put(specialPath, mStroke);
                specialPath = new SpecialPath();
                invalidate();
                break;
        }
    }

    public void redoPath () {
        if (undo.size()>0) {
            paths.add(undo.remove(undo.size()-1));
        } else {
            Toast.makeText(getContext() , "EMPTY REDO" , Toast.LENGTH_SHORT).show();
        }
    }

    public void undoPath () {
        if (paths.size()>0){
            undo.add(paths.remove(paths.size()-1));
        } else {
            Toast.makeText(getContext() , "EMPTY UNDO" , Toast.LENGTH_SHORT).show();
        }
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

        colorsMap.put(specialPath, mColor);
        strokeMap.put(specialPath, mStroke);
        xfermodeMap.put(specialPath, mXfermode);
        paths.add(specialPath);
    }

    // FIXME: 01.04.2022
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
        drawCanvas.drawPath(specialPath, drawPaint);
        for (SpecialPath p : paths) {
            drawPaint.setColor(colorsMap.get(p));
            drawPaint.setStrokeWidth(strokeMap.get(p));
            drawPaint.setXfermode(xfermodeMap.get(p));
            canvas.drawPath(p, drawPaint);
        }
        drawPaint.setColor(mColor);
        drawPaint.setXfermode(mXfermode);
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
                break;
            case MotionEvent.ACTION_MOVE:
                specialPath.lineTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                specialPath.lineTo(pointX, pointY);
                drawCanvas.drawPath(specialPath, drawPaint);
                paths.add(specialPath);
                colorsMap.put(specialPath, mColor);
                strokeMap.put(specialPath, mStroke);
                xfermodeMap.put(specialPath, mXfermode);
                specialPath = new SpecialPath();
                break;
        }
        postInvalidate();
        return true;
    }

    // FIXME: 01.04.2022
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        myCanvasBitmap = createBitmap(w, h, Config.ARGB_8888);
        drawCanvas.setBitmap(myCanvasBitmap);
        setMeasuredDimension(w, h);
    }
}