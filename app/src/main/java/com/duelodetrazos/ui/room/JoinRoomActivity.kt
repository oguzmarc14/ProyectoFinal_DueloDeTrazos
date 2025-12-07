package com.duelodetrazos.ui.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duelodetrazos.databinding.ActivityJoinRoomBinding
import com.duelodetrazos.network.ClientManager
import com.duelodetrazos.ui.game.GameActivity

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJoin.setOnClickListener {
            val code = binding.edtCode.text.toString()

            val client = ClientManager("10.0.2.2")

            client.connect {
                runOnUiThread {
                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
