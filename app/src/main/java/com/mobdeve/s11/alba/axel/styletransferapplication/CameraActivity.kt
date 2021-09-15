package com.mobdeve.s11.alba.axel.styletransferapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mobdeve.s11.alba.axel.styletransferapplication.camera.CameraFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

// This is an arbitrary number we are using to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "CameraActivity"

class CameraActivity : AppCompatActivity(), CameraFragment.OnCaptureFinished {
    private var isRunningModel = false
    private lateinit var cameraFragment: CameraFragment
    private lateinit var viewFinder: FrameLayout
    private lateinit var rerunButton: Button
    private lateinit var captureButton: ImageButton
    private var lastSavedFile = ""


    private var lensFacing = CameraCharacteristics.LENS_FACING_FRONT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        viewFinder = findViewById(R.id.view_finder)
        captureButton = findViewById(R.id.capture_button)


        // Request camera permissions
        if (allPermissionsGranted()) {
            addCameraFragment()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupControls()
    }

    private fun setupControls() {
        captureButton.setOnClickListener {
            it.clearAnimation()
            cameraFragment.takePicture()
        }

        findViewById<ImageButton>(R.id.toggle_button).setOnClickListener {
            lensFacing = if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                CameraCharacteristics.LENS_FACING_FRONT
            } else {
                CameraCharacteristics.LENS_FACING_BACK
            }
            cameraFragment.setFacingCamera(lensFacing)
            addCameraFragment()
        }
    }

    private fun addCameraFragment() {
        cameraFragment = CameraFragment.newInstance()
        cameraFragment.setFacingCamera(lensFacing)
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.view_finder, cameraFragment)
            .commit()
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                addCameraFragment()
                viewFinder.post { setupControls() }
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkPermission(
            it, Process.myPid(), Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }


    @ExperimentalCoroutinesApi
    override fun onCaptureFinished(file: File) {
        val msg = "Photo capture succeeded: ${file.absolutePath}"
        Log.d(TAG, msg)

        lastSavedFile = file.absolutePath
        //setImageView(originalImageView, lastSavedFile)

        Log.d(TAG, lastSavedFile)

        val intent = Intent(this, CreatePostActivity::class.java).apply {
            putExtra("IMAGE_URL", lastSavedFile)
        }

        startActivity(intent)
    }

}
