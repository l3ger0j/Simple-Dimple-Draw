package org.l3ger0j.simpledimpledraw.viewModel

import android.graphics.Canvas
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.l3ger0j.simpledimpledraw.utils.DrawPaint
import org.l3ger0j.simpledimpledraw.utils.SpecialPath
import org.l3ger0j.simpledimpledraw.view.MainActivity
import org.l3ger0j.simpledimpledraw.view.PaintActivity

class MainViewModel : ViewModel() {
    var paintActivityField = ObservableField<PaintActivity>()
    var mainActivityField = ObservableField<MainActivity>()
    @JvmField
    var isHideLeftMenu = ObservableBoolean()
    @JvmField
    var isHideRightMenu = ObservableBoolean()
    @JvmField
    var isHideBottomMenu = ObservableBoolean()
    @JvmField
    var isHideTopMenu = ObservableBoolean()

    // region Getters
    var drawPaint: DrawPaint? = null

    // endregion Getters
    // region Setters
    var drawCanvas: Canvas = Canvas()
    var specialPath: SpecialPath = SpecialPath()
    var colorsMap: MutableMap<SpecialPath?, Int> = mutableMapOf()
    var strokeMap: MutableMap<SpecialPath?, Float> = mutableMapOf()
    var paths: ArrayList<SpecialPath> = arrayListOf()
    var undo = ArrayList<SpecialPath>()

    // endregion Setters
    private val activity: MainActivity?
        get() = mainActivityField.get()

    fun addShape(v: View?) {
        if (activity != null) activity!!.addShape(v!!)
    }

    fun showAboutDialog() {
        if (activity != null) activity!!.showAboutDialog()
    }

    fun hideAllMenu() {
        isHideLeftMenu.set(true)
        isHideRightMenu.set(true)
        isHideBottomMenu.set(true)
        isHideTopMenu.set(true)
    }

    fun showAllMenu() {
        isHideLeftMenu.set(false)
        isHideRightMenu.set(false)
        isHideBottomMenu.set(false)
        isHideTopMenu.set(false)
    }
}