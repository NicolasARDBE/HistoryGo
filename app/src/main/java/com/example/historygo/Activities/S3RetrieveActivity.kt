package com.example.historygo.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.historygo.R
import com.example.historygo.databinding.ActivityReviewManagementBinding
import com.example.historygo.databinding.ActivityS3RetrieveBinding

class S3RetrieveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityS3RetrieveBinding
    private val chorroImagePath: String = "https://historygo-multimedia.s3.us-east-2.amazonaws.com/chorro-quevedo-antiguo.jpg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityS3RetrieveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ReviewBtn.setOnClickListener(){
            val intent = Intent(this, RatingManagement::class.java)
            startActivity(intent)
        }

        Glide.with(this).load(chorroImagePath).into(binding.chorro)
    }
}