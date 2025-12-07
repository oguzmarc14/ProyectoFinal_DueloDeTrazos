package com.duelodetrazos.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.random.Random

// Vista que dibuja un circulo objetivo en una posicion aleatoria
class ObjectiveCircleView(context: Context) : View(context) {

    // Radio del circulo
    private val radius = 80f

    // Posicion del circulo
    private var centerX = 0f
    private var centerY = 0f

    // Paint que dibuja el circulo
    private val circlePaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }

    // Listener que GameActivity usara cuando el jugador acierta
    var onHit: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar circulo
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    // Genera una nueva posicion aleatoria dentro del tamaño del contenedor
    fun randomizePosition(parentWidth: Int, parentHeight: Int) {
        centerX = Random.nextInt(radius.toInt(), parentWidth - radius.toInt()).toFloat()
        centerY = Random.nextInt(radius.toInt(), parentHeight - radius.toInt()).toFloat()
        invalidate()
    }

    // Detectar si el usuario toco dentro del circulo
    fun checkHit(x: Float, y: Float) {
        val dx = x - centerX
        val dy = y - centerY
        val distance = kotlin.math.sqrt(dx*dx + dy*dy)

        if (distance <= radius) {
            onHit?.invoke() // Avisar que acertó
        }
    }
}
