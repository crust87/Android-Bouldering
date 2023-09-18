package com.kayadami.bouldering.editor

import android.graphics.*
import androidx.exifinterface.media.ExifInterface
import android.view.WindowManager
import com.kayadami.bouldering.data.type.Bouldering
import java.io.FileInputStream
import java.io.InputStream
import java.lang.NullPointerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageGenerator @Inject constructor (val windowManager: WindowManager) {

    private fun createImage(imagePath: String) : Bitmap {
        try {
            val imageStream: InputStream = FileInputStream(imagePath)
            val exif = ExifInterface(imagePath)
            val rotation: Float = when (exif.getAttribute(ExifInterface.TAG_ORIENTATION)) {
                "3" -> 180f
                "6" -> 90f
                "8" -> 270f
                else -> 0f
            }

            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565

            val matrix = Matrix()
            matrix.postRotate(rotation)

            val image = BitmapFactory.decodeStream(imageStream, null, options) ?: throw NullPointerException()
            return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } catch (e: Exception) {
            throw ImageGenerateException(e.message)
        }
    }

    fun createThumbnail(bouldering: Bouldering): Bitmap {
        val originImage = createImage(bouldering)

        val thumbnail: Bitmap
        if (originImage.width > originImage.height) {
            thumbnail = scaleCenterCrop(originImage, 800f, 600f)
        } else if (originImage.height > originImage.width) {
            thumbnail = scaleCenterCrop(originImage, 600f, 800f)
        } else {
            thumbnail = scaleCenterCrop(originImage, 600f, 600f)
        }

        return thumbnail
    }

    private fun scaleCenterCrop(source: Bitmap, newWidth: Float, newHeight: Float): Bitmap {
        val xScale = newWidth / source.width
        val yScale = newHeight / source.height
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * source.width
        val scaledHeight = scale * source.height
        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val paint = Paint()
        paint.isAntiAlias = true

        val dest = Bitmap.createBitmap(newWidth.toInt(), newHeight.toInt(), source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, paint)

        return dest
    }

    fun createImage(bouldering: Bouldering): Bitmap {
        val originImage = createImage(bouldering.path)
        val options = Options()
        options.bound = Rect(0, 0, originImage.width, originImage.height)
        val shareImage = Bitmap.createBitmap(options.bound.width(), options.bound.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(shareImage)

        val mImagePaint = Paint()
        mImagePaint.isFilterBitmap = true
        val mMask = Mask(options)

        canvas.drawBitmap(originImage, null, options.bound, mImagePaint)
        mMask.draw(canvas, bouldering.holderList)

        val scale = getScale(originImage.width.toFloat(), originImage.height.toFloat())
        val specialLineGap = EditorConfigurations.SPECIAL_LINE_GAP / scale
        val paint = with(Paint()) {
            color = Color.parseColor(bouldering.color)
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = EditorConfigurations.DEFAULT_LINE_WIDTH / 2f / scale

            this
        }

        for (h in bouldering.holderList) {
            canvas.drawCircle(h.x, h.y, h.radius, paint)

            if (h.isSpecial) {
                canvas.drawCircle(h.x, h.y, h.radius - specialLineGap, paint)
            }
        }

        return shareImage
    }

    private fun getScale(width: Float, height: Float): Float {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)

        val scaleX: Float
        val scaleY: Float

        if (width < height) {
            scaleX = size.x / width
            scaleY = size.y / height
        } else {
            scaleX = size.x / height
            scaleY = size.y / width
        }

        return if (scaleX > scaleY) scaleY else scaleX
    }
}
