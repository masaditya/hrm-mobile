package com.hrmapps.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class FaceGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {
    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }
    private var boundingBox: RectF? = null

    fun updateBoundingBox(box: RectF) {
        boundingBox = box
        overlay.postInvalidate() // Request to redraw the overlay
    }

    override fun draw(canvas: Canvas?) {
        boundingBox?.let {
            canvas?.drawRect(it, paint)
        }
    }
}
