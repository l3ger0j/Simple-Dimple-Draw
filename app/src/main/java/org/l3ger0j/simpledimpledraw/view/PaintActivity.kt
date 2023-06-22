package org.l3ger0j.simpledimpledraw.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import org.l3ger0j.simpledimpledraw.utils.DrawPaint
import org.l3ger0j.simpledimpledraw.utils.ShapeBuilder
import org.l3ger0j.simpledimpledraw.utils.SpecialPath
import org.l3ger0j.simpledimpledraw.viewModel.MainViewModel

class PaintActivity(context: Context?, attributeSet: AttributeSet?) : View(context, attributeSet) {
    private lateinit var drawPaint: DrawPaint
    private lateinit var drawCanvas: Canvas
    private val identityMatrix = Matrix()
    private lateinit var specialPath: SpecialPath
    private val viewModel: MainViewModel = ViewModelProvider((getContext() as MainActivity))[MainViewModel::class.java]
    private var paths = ArrayList<SpecialPath>()
    private val undo = ArrayList<SpecialPath>()
    private var colorsMap: MutableMap<SpecialPath?, Int> = HashMap()
    private var strokeMap: MutableMap<SpecialPath?, Float> = HashMap()
    private var mBackColor = 0
    private var mColor = Color.BLACK
    private var mStroke = 30f
    private var eraserOn = false

    val canvasBitmap: Bitmap
        get() {
            this.isDrawingCacheEnabled = false
            this.isDrawingCacheEnabled = true
            return Bitmap.createBitmap(this.drawingCache)
        }

    val backgroundColor: Int
        get() {
            var color = Color.WHITE
            val background = background
            if (background is ColorDrawable) color = background.color
            invalidate()
            return color
        }

    fun setDrawPaint(drawPaint: DrawPaint?) {
        if (drawPaint != null) {
            this.drawPaint = drawPaint
        }
    }

    fun setColor(color: Int) {
        mColor = color
        invalidate()
    }

    fun setBackColor(color: Int) {
        setBackgroundColor(color)
    }

    fun setStroke(stroke: Int) {
        mStroke = stroke.toFloat()
        invalidate()
    }

    fun isEraserOn(): Boolean {
        return !eraserOn
    }

    fun eraseCanvas(isEraserOn: Boolean) {
        if (isEraserOn) {
            eraserOn = true
            mBackColor = mColor
            mColor = backgroundColor
        } else {
            eraserOn = false
            mColor = mBackColor
        }
        invalidate()
    }

    fun clearCanvas() {
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        paths.clear()
        undo.clear()
        invalidate()
    }

    fun drawShape(id: Int, shapeManager: ShapeBuilder) {
        val mRectF = RectF(shapeManager.mCropRect)
        when (id) {
            0 -> {
                specialPath.addCircle(
                        mRectF.centerX(),
                        mRectF.centerY(),
                        shapeManager.radiusRect.toFloat(),
                        Path.Direction.CW)
                paths.add(specialPath)
                colorsMap[specialPath] = mColor
                strokeMap[specialPath] = mStroke
                specialPath = SpecialPath()
                invalidate()
            }

            1 -> {
                specialPath.addRect(
                        mRectF,
                        Path.Direction.CW)
                paths.add(specialPath)
                colorsMap[specialPath] = mColor
                strokeMap[specialPath] = mStroke
                specialPath = SpecialPath()
                invalidate()
            }

            2 -> {
                specialPath.addOval(
                        mRectF,
                        Path.Direction.CW)
                paths.add(specialPath)
                colorsMap[specialPath] = mColor
                strokeMap[specialPath] = mStroke
                specialPath = SpecialPath()
                invalidate()
            }
        }
    }

    fun redoPath() {
        if (undo.size > 0) {
            paths.add(undo.removeAt(undo.size - 1))
            undo.trimToSize()
        } else {
            Toast.makeText(context, "EMPTY REDO", Toast.LENGTH_SHORT).show()
        }
    }

    fun undoPath() {
        if (paths.size > 0) {
            undo.add(paths.removeAt(paths.size - 1))
            paths.trimToSize()
        } else {
            Toast.makeText(context, "EMPTY UNDO", Toast.LENGTH_SHORT).show()
        }
    }

    fun invalidateView() {
        this.invalidate()
    }

    init {
        viewModel.paintActivityField.set(this)
        init()
    }

    private fun init() {
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundColor(Color.WHITE)

        // undo/redo system
        paths = viewModel.paths
        paths = viewModel.undo
        if (viewModel.drawPaint != null) {
            drawPaint = viewModel.drawPaint!!
        }
        drawCanvas = viewModel.drawCanvas
        specialPath = viewModel.specialPath
        colorsMap = viewModel.colorsMap
        strokeMap = viewModel.strokeMap
    }

    override fun onSaveInstanceState(): Parcelable? {
        viewModel.drawCanvas = drawCanvas
        viewModel.drawPaint = drawPaint
        viewModel.specialPath = specialPath
        viewModel.strokeMap = strokeMap
        viewModel.colorsMap = colorsMap
        viewModel.paths = paths
        viewModel.undo = undo
        return super.onSaveInstanceState()
    }

    override fun onDraw(canvas: Canvas) {
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        drawCanvas.drawPath(specialPath, drawPaint)
        for (p in paths) {
            val currCnt = colorsMap[p]
            val temp1 = currCnt ?: Color.BLACK
            drawPaint.color = temp1
            val currCount = strokeMap[p]
            val currCount1: Float = (currCount ?: 30) as Float
            drawPaint.strokeWidth = currCount1
            canvas.drawPath(p, drawPaint)
        }
        drawPaint.color = mColor
        canvas.drawBitmap(myCanvasBitmap!!, identityMatrix, null)
    }

    private fun remove(index: Int) {
        paths.removeAt(index)
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val pointX = event.x
        val pointY = event.y
        if (eraserOn) {
            for (i in paths.indices) {
                val r = RectF()
                val pComp = Point(event.x.toInt(), event.y.toInt())
                val mPath: Path = paths[i]
                mPath.computeBounds(r, true)
                if (r.contains(pComp.x.toFloat(), pComp.y.toFloat())) {
                    remove(i)
                    break
                }
            }
            return false
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> specialPath.moveTo(pointX, pointY)
                MotionEvent.ACTION_MOVE -> specialPath.lineTo(pointX, pointY)
                MotionEvent.ACTION_UP -> {
                    specialPath.lineTo(pointX, pointY)
                    drawCanvas.drawPath(specialPath, drawPaint)
                    paths.add(specialPath)
                    colorsMap[specialPath] = mColor
                    strokeMap[specialPath] = mStroke
                    specialPath = SpecialPath()
                }
            }
        }
        postInvalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        myCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas.setBitmap(myCanvasBitmap)
        setMeasuredDimension(w, h)
    }

    companion object {
        private var myCanvasBitmap: Bitmap? = null
    }
}