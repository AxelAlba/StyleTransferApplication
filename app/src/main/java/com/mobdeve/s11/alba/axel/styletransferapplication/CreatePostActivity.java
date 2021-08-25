package com.mobdeve.s11.alba.axel.styletransferapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class CreatePostActivity extends AppCompatActivity {
    ImageView originalImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("IMAGE_URL");

        originalImageView = findViewById(R.id.original_imageview);

        Log.d("DEBUGDANJOHN", imageURL);

        Bitmap bmImg = BitmapFactory.decodeFile(imageURL);
        originalImageView.setImageBitmap(bmImg);

    }
}