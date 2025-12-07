package com.duelodetrazos.ui.room

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.PlayerManager
import com.duelodetrazos.databinding.ActivityJoinRoomBinding
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJoin.setOnClickListener {
            val code = binding.edtCode.text.toString().uppercase().trim()

            if (code.length != 4) {
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            joinRoom(code)
        }
    }

    private fun joinRoom(code: String) {
        val query = ParseQuery.getQuery<ParseObject>("Room")
        query.whereEqualTo("code", code)
        query.whereEqualTo("status", "waiting")

        query.getFirstInBackground { room, e ->
            if (e != null || room == null) {
                Toast.makeText(this, "No existe una sala esperando", Toast.LENGTH_LONG).show()
                return@getFirstInBackground
            }

            val player2Id = PlayerManager.getPlayerId(this)

            room.put("player2Id", player2Id)
            room.put("status", "ready")

            room.saveInBackground(SaveCallback { err ->
                if (err == null) {
                    Toast.makeText(this, "Te uniste. Esperando inicio...", Toast.LENGTH_LONG).show()

                    // ⛔ No abrimos GameActivity aquí.
                    // La partida iniciará por LiveQuery cuando el servidor cambie el estado.
                } else {
                    Toast.makeText(this, "Error: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
