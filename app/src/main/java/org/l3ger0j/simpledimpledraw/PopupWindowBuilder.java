package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

enum WindowType {
    ColorSetting,
    MinimalSetting,
    ClearCanvas,
    CaptureMenu
}

public class PopupWindowBuilder {
    private View popupView;
    private PopupWindow popupWindow;
    private int mColor = Color.WHITE;

    public void setBackgroundColor (int color) {
        this.mColor = color;
    }

    public View createPopupWindowView (Activity activity, @NonNull WindowType windowType) {
        LinearLayout linearLayout = new LinearLayout(activity);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (windowType) {
            case ColorSetting:
                popupView = layoutInflater.inflate(R.layout.popup_color_setting, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
            case MinimalSetting:
                popupView = layoutInflater.inflate(R.layout.popup_fab, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
            case ClearCanvas:
                popupView = layoutInflater.inflate(R.layout.popup_clear, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
            case CaptureMenu:
                popupView = layoutInflater.inflate(R.layout.popup_capture_menu, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
        }

        return popupView;
    }

    public PopupWindow createPopupWindow (@NonNull WindowType windowType) {
        switch (windowType) {
            case ColorSetting:
            case MinimalSetting:
            case ClearCanvas:
            case CaptureMenu:
                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                popupWindow.setOutsideTouchable(true);
                break;
        }

        return popupWindow;
    }
}
