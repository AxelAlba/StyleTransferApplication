package com.mobdeve.s11.alba.axel.styletransferapplication

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.concurrent.Executors

private const val TAG = "CreatePostActivity"

@ExperimentalCoroutinesApi
@Suppress("NAME_SHADOWING")
class CreatePostActivity : AppCompatActivity(), StyleFragment.OnListFragmentInteractionListener {
    private var isRunningModel = false
    private val stylesFragment: StyleFragment = StyleFragment()
    private var selectedStyle: String = ""

    private lateinit var viewModel: MLExecutionViewModel
    private lateinit var originalImageView: ImageView
    private lateinit var styleImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var postBtn: Button

    private var lastSavedFile = ""
    private lateinit var styleTransferModelExecutor: StyleTransferModelExecutor
    private val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        originalImageView = findViewById(R.id.original_imageview)
        styleImageView = findViewById(R.id.style_imageview)
        progressBar = findViewById(R.id.progress_circular)


        val intent = intent
        lastSavedFile = intent.getStringExtra("IMAGE_URL").toString()

        Log.d(TAG, lastSavedFile)

        viewModel = AndroidViewModelFactory(application).create(MLExecutionViewModel::class.java)
        viewModel.styledBitmap.observe(
          this,
          Observer { resultImage ->
            if (resultImage != null) {
              updateUIWithResults(resultImage)
            }
          }
        )

        mainScope.async(inferenceThread) {
          styleTransferModelExecutor = StyleTransferModelExecutor(this@CreatePostActivity)
          Log.d(TAG, "Executor created")
        }

        styleImageView.setOnClickListener {
          if (!isRunningModel) {
            stylesFragment.show(supportFragmentManager, "StylesFragment")
          }
        }

        progressBar.visibility = View.INVISIBLE
        //lastSavedFile = getLastTakenPicture()
        setImageView(originalImageView, lastSavedFile)
        Log.d(TAG, "finished onCreate!!")

        postBtn = findViewById(R.id.post_button)
        postBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUIWithResults(modelExecutionResult: ModelExecutionResult) {
        progressBar.visibility = View.INVISIBLE
        setImageView(originalImageView, modelExecutionResult.styledImage)
    }

    private fun setImageView(imageView: ImageView, image: Bitmap) {
        Glide.with(baseContext)
            .load(image)
            .override(512, 512)
            .fitCenter()
            .into(imageView)
    }

    private fun setImageView(imageView: ImageView, imagePath: String) {
        Glide.with(baseContext)
            .asBitmap()
            .load(imagePath)
            .override(512, 512)
            .apply(RequestOptions().transform(CropTop()))
            .into(imageView)
    }

    override fun onListFragmentInteraction(item: String) {
        Log.d(TAG, item)
        selectedStyle = item
        stylesFragment.dismiss()

        startRunningModel()
    }

    private fun getUriFromAssetThumb(thumb: String): String {
        return "file:///android_asset/thumbnails/$thumb"
    }

    private fun startRunningModel() {
        if (!isRunningModel && lastSavedFile.isNotEmpty() && selectedStyle.isNotEmpty()) {
            setImageView(styleImageView, getUriFromAssetThumb(selectedStyle))
            progressBar.visibility = View.VISIBLE
            viewModel.onApplyStyle(
                baseContext, lastSavedFile, selectedStyle, styleTransferModelExecutor,
                inferenceThread
            )
        } else {
            Toast.makeText(this, "Previous Model still running", Toast.LENGTH_SHORT).show()
        }
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