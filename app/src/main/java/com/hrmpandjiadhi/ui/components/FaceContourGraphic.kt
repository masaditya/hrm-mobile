package com.hrmpandjiadhi.ui.components

import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.google.mlkit.vision.face.Face
import kotlin.math.ceil

class FaceContourGraphic(
    overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.GREEN

        facePositionPaint = Paint().apply {
            color = selectedColor
            style = Paint.Style.FILL
        }

        boxPaint = Paint().apply {
            color = selectedColor
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }
    }
    fun calculateRect(height: Float, width: Float, boundingBoxT: Rect): RectF {

        // for land scape
        fun isLandScapeMode(): Boolean {
            return overlay.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        fun whenLandScapeModeWidth(): Float {
            return when(isLandScapeMode()) {
                true -> width
                false -> height
            }
        }

        fun whenLandScapeModeHeight(): Float {
            return when(isLandScapeMode()) {
                true -> height
                false -> width
            }
        }

        val scaleX = overlay.width.toFloat() / whenLandScapeModeWidth()
        val scaleY = overlay.height.toFloat() / whenLandScapeModeHeight()
        val scale = scaleX.coerceAtLeast(scaleY)
        overlay.mScale = scale

        // Calculate offset (we need to center the overlay on the target)
        val offsetX = (overlay.width.toFloat() - ceil(whenLandScapeModeWidth() * scale)) / 2.0f
        val offsetY = (overlay.height.toFloat() - ceil(whenLandScapeModeHeight() * scale)) / 2.0f

        overlay.mOffsetX = offsetX
        overlay.mOffsetY = offsetY

        val mappedBox = RectF().apply {
            left = boundingBoxT.right * scale + offsetX
            top = boundingBoxT.top * scale + offsetY
            right = boundingBoxT.left * scale + offsetX
            bottom = boundingBoxT.bottom * scale + offsetY
        }

        // for front mode
        if (overlay.isFrontMode()) {
            val centerX = overlay.width.toFloat() / 2
            mappedBox.apply {
                left = centerX + (centerX - left)
                right = centerX - (right - centerX)
            }
        }
        return mappedBox
    }

    override fun draw(canvas: Canvas?) {
        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )
        canvas?.drawRect(rect, boxPaint)
    }

    companion object {
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
