package com.duelodetrazos.ui.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityCreateRoomBinding
import com.duelodetrazos.network.ServerManager
import com.duelodetrazos.ui.game.GameActivity
import kotlin.random.Random

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRoomBinding
    private val serverManager = ServerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerate.setOnClickListener {
            val code = generateRoomCode()
            binding.txtRoomCode.text = code

            serverManager.startServer {
                runOnUiThread {
                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun generateRoomCode(): String {
        val letters = ('A'..'Z').random().toString() + ('A'..'Z').random()
        val numbers = Random.nextInt(10, 99).toString()
        return letters + numbers
    }

    override fun onDestroy() {
        super.onDestroy()
        serverManager.close()
    }
}

