package org.l3ger0j.simpledimpledraw.view;

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
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.l3ger0j.simpledimpledraw.utils.DrawPaint;
import org.l3ger0j.simpledimpledraw.utils.ShapeBuilder;
import org.l3ger0j.simpledimpledraw.utils.SpecialPath;
import org.l3ger0j.simpledimpledraw.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintActivity extends View {
    private DrawPaint drawPaint;
    private static Bitmap myCanvasBitmap;
    private Canvas drawCanvas;
    private final Matrix identityMatrix = new Matrix();
    private SpecialPath specialPath;
    private final MainViewModel viewModel;

    private ArrayList<SpecialPath> paths = new ArrayList<>();
    private final ArrayList<SpecialPath> undo = new ArrayList<>();
    private Map<SpecialPath, Integer> colorsMap = new HashMap<>();
    private Map<SpecialPath, Float> strokeMap = new HashMap<>();

    private int mBackColor = 0;
    private int mColor = Color.BLACK;
    private float mStroke = 30;
    private boolean eraserOn = false;

    public Bitmap getCanvasBitmap(){
        this.setDrawingCacheEnabled(false);
        this.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(this.getDrawingCache());
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

    public void setBackColor (int color) {
        setBackgroundColor(color);
    }

    public void setStroke (int stroke) {
        this.mStroke = stroke;
        invalidate();
    }

    public boolean isEraserOn() {
        return !eraserOn;
    }

    public void eraseCanvas (boolean isEraserOn) {
        if (isEraserOn) {
            eraserOn = true;
            mBackColor = mColor;
            mColor = getBackgroundColor();
        } else {
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

    public void drawShape (int id , @NonNull ShapeBuilder shapeManager) {
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
            undo.trimToSize();
        } else {
            Toast.makeText(getContext() , "EMPTY REDO" , Toast.LENGTH_SHORT).show();
        }
    }

    public void undoPath () {
        if (paths.size()>0){
            undo.add(paths.remove(paths.size()-1));
            paths.trimToSize();
        } else {
            Toast.makeText(getContext() , "EMPTY UNDO" , Toast.LENGTH_SHORT).show();
        }
    }

    public void invalidateView () {
        this.invalidate();
    }

    public PaintActivity(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        viewModel = new ViewModelProvider((MainActivity) getContext()).get(MainViewModel.class);
        viewModel.paintActivityField.set(this);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundColor(Color.WHITE);

        // undo/redo system
        if (viewModel.getPaths() != null) {
            paths = viewModel.getPaths();
        } else {
            paths.add(specialPath);
        }
        if (viewModel.getUndo() != null) {
            paths = viewModel.getUndo();
        }

        if (viewModel.getDrawPaint() != null) {
            drawPaint = viewModel.getDrawPaint();
        } else {
            drawPaint = new DrawPaint();
        }

        if (viewModel.getDrawCanvas() != null) {
            drawCanvas = viewModel.getDrawCanvas();
        } else {
            drawCanvas = new Canvas();
        }

        if (viewModel.getSpecialPath() != null) {
            specialPath = viewModel.getSpecialPath();
        } else {
            specialPath = new SpecialPath();
        }

        if (viewModel.getColorsMap() != null) {
            colorsMap = viewModel.getColorsMap();
        } else {
            colorsMap.put(specialPath, mColor);
        }

        if (viewModel.getStrokeMap() != null) {
            strokeMap = viewModel.getStrokeMap();
        } else {
            strokeMap.put(specialPath, mStroke);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        viewModel.setDrawCanvas(drawCanvas);
        viewModel.setDrawPaint(drawPaint);
        viewModel.setSpecialPath(specialPath);
        viewModel.setStrokeMap(strokeMap);
        viewModel.setColorsMap(colorsMap);
        viewModel.setPaths(paths);
        viewModel.setUndo(undo);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
        drawCanvas.drawPath(specialPath, drawPaint);
        for (SpecialPath p : paths) {
            Integer currCnt = colorsMap.get(p);
            int temp1 = currCnt == null ? Color.BLACK : currCnt;
            drawPaint.setColor(temp1);
            Float currCount = strokeMap.get(p);
            float currCount1 = currCount == null ? 30 : currCount;
            drawPaint.setStrokeWidth(currCount1);
            canvas.drawPath(p, drawPaint);
        }
        drawPaint.setColor(mColor);
        canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);
    }

    private void remove(int index){
        paths.remove(index);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super .onTouchEvent(event);
        float pointX = event.getX();
        float pointY = event.getY();
        if (eraserOn) {
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
                    specialPath.lineTo(pointX, pointY);
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