package com.mobdeve.s11.alba.axel.styletransferapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.mobdeve.s11.alba.axel.styletransferapplication.camera.CameraFragment
import java.io.File
import java.nio.charset.Charset
import java.security.MessageDigest

// This is an arbitrary number we are using to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "MainActivity"

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

    private fun enableControls(enable: Boolean) {
        isRunningModel = !enable
        rerunButton.isEnabled = enable
        captureButton.isEnabled = enable
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

    // And update once new picture is taken?
    // Alternatively we can provide user an ability to select any of taken photos
    private fun getLastTakenPicture(): String {
        val directory = baseContext.filesDir // externalMediaDirs.first()
        var files =
            directory.listFiles()?.filter { file -> file.absolutePath.endsWith(".jpg") }?.sorted()
        if (files == null || files.isEmpty()) {
            Log.d(TAG, "there is no previous saved file")
            return ""
        }

        val file = files.last()
        Log.d(TAG, "lastsavedfile: " + file.absolutePath)
        return file.absolutePath
    }


    // this transformation is necessary to show the top square of the image as the model
    // will work on this part only, making the preview and the result show the same base
    class CropTop : BitmapTransformation() {
        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            return if (toTransform.width == outWidth && toTransform.height == outHeight) {
                toTransform
            } else ImageUtils.scaleBitmapAndKeepRatio(toTransform, outWidth, outHeight)
        }

        override fun equals(other: Any?): Boolean {
            return other is CropTop
        }

        override fun hashCode(): Int {
            return ID.hashCode()
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(ID_BYTES)
        }

        companion object {
            private const val ID = "org.tensorflow.lite.examples.styletransfer.CropTop"
            private val ID_BYTES = ID.toByteArray(Charset.forName("UTF-8"))
        }
    }
}
