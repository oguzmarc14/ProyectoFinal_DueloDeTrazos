package com.duelodetrazos.ui.game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityGameBinding
import com.duelodetrazos.network.LiveQueryManager
import com.duelodetrazos.ui.canvas.DrawingView
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import org.json.JSONObject
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    companion object {
        private const val POLLING_INTERVAL_MS = 100L
    }

    private lateinit var binding: ActivityGameBinding

    // --- Vistas ---
    private lateinit var drawingView: DrawingView
    private lateinit var objectiveView: ObjectiveCircleView
    private lateinit var explosionView: ParticleExplosionView

    // --- Datos de la Sala ---
    private var roomId: String = ""
    private var isPlayer1 = false

    // --- Lógica de Polling ---
    private val pollingHandler = Handler(Looper.getMainLooper())
    private var pollingRunnable: Runnable? = null
    private var lastSeenSpawnId: String? = null
    private var lastSeenHitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!getIntentData()) {
            finish()
            return
        }

        setupViews()
        setupGameInfo() // <-- NUEVA FUNCIÓN
        startGameWhenReady()
        setupButtons()
    }

    private fun getIntentData(): Boolean {
        roomId = intent.getStringExtra("roomId") ?: ""
        isPlayer1 = intent.getBooleanExtra("isPlayer1", false)
        if (roomId.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió el ID de la sala.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun setupViews() {
        drawingView = DrawingView(this)
        binding.containerCanvas.addView(drawingView)

        objectiveView = ObjectiveCircleView(this)
        binding.containerCanvas.addView(objectiveView)

        explosionView = ParticleExplosionView(this)
        binding.containerCanvas.addView(explosionView)

        objectiveView.onHit = { registerHit() }
    }

    private fun setupGameInfo() {
        binding.tvRoomCode.text = roomId
        binding.tvPlayer1Name.text = "Jugador 1"
        binding.tvPlayer2Name.text = "Jugador 2"
    }

    private fun startGameWhenReady() {
        val observer = binding.containerCanvas.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.containerCanvas.viewTreeObserver.removeOnGlobalLayoutListener(this)

                drawingView.onTouchPoint = { x, y -> objectiveView.checkHit(x, y) }

                if (isPlayer1) {
                    spawnNewObjective()
                    startPollingForHits()
                } else {
                    startPollingForSpawns()
                }
            }
        })
    }

    // --- LÓGICA DE POLLING ---

    private fun startPollingForSpawns() {
        pollingRunnable = Runnable {
            val query = ParseQuery.getQuery<ParseObject>("GameEvent")
            query.whereEqualTo("roomCode", roomId)
            query.whereEqualTo("type", "SPAWN")
            query.orderByDescending("createdAt")
            query.getFirstInBackground(object : GetCallback<ParseObject> {
                override fun done(obj: ParseObject?, e: ParseException?) {
                    if (e == null && obj != null) {
                        if (obj.objectId != lastSeenSpawnId) {
                            lastSeenSpawnId = obj.objectId
                            val payload = obj.getJSONObject("payload")
                            if (payload != null) {
                                val x = payload.getDouble("x").toFloat()
                                val y = payload.getDouble("y").toFloat()
                                objectiveView.setPosition(x, y)
                            }
                        }
                    }
                    pollingRunnable?.let { pollingHandler.postDelayed(it, POLLING_INTERVAL_MS) }
                }
            })
        }
        pollingRunnable?.let { pollingHandler.post(it) }
    }

    private fun startPollingForHits() {
        pollingRunnable = Runnable {
            val query = ParseQuery.getQuery<ParseObject>("GameEvent")
            query.whereEqualTo("roomCode", roomId)
            query.whereEqualTo("type", "HIT")
            query.orderByDescending("createdAt")
            query.getFirstInBackground(object : GetCallback<ParseObject> {
                override fun done(obj: ParseObject?, e: ParseException?) {
                    if (e == null && obj != null) {
                        if (obj.objectId != lastSeenHitId) {
                            lastSeenHitId = obj.objectId
                            spawnNewObjective()
                        }
                    }
                    pollingRunnable?.let { pollingHandler.postDelayed(it, POLLING_INTERVAL_MS) }
                }
            })
        }
        pollingRunnable?.let { pollingHandler.post(it) }
    }

    // --- LÓGICA DEL JUEGO ---

    private fun spawnNewObjective() {
        val width = binding.containerCanvas.width
        val height = binding.containerCanvas.height

        if (width == 0 || height == 0) return

        val horizontalMargin = (width * 0.20f).toInt()
        val verticalMargin = (height * 0.20f).toInt()

        val x = Random.nextInt(horizontalMargin, width - horizontalMargin).toFloat()
        val y = Random.nextInt(verticalMargin, height - verticalMargin).toFloat()

        objectiveView.setPosition(x, y)

        val data = JSONObject().apply { put("x", x); put("y", y) }
        LiveQueryManager.sendEvent(roomId, "SPAWN", data)
    }

    private fun registerHit() {
        val hitX = objectiveView.getCircleX()
        val hitY = objectiveView.getCircleY()
        val hitColor = objectiveView.getCircleColor()

        objectiveView.setPosition(-200f, -200f)
        explosionView.startExplosion(hitX, hitY, hitColor)

        val playerId = if (isPlayer1) "player1" else "player2"
        LiveQueryManager.sendEvent(roomId, "HIT", JSONObject().put("playerId", playerId))
    }

    private fun setupButtons() {
        binding.btnClear.setOnClickListener { drawingView.clear() }
        binding.btnFinish.setOnClickListener { /* Lógica futura */ }
    }

    override fun onDestroy() {
        super.onDestroy()
        pollingRunnable?.let { pollingHandler.removeCallbacks(it) }
    }
}
