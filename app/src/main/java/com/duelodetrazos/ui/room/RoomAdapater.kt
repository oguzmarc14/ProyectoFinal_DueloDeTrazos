package com.duelodetrazos.ui.room

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duelodetrazos.databinding.ItemAlbumBinding
import com.duelodetrazos.ui.game.GameActivity

// Adaptador para mostrar la lista de albums en el RecyclerView
class RoomAdapter(
    private val items: List<AlbumItem>
) : RecyclerView.Adapter<RoomAdapter.AlbumViewHolder>() {

    // ViewHolder que guarda la referencia al binding del item
    class AlbumViewHolder(
        val binding: ItemAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        // Creo el binding para el layout de cada tarjeta
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAlbumBinding.inflate(inflater, parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        // Obtengo el item de la posicion actual
        val item = items[position]

        // Asigno texto e imagen a la tarjeta
        holder.binding.txtAlbumTitle.text = item.title
        holder.binding.imgAlbum.setImageResource(item.imageRes)

        // Evento click para navegar a cada Activity
        holder.binding.root.setOnClickListener {
            val context = holder.itemView.context

            when (item.title) {

                "Modo de juego" -> {
                    // Navega a la pantalla del juego
                    val intent = Intent(context, GameActivity::class.java)
                    context.startActivity(intent)
                }

                "Historial" -> {
                    // Aqui despues agrego la pantalla de historial
                    // val intent = Intent(context, HistoryActivity::class.java)
                    // context.startActivity(intent)
                }

                "Resultados" -> {
                    // Aqui despues agrego la pantalla de resultados
                    // val intent = Intent(context, ResultsActivity::class.java)
                    // context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
