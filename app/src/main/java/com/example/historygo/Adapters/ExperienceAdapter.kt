package com.example.historygo.Adapters

import android.view.LayoutInflater
import android.content.Intent
import android.view.View
import androidx.cardview.widget.CardView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.historygo.Model.Experience
import com.example.historygo.R
import com.example.historygo.Activities.SelectedExperience

class ExperienceAdapter(private val experiences: List<Experience>) :
    RecyclerView.Adapter<ExperienceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val locationImage: ImageView = view.findViewById(R.id.LocationImage)
        val experienceTitle: TextView = view.findViewById(R.id.ExperienceTitle)
        val description: TextView = view.findViewById(R.id.tvDescripcion)
        val cardView: CardView = view.findViewById(R.id.cardExperience)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.experience_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val experience = experiences[position]
        holder.experienceTitle.text = experience.title
        holder.description.text = experience.description

        // Usamos Glide para cargar la imagen desde la URL (S3)
        Glide.with(holder.itemView.context)
            .load("https://d3krfb04kdzji1.cloudfront.net/"+experience.imageUrl)  // `imageUrl` debe ser una URL válida
            .placeholder(R.drawable.carga)  // Imagen mientras se carga
            .error(R.drawable.error)  // Imagen en caso de error
            .into(holder.locationImage)

        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SelectedExperience::class.java)
            // En el putExtra poner el nombre/id de la experiencia seleccionada
            intent.putExtra("id", experience.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = experiences.size
}
