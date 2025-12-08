package com.duelodetrazos.ui.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.duelodetrazos.R
import com.duelodetrazos.databinding.ActivityRoomBinding

class RoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val albumList = listOf(
            AlbumItem("Crear sala", R.drawable.bad2),
            AlbumItem("Unirme a sala", R.drawable.nodal),
            AlbumItem("Modo de juego", R.drawable.jesus)
        )

        binding.rvAlbums.layoutManager = GridLayoutManager(this, 2)
        binding.rvAlbums.adapter = RoomAdapter(albumList)
    }
}
