package com.kayadami.bouldering.editor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import androidx.exifinterface.media.ExifInterface
import com.crust87.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class EditorView : SurfaceView, SurfaceHolder.Callback, OnGestureListener {

    val options = Options()
    var color: Int
        get() = options.color
        set(color) {
            options.color = color
            invalidate()
        }

    private var onSelectedChangeListener: OnSelectedChangeListener? = null
    private var onProblemListener: OnProblemListener? = null
    private var onClickListener: View.OnClickListener? = null

    // Components
    private var imageBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private var imagePaint = Paint()
    private var mask = Mask(options)
    private var holderBoxList = ArrayList<HolderBox>()
    private var imageGenerator = ImageGenerator(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    private var suppMatrix = Matrix()
    private var scaleDragDetector = CustomGestureDetector(context, this)

    // Attributes
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var scale: Float = 0.toFloat()
    private var isViewer: Boolean = false
    private lateinit var bouldering: Bouldering

    // Working Variables
    private var editorTouchEvent = EditorTouchEvent.idle
    private var isLoadingFinish: Boolean = false
    var isLoading: Boolean = false
    private var isAbleOpen: Boolean = false

    private var _selectedHolderBox: HolderBox? = null
    val selectedHolderBox: HolderBox?
        get() = _selectedHolderBox

    // Interface
    interface OnSelectedChangeListener {
        fun onSelectedChange(selectedHolder: HolderBox?)
    }

    interface OnProblemListener {
        fun onLoadingStart()
        fun onLoadingFinish()
    }

    private enum class EditorTouchEvent {
        idle,
        selecting,
        deselecting,
        transforming,
        zooming,
        moving
    }

    // Constructor
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    init {
        EditorConfigurations.init(context)
        suppMatrix.reset()
        options.scale = Options.MIN_SCALE
        options.color = Color.WHITE

        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (viewWidth != width || viewHeight != height) {
            viewWidth = width
            viewHeight = height

            if (isAbleOpen) {
                openImage()
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    fun setProblem(p: Bouldering) {
        bouldering = p

        p.color?.let {
            options.color = Color.parseColor(p.color)
        }

        isAbleOpen = true

        if (viewWidth != 0 && viewHeight != 0) {
            openImage()
        }
    }

    private fun openImage() = CoroutineScope(Dispatchers.Main).launch {
        suppMatrix.reset()

        isLoadingFinish = false
        isLoading = true
        onProblemListener?.onLoadingStart()

        imageBitmap = withContext(Dispatchers.IO) {
            val imageStream = FileInputStream(bouldering.path)
            val exif = ExifInterface(bouldering.path)
            val rotation = when (Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                ?: "0")) {
                3 -> 180
                6 -> 90
                8 -> 270
                else -> 0
            }
            val bitmapOptions = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            val matrix = Matrix().apply {
                postRotate(rotation.toFloat())
            }

            val image = BitmapFactory.decodeStream(imageStream, null, bitmapOptions)

            image ?: throw RuntimeException()

            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }

        val width = imageBitmap.width
        val height = imageBitmap.height

        imagePaint.isFilterBitmap = true

        val scaleX: Float
        val scaleY: Float

        scaleX = getWidth().toFloat() / width
        scaleY = getHeight().toFloat() / height

        val newWidth: Int
        val newHeight: Int

        if (scaleX > scaleY) {
            scale = scaleY
        } else {
            scale = scaleX
        }

        newWidth = (width * scale).toInt()
        newHeight = (height * scale).toInt()

        options.bound = Rect(0, 0, newWidth, newHeight)

        optimizeMatrix()

        holderBoxList.clear()

        for (h in bouldering.holderList) {
            val holderBox = HolderBox(options)
            holderBox.radius = h.radius * scale
            holderBox.setPosition(h.x * scale, h.y * scale)
            holderBox.isSpecial = h.isSpecial
            holderBox.isInOrder = h.isInOrder
            holderBox.index = h.index
            holderBoxList.add(holderBox)
        }

        isLoadingFinish = true
        isLoading = false
        invalidate()
        onProblemListener?.onLoadingFinish()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isLoadingFinish) {
            return
        }

        canvas.setMatrix(suppMatrix)
        canvas.drawBitmap(imageBitmap, null, options.bound, imagePaint)

        mask.draw(canvas, holderBoxList)

        for (hb in holderBoxList) {
            hb.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isLoadingFinish) {
            return false
        }

        scaleDragDetector.onTouchEvent(event)

        val scale = getMatrixValue(suppMatrix, Matrix.MSCALE_X)
        val left = getMatrixValue(suppMatrix, Matrix.MTRANS_X)
        val top = getMatrixValue(suppMatrix, Matrix.MTRANS_Y)

        val x = (event.x - left) / scale
        val y = (event.y - top) / scale

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown(x, y)
            MotionEvent.ACTION_MOVE -> onTouchMove(x, y)
            MotionEvent.ACTION_UP -> onTouchUp(x, y)
        }

        invalidate()
        return true
    }

    fun clear() {
        _selectedHolderBox = null
        editorTouchEvent = EditorTouchEvent.idle
        holderBoxList.clear()

        invalidate()
    }

    fun toggleOrder() {
        _selectedHolderBox?.let {
            it.isInOrder = it.isInOrder != true

            if (it.isInOrder) {
                it.index = Int.MAX_VALUE
            }

            sort()
            invalidate()

            onSelectedChangeListener?.onSelectedChange(_selectedHolderBox)
        }
    }

    fun toggleSpecial() {
        _selectedHolderBox?.let {
            it.isSpecial = it.isSpecial != true

            if (it.isSpecial) {
                it.isInOrder = false
                it.index = 0
            }

            sort()
            invalidate()

            onSelectedChangeListener?.onSelectedChange(_selectedHolderBox)
        }
    }

    fun sort() {
        holderBoxList.sort()

        var index = 1
        for (holderBox in holderBoxList) {
            if (holderBox.isInOrder && !holderBox.isSpecial) {
                holderBox.index = index
                index++
            }
        }
    }

    fun deleteSelectedHolder() {
        _selectedHolderBox?.let {
            val holder = it
            deselectHolder()
            holderBoxList.remove(holder)
            editorTouchEvent = EditorTouchEvent.idle

            invalidate()
        }
    }

    private fun deselectHolder() {
        _selectedHolderBox?.let {
            it.isSelected = false
            _selectedHolderBox = null
            editorTouchEvent = EditorTouchEvent.deselecting

            onSelectedChangeListener?.onSelectedChange(null)
        }
    }

    private fun selectHolder(holderBox: HolderBox) {
        holderBox.isSelected = true
        _selectedHolderBox = holderBox
        editorTouchEvent = EditorTouchEvent.selecting

        onSelectedChangeListener?.onSelectedChange(_selectedHolderBox)
    }

    private fun findHolder(x: Float, y: Float): HolderBox? {
        for (hb in holderBoxList) {
            if (hb.contains(x, y)) {
                return hb
            }
        }

        return null
    }

    private fun onTouchDown(x: Float, y: Float) {
        if (isViewer) {
            return
        }

        if (editorTouchEvent == EditorTouchEvent.idle) {

            val holderBox = findHolder(x, y)

            if (holderBox != null) {
                selectHolder(holderBox)
            }
        } else if (editorTouchEvent == EditorTouchEvent.selecting) {
            val holderBox = findHolder(x, y)

            if (holderBox == null) {
                deselectHolder()
            } else {
                if (_selectedHolderBox != holderBox) {
                    deselectHolder()
                    selectHolder(holderBox)
                }
            }
        }
    }

    private fun onTouchMove(x: Float, y: Float) {
        if (isViewer) {
            return
        }

        if (editorTouchEvent == EditorTouchEvent.selecting) {
            _selectedHolderBox?.startTransforming(x, y)
            editorTouchEvent = EditorTouchEvent.transforming
        } else if (editorTouchEvent == EditorTouchEvent.transforming) {
            _selectedHolderBox?.keepTransforming(x, y)
        }
    }

    private fun onTouchUp(x: Float, y: Float) {
        if (editorTouchEvent == EditorTouchEvent.transforming && !isViewer) {
            _selectedHolderBox?.finishTransforming()
            editorTouchEvent = EditorTouchEvent.selecting
        } else if (editorTouchEvent == EditorTouchEvent.zooming || editorTouchEvent == EditorTouchEvent.moving) {
            editorTouchEvent = EditorTouchEvent.idle
        } else if (editorTouchEvent == EditorTouchEvent.idle) {
            if (!isViewer) {
                val holderBox = HolderBox(options)
                holderBox.setPosition(x, y)
                holderBoxList.add(holderBox)

                selectHolder(holderBox)
            } else {
                onClickListener?.onClick(this)
            }
        } else if (editorTouchEvent == EditorTouchEvent.deselecting) {
            editorTouchEvent = EditorTouchEvent.idle
        }
    }

    override fun onDrag(dx: Float, dy: Float) {
        if (editorTouchEvent == EditorTouchEvent.transforming) {
            return
        }

        if (editorTouchEvent == EditorTouchEvent.selecting) {
            deselectHolder()
        }

        editorTouchEvent = EditorTouchEvent.moving

        suppMatrix.postTranslate(dx, dy)
        optimizeMatrix()
    }

    override fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float) {}

    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
        if (editorTouchEvent == EditorTouchEvent.transforming) {
            return
        }

        if (editorTouchEvent == EditorTouchEvent.selecting) {
            deselectHolder()
        }

        editorTouchEvent = EditorTouchEvent.zooming

        if ((calcScale() < Options.MAX_SCALE || scaleFactor < 1f) && (calcScale() > Options.MIN_SCALE || scaleFactor > 1f)) {
            suppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
        }

        optimizeMatrix()
    }

    fun calcScale(): Float {
        return Math.sqrt((Math.pow(getMatrixValue(suppMatrix, Matrix.MSCALE_X).toDouble(), 2.0).toFloat() + Math.pow(getMatrixValue(suppMatrix, Matrix.MSKEW_Y).toDouble(), 2.0).toFloat()).toDouble()).toFloat()
    }

    private fun getMatrixValue(matrix: Matrix, whichValue: Int): Float {
        val matrixValues = FloatArray(9)
        matrix.getValues(matrixValues)
        return matrixValues[whichValue]
    }

    private fun optimizeMatrix() {
        if (calcScale() < Options.MIN_SCALE) {
            suppMatrix.setScale(Options.MIN_SCALE, Options.MIN_SCALE)
        }

        options.scale = calcScale()

        val rect = RectF()
        suppMatrix.mapRect(rect)

        var deltaX = 0f
        var deltaY = 0f

        val scale = getMatrixValue(suppMatrix, Matrix.MSCALE_X)
        val left = getMatrixValue(suppMatrix, Matrix.MTRANS_X)
        val top = getMatrixValue(suppMatrix, Matrix.MTRANS_Y)
        val right = getMatrixValue(suppMatrix, Matrix.MTRANS_X) + scale * options.bound.width()
        val bottom = getMatrixValue(suppMatrix, Matrix.MTRANS_Y) + scale * options.bound.height()

        if (scale * options.bound.height() > viewHeight) {
            if (top > 0) {
                deltaY = -top
            }

            if (bottom < viewHeight) {
                deltaY = viewHeight - bottom
            }
        } else {
            val topMargin = (viewHeight - scale * options.bound.height()) / 2
            deltaY = topMargin - top
        }

        if (scale * options.bound.width() > viewWidth) {
            if (left > 0) {
                deltaX = -left
            }

            if (right < viewWidth) {
                deltaX = viewWidth - right
            }
        } else {
            val leftMargin = (viewWidth - scale * options.bound.width()) / 2
            deltaX = leftMargin - left
        }

        suppMatrix.postTranslate(deltaX, deltaY)
    }

    fun modify(): Bouldering = runBlocking(Dispatchers.Main) {
        deselectHolder()
        invalidate()

        bouldering = withContext(Dispatchers.IO) { modifyBouldering(bouldering) }

        bouldering
    }

    fun create(): Bouldering = runBlocking(Dispatchers.Main) {
        deselectHolder()
        invalidate()

        bouldering = withContext(Dispatchers.IO) { createBouldering() }

        bouldering
    }

    private fun modifyBouldering(bouldering: Bouldering): Bouldering {
        editorTouchEvent = EditorTouchEvent.idle

        try {
            val problemDir = getProblemDir(bouldering.createdAt)
            val thumb = File(problemDir, "thumb.jpg")
            val color = String.format("#%06X", 0xFFFFFF and options.color)
            val currentTime = System.currentTimeMillis()
            val holderList = ArrayList<Holder>()

            for (h in holderBoxList) {
                holderList.add(h.convert(scale))
            }

            bouldering.thumb = thumb.absolutePath
            bouldering.color = color
            bouldering.updatedAt = currentTime
            bouldering.holderList = holderList

            val thumbnail = imageGenerator.createThumbnail(bouldering)
            FileUtil.store(thumbnail, thumb)
        } catch (e: IOException) {
            throw BoulderingException("Can not modify Boulder")
        }

        return bouldering
    }

    private fun createBouldering(): Bouldering {
        editorTouchEvent = EditorTouchEvent.idle

        try {
            val currentTime = System.currentTimeMillis()
            val problemDir = getProblemDir(currentTime)


            val path = File(problemDir, "image.jpg")
            val thumb = File(problemDir, "thumb.jpg")
            val color = String.format("#%06X", 0xFFFFFF and options.color)
            val holderList = ArrayList<Holder>()

            for (h in holderBoxList) {
                holderList.add(h.convert(scale))
            }

            FileUtil.copy(File(bouldering.path), path)
            val bouldering = Bouldering(path.absolutePath, thumb.absolutePath, color, currentTime, currentTime, holderList, getOrientation(options.bound.width(), options.bound.height()))
            val thumbnail = imageGenerator.createThumbnail(bouldering)
            FileUtil.store(thumbnail, thumb)

            return bouldering
        } catch (e: IOException) {
            throw BoulderingException("Can not create Boulder")
        }
    }

    private fun getProblemDir(time: Long): File {
        val problemDir = File(context.filesDir, time.toString())

        if (!problemDir.exists()) {
            problemDir.mkdir()
        }

        return problemDir
    }

    private fun getOrientation(width: Int, height: Int): Int {
        return if (width > height) {
            Orientation.ORIENTATION_LAND
        } else if (width < height) {
            Orientation.ORIENTATION_PORT
        } else {
            Orientation.ORIENTATION_SQUARE
        }
    }

    fun setIsViewer(toViewer: Boolean) {
        isViewer = toViewer
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        onClickListener = l
    }

    fun setOnProblemListener(listener: OnProblemListener) {
        onProblemListener = listener
    }

    fun setOnSelectedChangeListener(action: (HolderBox?) -> Unit) {
        onSelectedChangeListener = object : OnSelectedChangeListener {
            override fun onSelectedChange(selectedHolder: HolderBox?) {
                action(selectedHolder)
            }
        }
    }
}
