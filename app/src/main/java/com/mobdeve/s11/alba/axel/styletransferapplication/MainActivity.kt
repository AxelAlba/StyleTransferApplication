package com.mobdeve.s11.alba.axel.styletransferapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.mobdeve.s11.alba.axel.styletransferapplication.camera.CameraFragment

private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private lateinit var cameraFragment: CameraFragment
    private var lensFacing = CameraCharacteristics.LENS_FACING_FRONT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStyleTransfer = findViewById<Button>(R.id.btn_style_transfer)
            btnStyleTransfer.setOnClickListener{
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
    }


}