package com.duelodetrazos.ui.room

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.PlayerManager
import com.duelodetrazos.databinding.ActivityCreateRoomBinding
import com.duelodetrazos.ui.game.GameActivity
import com.parse.ParseObject
import com.parse.SaveCallback
import kotlin.random.Random

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerate.setOnClickListener {
            val code = generateRoomCode()
            binding.txtRoomCode.text = code

            val playerId = PlayerManager.getPlayerId(this)
            saveRoomToServer(code, playerId)
        }
    }

    /** Genera código tipo AB12 **/
    private fun generateRoomCode(): String {
        val letters = ('A'..'Z').random().toString() + ('A'..'Z').random()
        val numbers = Random.nextInt(10, 99).toString()
        return letters + numbers
    }

    /** Guarda la sala en Back4App (player1 host) **/
    private fun saveRoomToServer(code: String, player1Id: String) {
        val room = ParseObject("Room")

        room.put("code", code)
        room.put("status", "waiting")   // esperando jugador 2
        room.put("player1Id", player1Id)
        room.put("player2Id", "")
        room.put("player1Score", 0)
        room.put("player2Score", 0)
        room.put("currentRound", 0)
        room.put("maxRounds", 10)
        room.put("eventType", "")
        room.put("targetX", 0)
        room.put("targetY", 0)

        room.saveInBackground(SaveCallback { e ->
            if (e == null) {
                Toast.makeText(
                    this,
                    "Sala creada. Iniciando como Jugador 1...",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("roomId", code)
                intent.putExtra("isPlayer1", true)
                startActivity(intent)

            } else {
                Toast.makeText(
                    this,
                    "Error al crear la sala: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
