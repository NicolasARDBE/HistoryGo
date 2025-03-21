package com.example.historygo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.historygo.Model.Experience
import com.example.historygo.R

class ExperienceAdapter(private val experiences: List<Experience>) :
    RecyclerView.Adapter<ExperienceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val locationImage: ImageView = view.findViewById(R.id.LocationImage)
        val experienceTitle: TextView = view.findViewById(R.id.ExperienceTitle)
        val description: TextView = view.findViewById(R.id.tvDescripcion)
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
        holder.locationImage.setImageResource(experience.imageResId)
    }

    override fun getItemCount(): Int = experiences.size
}