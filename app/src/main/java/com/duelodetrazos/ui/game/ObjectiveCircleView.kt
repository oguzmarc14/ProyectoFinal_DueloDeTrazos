package com.duelodetrazos.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.random.Random

class ObjectiveCircleView(context: Context) : View(context) {

    private val radius = 80f

    private var centerX = 0f
    private var centerY = 0f

    private val circlePaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }

    var onHit: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    fun randomizePosition(parentWidth: Int, parentHeight: Int) {
        centerX = Random.nextInt(radius.toInt(), parentWidth - radius.toInt()).toFloat()
        centerY = Random.nextInt(radius.toInt(), parentHeight - radius.toInt()).toFloat()
        invalidate()
    }

    fun checkHit(x: Float, y: Float) {
        val dx = x - centerX
        val dy = y - centerY
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

        if (distance <= radius) {
            onHit?.invoke()
        }
    }

    // 🔴 ESTA ES LA FUNCIÓN QUE TE FALTABA 🔥
    fun setPosition(x: Float, y: Float) {
        centerX = x
        centerY = y
        invalidate()
    }
}
