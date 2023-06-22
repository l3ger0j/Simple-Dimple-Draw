package org.l3ger0j.simpledimpledraw.view.window

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import org.l3ger0j.simpledimpledraw.R

class PopupWindowBuilder {
    private var popupView: View? = null
    private var popupWindow: PopupWindow? = null
    private var mColor = Color.WHITE
    fun setBackgroundColor(color: Int) {
        mColor = color
    }

    fun createPopupWindowView(activity: Activity, windowType: WindowType): View? {
        val linearLayout = LinearLayout(activity)
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        when (windowType) {
            WindowType.MINIMAL_SETTING -> {
                popupView = layoutInflater.inflate(R.layout.popup_fab, linearLayout)
                popupView?.let { setBackgroundColor(mColor) }
            }

            WindowType.CAPTURE_MENU -> {
                popupView = layoutInflater.inflate(R.layout.popup_capture_menu, linearLayout)
                popupView?.let { setBackgroundColor(mColor) }
            }
        }
        return popupView
    }

    fun createPopupWindow(windowType: WindowType): PopupWindow {
        when (windowType) {
            WindowType.MINIMAL_SETTING, WindowType.CAPTURE_MENU -> {
                popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true)
                popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                popupWindow!!.isOutsideTouchable = true
            }
        }
        return popupWindow as PopupWindow
    }
}