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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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
    static Bitmap myCanvasBitmap;
    public Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    private SpecialPath specialPath;

    private final ArrayList<SpecialPath> paths = new ArrayList<>();
    private final ArrayList<SpecialPath> undo = new ArrayList<>();
    private final Map<Path, Integer> colorsMap = new HashMap<>();
    private final Map<Path, Integer> strokeMap = new HashMap<>();

    private int mColor = Color.BLACK;
    private int mStroke = 30;
    private boolean isErasemode = false;

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

    public void eraseCanvas(View v) {
        if (!isErasemode) {
            isErasemode = true;
            v.setRotation(180);
        } else {
            isErasemode = false;
            v.setRotation(0);
        }
        invalidate();
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

    private void remove(int index){
        paths.remove(index);
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
        colorsMap.put(specialPath, mColor);
        strokeMap.put(specialPath, mStroke);
        paths.add(specialPath);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
        drawCanvas.drawPath(specialPath, drawPaint);
        for (SpecialPath p : paths) {
            drawPaint.setColor(colorsMap.get(p));
            drawPaint.setStrokeWidth(strokeMap.get(p));
            canvas.drawPath(p, drawPaint);
        }
        drawPaint.setColor(mColor);
        canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super .onTouchEvent(event);

        float pointX = event.getX();
        float pointY = event.getY();

        // TODO Rework this
        if (isErasemode) {
            for (int i = 0; i < paths.size(); i++) {
                RectF r = new RectF();
                Point pComp = new Point((int) (event.getX()) , (int) (event.getY()));
                Path mPath = paths.get(i);
                mPath.computeBounds(r , true);
                if (r.contains(pComp.x , pComp.y)) {
                    remove(i);
                    break;
                }
            }
            return false;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    specialPath.moveTo(pointX , pointY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    specialPath.lineTo(pointX , pointY);
                    break;
                case MotionEvent.ACTION_UP:
                    specialPath.lineTo(pointX, pointY);
                    drawCanvas.drawPath(specialPath, drawPaint);
                    paths.add(specialPath);
                    colorsMap.put(specialPath, mColor);
                    strokeMap.put(specialPath, mStroke);
                    specialPath = new SpecialPath();
                    break;
            }
            postInvalidate();
            return true;
        }
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