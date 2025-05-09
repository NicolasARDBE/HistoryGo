package com.example.historygo.Adapters

import android.view.LayoutInflater
import com.example.historygo.R
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.historygo.Model.ComentarioExperiencia
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException



class CommentAdapter(private val comments: List<ComentarioExperiencia>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.nombre)
        val tiempo: TextView = view.findViewById(R.id.tiempo)
        val stars: List<ImageView> = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
    }

    private fun convertirFecha(userName: String): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(userName)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            zonedDateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            Log.e("FechaInvalida", "Error al parsear fecha: $userName")
            userName // Devuelve el original si hay error
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.userName.text = convertirFecha(comment.userName)
        holder.tiempo.text = comment.timestamp




        val rating = comment.review.toFloatOrNull()?.toInt() ?: 0
        Log.d("RatingCheck", "Rating convertido: $rating")

        for (i in 0 until holder.stars.size) {
            if (i < rating) {
                holder.stars[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_light))
            } else {
                holder.stars[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
            }
        }

    }

    override fun getItemCount(): Int = comments.size
}
