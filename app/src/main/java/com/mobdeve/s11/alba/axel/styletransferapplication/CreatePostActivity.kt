package com.mobdeve.s11.alba.axel.styletransferapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "CreatePostActivity"

class CreatePostActivity : AppCompatActivity() {
    private lateinit var originalImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        val intent = intent
        val imageURL = intent.getStringExtra("IMAGE_URL")
        originalImageView = findViewById(R.id.original_imageview)
        Log.d(TAG, imageURL!!)
        val bmImg = BitmapFactory.decodeFile(imageURL)
        with(originalImageView) {
            this!!.setImageBitmap(bmImg)
        }
    }
}