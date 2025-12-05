package com.duelodetrazos.ui.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.duelodetrazos.R
import com.duelodetrazos.databinding.ActivityRoomBinding

class RoomActivity : AppCompatActivity() {

    // Uso view binding para acceder al layout sin usar findViewById
    private lateinit var binding: ActivityRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializo el binding
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lista de pantallas que quiero mostrar como si fueran portadas de musica
        // Aqui puse nombres de ejemplo pero despues los cambiamos
        val albumList = listOf(
            AlbumItem("Crear sala", R.drawable.bad2),
            AlbumItem("Unirme a sala", R.drawable.nodal),
            AlbumItem("Modo de juego", R.drawable.jesus)
        )


        // Configuro el RecyclerView en formato grid de 2 columnas
        binding.rvAlbums.layoutManager = GridLayoutManager(this, 2)

        // Asigno el adaptador
        binding.rvAlbums.adapter = RoomAdapter(albumList)
    }
}
