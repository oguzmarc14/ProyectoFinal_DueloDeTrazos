package com.duelodetrazos.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityGameBinding
import com.duelodetrazos.ui.canvas.CircleCanvas
import com.duelodetrazos.ui.canvas.DrawingView

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    // Referencia global al DrawingView para poder limpiarlo
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Círculo guía
        val circleCanvas = CircleCanvas(this)
        binding.containerCircle.addView(circleCanvas)

        // Vista donde el usuario dibuja
        drawingView = DrawingView(this)
        binding.containerCanvas.addView(drawingView)

        // -------------------------------
        // BOTON LIMPIAR
        // -------------------------------
        binding.btnClear.setOnClickListener {
            drawingView.clear()
        }

        // -------------------------------
        // BOTON TERMINAR (próxima funcionalidad)
        // -------------------------------
        binding.btnFinish.setOnClickListener {
            // Aqui despues haremos que guarde el dibujo y pase a resultados
        }
    }
}
