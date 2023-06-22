package org.l3ger0j.simpledimpledraw.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import org.l3ger0j.simpledimpledraw.R
import org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding
import org.l3ger0j.simpledimpledraw.utils.DrawPaint
import org.l3ger0j.simpledimpledraw.utils.ShapeBuilder
import org.l3ger0j.simpledimpledraw.view.dialogs.MainDialogFrags
import org.l3ger0j.simpledimpledraw.view.dialogs.MainDialogType
import org.l3ger0j.simpledimpledraw.view.dialogs.MainPatternDialogFrags.MainPatternDialogList
import org.l3ger0j.simpledimpledraw.view.window.PopupWindowBuilder
import org.l3ger0j.simpledimpledraw.view.window.WindowType
import org.l3ger0j.simpledimpledraw.viewModel.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), MainPatternDialogList, ColorPickerDialogListener, OnSeekBarChangeListener {
    // region Layouts
    private var paintActivity: PaintActivity? = null
    private var shapeManager: ShapeBuilder? = null

    // endregion
    // region Buttons
    private var fabSave: FloatingActionButton? = null
    private var toggleButton: ToggleButton? = null

    // endregion
    private var drawPaint: DrawPaint? = null
    private var textView: TextView? = null
    private val popupWindowBuilder = PopupWindowBuilder()
    private var uriBitmap: Uri? = null
    private var path: String? = null
    private var turnOnMove = 0
    private val posPopupWindow = IntArray(2)
    private var mainViewModel: MainViewModel? = null
    private var mainBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel!!.mainActivityField.set(this)
        mainBinding?.mainViewModel = mainViewModel
        shapeManager = mainBinding?.shapeManager
        paintActivity = mainBinding?.simpleDrawingView
        drawPaint = DrawPaint()
        paintActivity!!.setDrawPaint(drawPaint)
        fabSave = mainBinding?.fabSave
        toggleButton = mainBinding?.toggleButton

        mainBinding?.fabSetSize?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabSetDrawColor?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabSetBackgroundColor?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabEraser?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabClearCanvas?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabSave?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabVisibility?.setOnClickListener(mainViewOnClickList)
        mainBinding?.fabShowMenu?.setOnClickListener(mainViewOnClickList)
        mainBinding?.toggleButton?.setOnClickListener(mainViewOnClickList)
        mainBinding?.undoButton?.setOnClickListener(mainViewOnClickList)
        mainBinding?.redoButton?.setOnClickListener(mainViewOnClickList)

        setContentView(mainBinding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (windowType in WindowType.values()) {
            popupWindowBuilder.createPopupWindow(windowType).dismiss()
        }
    }

    @SuppressLint("NonConstantResourceId")
    var mainViewOnClickList = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.undoButton -> {
                paintActivity!!.undoPath()
                paintActivity!!.invalidateView()
            }

            R.id.redoButton -> {
                paintActivity!!.redoPath()
                paintActivity!!.invalidateView()
            }

            R.id.fabSetSize -> showMinSettingDrawWindow()
            R.id.fabSetDrawColor -> createColorPickerDialog(2)
            R.id.fabSetBackgroundColor -> createColorPickerDialog(1)
            R.id.fabEraser -> if (paintActivity!!.isEraserOn()) {
                paintActivity!!.eraseCanvas(true)
                v.rotation = 180f
            } else {
                paintActivity!!.eraseCanvas(false)
                v.rotation = 0f
            }

            R.id.fabSave -> openCaptureMenu()
            R.id.fabClearCanvas -> paintActivity!!.clearCanvas()
            R.id.fabVisibility -> {}
            R.id.toggleButton -> {
                when (shapeManager!!.selectShape) {
                    1 -> {
                        paintActivity!!.drawShape(0, shapeManager!!)
                        shapeManager!!.visibility = View.GONE
                        toggleButton!!.visibility = View.GONE
                    }
                    2 -> {
                        paintActivity!!.drawShape(1, shapeManager!!)
                        shapeManager!!.visibility = View.GONE
                        toggleButton!!.visibility = View.GONE
                    }
                    3 -> {
                        paintActivity!!.drawShape(2, shapeManager!!)
                        shapeManager!!.visibility = View.GONE
                        toggleButton!!.visibility = View.GONE
                    }
                }
                mainViewModel!!.showAllMenu()
            }
        }
    }

    fun addShape(v: View) {
        when (v.id) {
            R.id.cAddCircle -> {
                shapeManager!!.selectShape = 1
                toggleButton!!.isChecked = true
                mainViewModel!!.hideAllMenu()
                shapeManager!!.visibility = View.VISIBLE
                toggleButton!!.visibility = View.VISIBLE
            }
            R.id.cAddRect -> {
                shapeManager!!.selectShape = 2
                toggleButton!!.isChecked = true
                shapeManager!!.visibility = View.VISIBLE
                mainViewModel!!.hideAllMenu()
                toggleButton!!.visibility = View.VISIBLE
            }
            R.id.cAddOval -> {
                shapeManager!!.selectShape = 3
                toggleButton!!.isChecked = true
                mainViewModel!!.hideAllMenu()
                shapeManager!!.visibility = View.VISIBLE
                toggleButton!!.visibility = View.VISIBLE
            }
        }
    }

    fun showAboutDialog() {
        val dialogFrags = MainDialogFrags()
        dialogFrags.setDialogType(MainDialogType.ABOUT_DIALOG)
        dialogFrags.show(supportFragmentManager, "aboutDialogFragment")
    }

    // region picSave
    private fun openCaptureMenu() {
        val popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.CAPTURE_MENU)
        val popupWindow = popupWindowBuilder.createPopupWindow(WindowType.CAPTURE_MENU)
        popupWindow.showAtLocation(fabSave, Gravity.START, 870, 140)
        popupView?.findViewById<View>(R.id.saveCanvas)?.setOnClickListener {
            try {
                val extStorage = getExternalFilesDir(null)
                val date = Date()
                val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
                val fileName = "PIC_" + sdf.format(date) + ".png"
                path = extStorage!!.absolutePath + "/" + fileName
                val myFile = File(path.toString())
                val fOut = FileOutputStream(myFile)
                paintActivity!!.canvasBitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut)
                fOut.flush()
                fOut.close()
                uriBitmap = FileProvider.getUriForFile(
                        this,
                        "org.l3ger0j.simpledimpledraw.provider",
                        myFile
                )
                MediaStore.Images.Media.insertImage(contentResolver, myFile.absolutePath, myFile.name, myFile.name)
                MediaScannerConnection.scanFile(applicationContext, arrayOf(myFile.toString()), null, null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            popupWindow.dismiss()
            Toast.makeText(this, "Save", Toast.LENGTH_LONG).show()
            val dialogFrags = MainDialogFrags()
            dialogFrags.setDialogType(MainDialogType.CAPTURE_DIALOG)
            dialogFrags.imageBitmap.set(paintActivity!!.canvasBitmap)
            dialogFrags.show(supportFragmentManager, "captureDialogFragment")
        }
        if ((uriBitmap != null) and (path != null)) {
            popupView?.findViewById<View>(R.id.shareCanvas)?.setOnClickListener { sendPic() }
        } else {
            popupView?.findViewById<View>(R.id.shareCanvas)?.isEnabled = false
        }
    }

    private fun sendPic() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uriBitmap)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }

    // endregion
    // region FAB popupWindow
    private fun showMinSettingDrawWindow() {
        val popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.MINIMAL_SETTING)
        val popupWindow = popupWindowBuilder.createPopupWindow(WindowType.MINIMAL_SETTING)
        popupWindow.showAtLocation(findViewById(R.id.fabSetSize),
                Gravity.CENTER, posPopupWindow[0], posPopupWindow[1])
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (turnOnMove == 1) {
            popupView?.setOnTouchListener(object : OnTouchListener {
                private var wx = 0
                private var wy = 0

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            wx = event.x.toInt()
                            wy = event.y.toInt()
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val xp = event.rawX.toInt()
                            val yp = event.rawY.toInt()
                            val sides = xp - wx
                            val topBot = yp - wy
                            popupWindow.update(sides, topBot, -1, -1, true)
                            posPopupWindow[0] = sides
                            posPopupWindow[1] = topBot
                        }
                    }
                    return true
                }
            })
        }
        val seekBar = popupView?.findViewById<SeekBar>(R.id.seekBar)
        seekBar?.setOnSeekBarChangeListener(this)
        textView = popupView?.findViewById(R.id.textView)
        textView?.text = drawPaint!!.strokeWidth.toString()
        if (seekBar != null) {
            seekBar.progress = drawPaint!!.strokeWidth.toInt()
        }
        popupView?.findViewById<ImageButton>(R.id.turnMoveDialog)?.setOnClickListener {
            if (turnOnMove == 0) {
                turnOnMove = 1
                popupWindow.dismiss()
                showMinSettingDrawWindow()
            } else if (turnOnMove == 1) {
                turnOnMove = 0
                popupWindow.dismiss()
                showMinSettingDrawWindow()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        textView!!.text = seekBar.progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        paintActivity!!.setStroke(seekBar.progress)
        shapeManager!!.mStrokeWidth = seekBar.progress
    }

    // endregion
    private fun createColorPickerDialog(id: Int) {
        if (id == 1) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_background)
                    .setColor(paintActivity!!.backgroundColor)
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .show(this)
        } else if (id == 2) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_drawing)
                    .setColor(drawPaint!!.color)
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .show(this)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == 1) {
            paintActivity!!.setBackColor(color)
            popupWindowBuilder.setBackgroundColor(color)
        } else if (dialogId == 2) {
            paintActivity!!.setColor(color)
            shapeManager!!.mStrokeColor = color
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}
    override fun onDialogNegativeClick(mainDialogFrags: MainDialogFrags?) {
        sendPic()
    }
}