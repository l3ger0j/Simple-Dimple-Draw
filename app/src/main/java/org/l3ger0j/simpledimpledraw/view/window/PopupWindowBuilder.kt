package org.l3ger0j.simpledimpledraw.view.window;

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

import org.l3ger0j.simpledimpledraw.R;

public class PopupWindowBuilder {
    private View popupView;
    private PopupWindow popupWindow;
    private int mColor = Color.WHITE;

    public void setBackgroundColor (int color) {
        this.mColor = color;
    }

    public View createPopupWindowView (Activity activity, @NonNull WindowType windowType) {
        var linearLayout = new LinearLayout(activity);
        var layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (windowType) {
            case MINIMAL_SETTING:
                popupView = layoutInflater.inflate(R.layout.popup_fab, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
            case CAPTURE_MENU:
                popupView = layoutInflater.inflate(R.layout.popup_capture_menu, linearLayout);
                popupView.setBackgroundColor(mColor);
                break;
        }

        return popupView;
    }

    public PopupWindow createPopupWindow (@NonNull WindowType windowType) {
        switch (windowType) {
            case MINIMAL_SETTING:
            case CAPTURE_MENU:
                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                popupWindow.setOutsideTouchable(true);
                break;
        }

        return popupWindow;
    }
}
