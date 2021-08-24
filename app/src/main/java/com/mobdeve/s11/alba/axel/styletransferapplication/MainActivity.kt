package com.mobdeve.s11.alba.axel.styletransferapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStyleTransfer = findViewById<Button>(R.id.btn_style_transfer)
            btnStyleTransfer.setOnClickListener{
                val intent = Intent(this, StyleTransferActivity::class.java)
                startActivity(intent)
            }
    }
}