package com.example.imagereader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.imagereader.R
import com.example.imagereader.databinding.ActivityImageDetailBinding
import com.example.imagereader.databinding.ActivityMainBinding

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityImageDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageView: ImageView =binding.detailImageView
        val imageName: TextView = binding.detailImageName
        val imageId: TextView = binding.detailImageId

        val imagePath = intent.getStringExtra("IMAGE_PATH")
      //  val imageNameText = intent.getStringExtra("IMAGE_NAME")
        val imageIdText = intent.getIntExtra("IMAGE_ID", -1)

        Glide.with(this)
            .load(imagePath)
            .into(imageView)

        imageName.text = "Path: $imagePath"
        imageId.text = "ID: $imageIdText"
    }
}
