package com.mobdeve.s11.alba.axel.styletransferapplication

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


private const val TAG = "CreatePostActivity"

@ExperimentalCoroutinesApi
@Suppress("NAME_SHADOWING", "DEPRECATION")
class CreatePostActivity : AppCompatActivity(), StyleFragment.OnListFragmentInteractionListener {
    private var isRunningModel = false
    private val stylesFragment: StyleFragment = StyleFragment()
    private var selectedStyle: String = ""

    private lateinit var viewModel: MLExecutionViewModel
    private lateinit var originalImageView: ImageView
    private lateinit var styleImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var postBtn: Button
    private lateinit var etCaption: EditText

    private var lastSavedFile = ""
    private var styledImagePath = ""
    private lateinit var styleTransferModelExecutor: StyleTransferModelExecutor
    private val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private lateinit var signInAccount: GoogleSignInAccount

    @ExperimentalCoroutinesApi
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        originalImageView = findViewById(R.id.original_imageview)
        styleImageView = findViewById(R.id.style_imageview)
        progressBar = findViewById(R.id.progress_circular)
        etCaption = findViewById(R.id.et_caption)

        // google signIn
        signInAccount = GoogleSignIn.getLastSignedInAccount(this)

        val intent = intent
        lastSavedFile = intent.getStringExtra("IMAGE_URL").toString()

        Log.d(TAG, lastSavedFile)

        viewModel = AndroidViewModelFactory(application).create(MLExecutionViewModel::class.java)
        viewModel.styledBitmap.observe(
          this,
          Observer { resultImage ->
            if (resultImage != null) {
              updateUIWithResults(resultImage)
                styledImagePath = saveBitmapToStorage(resultImage.styledImage)
                Log.d(TAG, styledImagePath)
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
        postBtn.setOnClickListener() {
            if (TextUtils.isEmpty(styledImagePath)) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Paint Your Image!")
                alertDialog.setMessage("Hi there! Please set the style of your image before posting.")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                alertDialog.show()
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                val storageHelper = StorageHelper.getInstance()

                // Create Post object
                val df: DateFormat = SimpleDateFormat("dd MMMM yyyy")
                val strDate: String = df.format(Date())
                val timestamp = Timestamp(System.currentTimeMillis()).getTime()
                val post = Post(
                    signInAccount.getDisplayName(),
                    etCaption.getText().toString(),
                    strDate,
                    0,
                    false,
                    timestamp
                )

                // Upload Post
                storageHelper.addPost(styledImagePath, post)
                startActivity(intent)
            }
        }
    }

    private fun updateUIWithResults(modelExecutionResult: ModelExecutionResult) {
        progressBar.visibility = View.INVISIBLE
        setImageView(originalImageView, modelExecutionResult.styledImage)
    }

    private fun saveBitmapToStorage(image: Bitmap): String {
        var filename= "";
        try {
            filename = "styledImage.jpg"
            val fileOutputStream: FileOutputStream =
                this.openFileOutput(filename, Context.MODE_PRIVATE)
            val status = image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            Log.d(TAG, "saved successfully " + getFilesDir().toPath())
            Log.d(TAG, "saved successfully " + getFilesDir().listFiles().joinToString(" "))
            fileOutputStream.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "" + getFilesDir().toPath() + "/" + filename
    }

//    private fun SaveImage(finalBitmap: Bitmap) {
//        val root = Environment.getExternalStorageDirectory().absolutePath
//        val myDir = File("$root/saved_images")
//        myDir.mkdirs()
//        val fname = "Image-" + "test" + ".jpg"
//        val file = File(myDir, fname)
//        if (file.exists()) file.delete()
//        try {
//            val out: FileOutputStream = FileOutputStream(file)
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//            out.flush()
//            out.close()
//            Log.d("SaveImage", "success" + file.exists())
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }

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

    override val DateTimeFormatter: Any
        get() = TODO("Not yet implemented")

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