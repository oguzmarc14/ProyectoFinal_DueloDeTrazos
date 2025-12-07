package com.duelodetrazos.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityGameBinding
import com.duelodetrazos.ui.canvas.CircleCanvas
import com.duelodetrazos.ui.canvas.DrawingView

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    // Vista donde se dibuja
    private lateinit var drawingView: DrawingView

    // Circulo objetivo (punto que el usuario debe tocar)
    private lateinit var objectiveView: ObjectiveCircleView

    private var score = 0 // Contador de aciertos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // -------------------------------
        // 1. Circulo guia (tu canvas original)
        // -------------------------------
        val circleCanvas = CircleCanvas(this)
        binding.containerCircle.addView(circleCanvas)

        // -------------------------------
        // 2. Vista donde el usuario dibuja
        // -------------------------------
        drawingView = DrawingView(this)
        binding.containerCanvas.addView(drawingView)

        // -------------------------------
        // 3. Circulo objetivo del juego
        // -------------------------------
        objectiveView = ObjectiveCircleView(this)
        binding.containerCanvas.addView(objectiveView)

        // Enviar posicion de toque a la vista objetivo
        drawingView.onTouchPoint = { x, y ->
            objectiveView.checkHit(x, y)
        }

        // Cuando el usuario acierta
        objectiveView.onHit = {
            score++
            spawnNewObjective()
        }

        // Generar el primer objetivo una vez cargado el layout
        binding.containerCanvas.post {
            spawnNewObjective()
        }

        // -------------------------------
        // BOTON LIMPIAR
        // -------------------------------
        binding.btnClear.setOnClickListener {
            drawingView.clear()
        }

        // -------------------------------
        // BOTON TERMINAR
        // (aqui despues guardamos datos y enviamos resultados)
        // -------------------------------
        binding.btnFinish.setOnClickListener {
            // Proxima funcionalidad
        }
    }

    // Genera una nueva posicion aleatoria del objetivo
    private fun spawnNewObjective() {
        val width = binding.containerCanvas.width
        val height = binding.containerCanvas.height
        objectiveView.randomizePosition(width, height)
    }
}
