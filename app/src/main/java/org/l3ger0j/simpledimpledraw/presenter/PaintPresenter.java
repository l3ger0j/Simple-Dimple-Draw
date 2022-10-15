package org.l3ger0j.simpledimpledraw.presenter;

import android.graphics.Canvas;
import android.os.Bundle;

import org.l3ger0j.simpledimpledraw.DrawPaint;
import org.l3ger0j.simpledimpledraw.model.SpecialPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintPresenter implements MainContract.PaintPresenter {
    private static PaintPresenter INSTANCE;

    private DrawPaint drawPaint;
    private Canvas drawCanvas;
    private SpecialPath specialPath;

    private Map<SpecialPath, Integer> colorsMap;
    private Map<SpecialPath, Float> strokeMap;
    private ArrayList<SpecialPath> paths;

    public static PaintPresenter newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PaintPresenter();
        }
        return INSTANCE;
    }

    public PaintPresenter() {

    }

    public void setDrawCanvas(Canvas drawCanvas) {
        this.drawCanvas = drawCanvas;
    }

    public void setDrawPaint(DrawPaint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public void setSpecialPath(SpecialPath specialPath) {
        this.specialPath = specialPath;
    }

    public void setColorsMap(Map<SpecialPath, Integer> colorsMap) {
        this.colorsMap = colorsMap;
    }

    public void setStrokeMap (Map<SpecialPath, Float> strokeMap) {
        this.strokeMap = strokeMap;
    }

    public void setPaths(ArrayList<SpecialPath> paths) {
        this.paths = paths;
    }

    public Map<SpecialPath, Float> getStrokeMap() {
        return strokeMap;
    }

    public Map<SpecialPath, Integer> getColorsMap() {
        return colorsMap;
    }

    public ArrayList<SpecialPath> getPaths() {
        return paths;
    }

    public Canvas getDrawCanvas() {
        return drawCanvas;
    }

    public DrawPaint getDrawPaint() {
        return drawPaint;
    }

    public SpecialPath getSpecialPath() {
        return specialPath;
    }
}
