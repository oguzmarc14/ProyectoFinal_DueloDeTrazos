package com.duelodetrazos.ui.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

// DrawingView es una vista personalizada donde el usuario puede dibujar
// Esta vista maneja el trazo, color y movimiento del dedo
class DrawingView(context: Context) : View(context) {

    // Path almacena la linea que el usuario dibuja
    private val drawPath = Path()

    // Paint define el estilo de la linea (color, grosor, etc)
    private val drawPaint = Paint().apply {
        color = Color.WHITE     // Color por defecto del pincel
        strokeWidth = 14f       // Grosor del pincel
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true      // Hace que el trazo se vea mas suave
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Dibujo la linea que el usuario ha trazado
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        // Dependiendo del tipo de toque, actualizo el path
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Inicio un nuevo trazo
                drawPath.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                // Continúo la linea conforme se mueve el dedo
                drawPath.lineTo(x, y)
            }
        }

        // Redibujo la pantalla
        invalidate()
        return true
    }

    // ----------------------------
    // Funcion para limpiar el dibujo
    // ----------------------------
    fun clear() {
        drawPath.reset() // Borra todo el trazo
        invalidate()     // Actualiza la pantalla
    }
}
