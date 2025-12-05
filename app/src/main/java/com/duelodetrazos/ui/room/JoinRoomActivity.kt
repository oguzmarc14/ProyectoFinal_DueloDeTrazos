package com.duelodetrazos.ui.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityJoinRoomBinding

// Pantalla donde escribo un codigo para unirme
class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accion del boton para unirse
        binding.btnJoin.setOnClickListener {
            val code = binding.edtCode.text.toString().uppercase()

            // Aqui mas adelante verifico si el codigo existe o no
        }
    }
}
