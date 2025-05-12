package com.example.historygo.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.historygo.Activities.SingleComentActivity
import com.example.historygo.Activities.UpdateComentActivity
import com.example.historygo.Model.ComentarioExperiencia
import com.example.historygo.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UpdateDeleteCommentAdapter(
    val comments: MutableList<ComentarioExperiencia>,
    private val onDeleteClick: (String, String) -> Unit
) : RecyclerView.Adapter<UpdateDeleteCommentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val comentario: TextView = view.findViewById(R.id.review)
        val tiempo: TextView = view.findViewById(R.id.tiempo)
        val trashIcon: ImageView = view.findViewById(R.id.trash)
        val pen: ImageView = view.findViewById(R.id.pen)

        val stars: List<ImageView> = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
    }

    private fun convertirFecha(tiempo: String): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(tiempo)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            zonedDateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            Log.e("FechaInvalida", "Error al parsear fecha: $tiempo")
            tiempo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.update_delete_comentario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.tiempo.text = convertirFecha(comment.timestamp)
        holder.comentario.text = comment.review

        val rating = comment.rating
        for (i in holder.stars.indices) {
            holder.stars[i].setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    if (i < rating) android.R.color.holo_orange_light else android.R.color.darker_gray
                )
            )
        }

        // Click en ícono de eliminar
        holder.trashIcon.setOnClickListener {
            onDeleteClick(comment.touristSpotId.toString(), comment.ratingId)
        }

        // Click en ícono de editar (pen)
        holder.pen.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpdateComentActivity::class.java)

            intent.putExtra("TOURIST_SPOT_ID", comment.touristSpotId.toString())
            intent.putExtra("RATING_ID", comment.ratingId)
            intent.putExtra("review", comment.review)
            intent.putExtra("rating", comment.rating?.toDouble() ?: 0.0)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = comments.size

    fun removeComment(ratingId: String) {
        val index = comments.indexOfFirst { it.ratingId.toString() == ratingId }
        if (index != -1) {
            comments.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
