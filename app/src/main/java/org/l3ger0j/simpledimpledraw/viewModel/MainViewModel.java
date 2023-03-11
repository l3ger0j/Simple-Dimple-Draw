package org.l3ger0j.simpledimpledraw.viewModel;

import android.graphics.Canvas;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import org.l3ger0j.simpledimpledraw.utils.DrawPaint;
import org.l3ger0j.simpledimpledraw.utils.SpecialPath;
import org.l3ger0j.simpledimpledraw.view.MainActivity;
import org.l3ger0j.simpledimpledraw.view.PaintActivity;

import java.util.ArrayList;
import java.util.Map;

public class MainViewModel extends ViewModel {
    public ObservableField<PaintActivity> paintActivityField =
            new ObservableField<>();

    public ObservableField<MainActivity> mainActivityField =
            new ObservableField<>();

    public ObservableBoolean isHideLeftMenu = new ObservableBoolean();
    public ObservableBoolean isHideRightMenu = new ObservableBoolean();
    public ObservableBoolean isHideBottomMenu = new ObservableBoolean();
    public ObservableBoolean isHideTopMenu = new ObservableBoolean();

    private DrawPaint drawPaint;
    private Canvas drawCanvas;
    private SpecialPath specialPath;

    private Map<SpecialPath, Integer> colorsMap;
    private Map<SpecialPath, Float> strokeMap;
    private ArrayList<SpecialPath> paths;
    private ArrayList<SpecialPath> undo = new ArrayList<>();

    // region Getters
    public DrawPaint getDrawPaint() {
        return drawPaint;
    }
    public Canvas getDrawCanvas() {
        return drawCanvas;
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
    public ArrayList<SpecialPath> getUndo() {
        return undo;
    }
    public SpecialPath getSpecialPath() {
        return specialPath;
    }
    // endregion Getters

    // region Setters
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
    public void setUndo(ArrayList<SpecialPath> undo) {
        this.undo = undo;
    }
    // endregion Setters

    private MainActivity getActivity() {
        return mainActivityField.get();
    }

    public void addShape(View v) {
        if (getActivity() != null) {
            getActivity().addShape(v);
        }
    }

    public void showAboutDialog() {
        if (getActivity() != null) {
            getActivity().showAboutDialog();
        }
    }

    public void hideAllMenu () {
        isHideLeftMenu.set(true);
        isHideRightMenu.set(true);
        isHideBottomMenu.set(true);
        isHideTopMenu.set(true);
    }

    public void showAllMenu () {
        isHideLeftMenu.set(false);
        isHideRightMenu.set(false);
        isHideBottomMenu.set(false);
        isHideTopMenu.set(false);
    }
}
