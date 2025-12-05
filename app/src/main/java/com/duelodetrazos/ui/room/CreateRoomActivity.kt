package com.duelodetrazos.ui.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityCreateRoomBinding
import kotlin.random.Random

// Pantalla donde genero un codigo de sala
class CreateRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializo el binding
        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener del boton para generar el codigo
        binding.btnGenerate.setOnClickListener {
            val code = generateRoomCode()
            binding.txtRoomCode.text = code
        }
    }

    // Funcion para generar codigos tipo AB12
    private fun generateRoomCode(): String {
        val letters = ('A'..'Z').random().toString() + ('A'..'Z').random()
        val numbers = Random.nextInt(10, 99).toString()
        return letters + numbers
    }
}
