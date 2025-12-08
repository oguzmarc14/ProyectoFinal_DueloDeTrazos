package com.duelodetrazos.ui.game

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityGameBinding
import com.duelodetrazos.network.LiveQueryManager
import com.duelodetrazos.ui.canvas.CircleCanvas
import com.duelodetrazos.ui.canvas.DrawingView
import org.json.JSONObject
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var drawingView: DrawingView
    private lateinit var objectiveView: ObjectiveCircleView

    private var roomId: String = ""
    private var isPlayer1 = false

    private var scoreP1 = 0
    private var scoreP2 = 0
    private var round = 1
    private val maxRounds = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //----------------------------------------
        // 0. RECIBIR DATOS DE LA SALA
        //----------------------------------------
        roomId = intent.getStringExtra("roomId") ?: ""
        isPlayer1 = intent.getBooleanExtra("isPlayer1", false)

        if (roomId.isEmpty()) {
            Toast.makeText(this, "Error: roomId vacío", Toast.LENGTH_SHORT).show()
            finish()
        }

        //----------------------------------------
        // 1. Dibujar círculo guía
        //----------------------------------------
        val circleCanvas = CircleCanvas(this)
        binding.containerCircle.addView(circleCanvas)

        //----------------------------------------
        // 2. Vista donde se dibuja
        //----------------------------------------
        drawingView = DrawingView(this)
        binding.containerCanvas.addView(drawingView)

        //----------------------------------------
        // 3. Objetivo
        //----------------------------------------
        objectiveView = ObjectiveCircleView(this)
        binding.containerCanvas.addView(objectiveView)

        drawingView.onTouchPoint = { x, y ->
            objectiveView.checkHit(x, y)
        }

        //----------------------------------------
        // 4. ACIERTO LOCAL -> ENVIAR EVENTO HIT
        //----------------------------------------
        objectiveView.onHit = {
            sendHitEvent()
        }

        //----------------------------------------
        // 5. Conectarnos a LiveQuery
        //----------------------------------------
        setupLiveQuery()

        //----------------------------------------
        // 6. Si eres jugador 1, generas primer SPAWN
        //----------------------------------------
        binding.containerCanvas.post {
            if (isPlayer1) spawnObjective()
        }

        //----------------------------------------
        // BOTÓN LIMPIAR
        //----------------------------------------
        binding.btnClear.setOnClickListener {
            drawingView.clear()
        }

        //----------------------------------------
        // BOTÓN TERMINAR
        //----------------------------------------
        binding.btnFinish.setOnClickListener {
            endGame()
        }
    }

    // ---------------------------------------------------------
    // SPAWN LOCAL -> se manda a la nube (solo Player1)
    // ---------------------------------------------------------
    private fun spawnObjective() {
        val width = binding.containerCanvas.width
        val height = binding.containerCanvas.height

        val x = Random.nextInt(80, width - 80).toFloat()
        val y = Random.nextInt(80, height - 80).toFloat()

        // Enviar SPAWN al servidor
        val data = JSONObject().apply {
            put("x", x)
            put("y", y)
        }

        LiveQueryManager.sendEvent(roomId, "SPAWN", data)
    }

    // HIT LOCAL → enviar a ambos jugadores
    private fun sendHitEvent() {
        LiveQueryManager.sendEvent(roomId, "HIT", JSONObject())
    }

    // FIN DE PARTIDA
    private fun endGame() {
        LiveQueryManager.sendEvent(roomId, "END", JSONObject())
    }

    // ---------------------------------------------------------
    // LiveQuery → escuchar eventos
    // ---------------------------------------------------------
    private fun setupLiveQuery() {

        LiveQueryManager.onSpawn = { x, y ->
            runOnUiThread {
                objectiveView.setPosition(x, y)
            }
        }

        LiveQueryManager.onHit = {
            runOnUiThread {
                if (isPlayer1) scoreP1++ else scoreP2++
                updateScores()
                spawnObjective()   // el host genera nuevo spawn
            }
        }

        LiveQueryManager.onScoreUpdate = { p1, p2 ->
            runOnUiThread {
                scoreP1 = p1
                scoreP2 = p2
                updateScores()
            }
        }

        LiveQueryManager.onRoundUpdate = { newRound ->
            runOnUiThread {
                round = newRound
            }
        }

        LiveQueryManager.onGameEnd = {
            runOnUiThread {
                Toast.makeText(this, "La partida terminó", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        LiveQueryManager.connect(roomId)
    }

    private fun updateScores() {
        // Aquí luego agregamos marcador visual
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveQueryManager.disconnect()
    }
}
