package com.example.historygo.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.historygo.Adapters.ExperienceAdapter
import com.example.historygo.Model.Experience
import com.example.historygo.R

class ExpererienceMenuActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExperienceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_expererience_menu)

        recyclerView = findViewById(R.id.recyclerViewExperiences)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val experiences = listOf(
            Experience("Plaza Del Chorro De Quevedo", "Una colección impresionante de artefactos precolombinos.", R.drawable.chorro_quevedo),
        )

        adapter = ExperienceAdapter(experiences)
        recyclerView.adapter = adapter


    }






}