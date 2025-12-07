package com.duelodetrazos.ui.room

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.PlayerManager
import com.duelodetrazos.databinding.ActivityCreateRoomBinding
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

            // 🔹 Guardar la sala correctamente en Back4App
            saveRoomToServer(code, playerId)
        }
    }

    /** Genera un código tipo AB12 **/
    private fun generateRoomCode(): String {
        val letters = ('A'..'Z').random().toString() + ('A'..'Z').random()
        val numbers = Random.nextInt(10, 99).toString()
        return letters + numbers
    }

    /** Guarda la sala (CORRECTO según PDF) **/
    private fun saveRoomToServer(code: String, player1Id: String) {
        val room = ParseObject("Room")

        room.put("code", code)
        room.put("status", "waiting")     // Esperando jugador 2
        room.put("player1Id", player1Id)
        room.put("player2Id", "")
        room.put("player1Score", 0)
        room.put("player2Score", 0)
        room.put("currentRound", 0)
        room.put("maxRounds", 10)

        room.saveInBackground(SaveCallback { e ->
            if (e == null) {
                Toast.makeText(
                    this,
                    "Sala creada. Esperando a que otro jugador se una...",
                    Toast.LENGTH_LONG
                ).show()

                // ⛔ NO abrimos GameActivity aquí
                // ⛔ NO iniciamos servidores locales

            } else {
                Toast.makeText(
                    this,
                    "Error al crear sala: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
