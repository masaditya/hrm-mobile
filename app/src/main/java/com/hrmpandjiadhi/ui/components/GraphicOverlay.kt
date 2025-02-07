package com.hrmpandjiadhi.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector

open class GraphicOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()
    private val faceGraphics: MutableList<FaceGraphic> = mutableListOf()
    var mScale: Float? = null
    var mOffsetX: Float? = null
    var mOffsetY: Float? = null
    var cameraSelector: Int = CameraSelector.LENS_FACING_FRONT

    abstract class Graphic(val overlay: GraphicOverlay) {
        abstract fun draw(canvas: Canvas?)
    }

    fun setFaceRectangles(rectangles: List<RectF>) {
        synchronized(lock) {
            faceGraphics.clear() // Clear old graphics
            rectangles.forEach { rect ->
                val faceGraphic = FaceGraphic(this)
                faceGraphic.updateBoundingBox(rect)
                faceGraphics.add(faceGraphic) // Add the graphic for drawing
            }
        }
        postInvalidate() // Request to redraw the overlay
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (graphic in faceGraphics) {
                graphic.draw(canvas)
            }
        }
    }
    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }
    fun isFrontMode() = cameraSelector == CameraSelector.LENS_FACING_FRONT

    fun toggleSelector() {
        cameraSelector =
            if (cameraSelector == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
    }
}
