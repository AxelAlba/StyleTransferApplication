package com.mobdeve.s11.alba.axel.styletransferapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import android.util.Log;
import android.widget.ImageView;

import com.mobdeve.s11.alba.axel.styletransferapplication.ImageFilePath;

//private String TAG = "MainActivity";

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private RecyclerView.LayoutManager myManager;
    private MainAdapter myAdapter;
    private ArrayList<Post> data;
    private boolean isFavorite;
    private FloatingActionButton btnStyleTransfer;

    private FirestoreHelper db_helper;
    private int SELECT_PICTURE = 200;
    private ImageView imageView;
    private String TAG = "MainActivity";
    private FirestoreHelper firestoreHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable night mode

        this.firestoreHelper = FirestoreHelper.getInstance();
        // demo, read data


        // initialize recycler view
        this.initRecyclerView();

        imageView = (ImageView) findViewById(R.id.my_avatar);

        // FAB action
        btnStyleTransfer = findViewById(R.id.btn_style_transfer);
        btnStyleTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//                startActivity(intent);
                selectImage(MainActivity.this);
            }
        });
    }
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Import an image");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    imageView.setImageURI(selectedImageUri);

                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap image = drawable.getBitmap();

                    String filename= "";
                    try {
                        filename = Long.toHexString(Double.doubleToLongBits(Math.random())) + ".jpg";
                        FileOutputStream fileOutputStream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                        boolean status = image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        Log.d(TAG, "saved successfully " + getFilesDir().toPath());
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "This is the file path from gallery: " + "" + getFilesDir().toPath() + "/" + filename);

                    Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                    intent.putExtra("IMAGE_URL", "" + getFilesDir().toPath() + "/" + filename);
                    startActivity(intent);
                }
            }
        }
    }

    private void initRecyclerView() {
        // Get reference to RecyclerView
        this.rvPosts = findViewById(R.id.rv_posts);

        // Initialize LayoutManager
        this.myManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvPosts.setLayoutManager(this.myManager);

        // Initialize Adapter
        //data = DataHelper.initializeData();
//        this.firestoreHelper.readAllData(new FirestoreHelper.readAllDataCallback() {
//            @Override
//            public void result(ArrayList<Post> data) {
//                data.forEach(post -> {
//                    Log.d("MAINACTIVITY", post.toString());
//                });
//                MainActivity.this.data = data;
//                MainActivity.this.myAdapter = new MainAdapter(MainActivity.this.data);
//                MainActivity.this.rvPosts.setAdapter(MainActivity.this.myAdapter);
//            }
//        });

        this.firestoreHelper.readAllDataListen(new FirestoreHelper.readAllDataCallback() {
            @Override
            public void result(ArrayList<Post> data) {
                data.forEach(post -> {
                    Log.d("MAINACTIVITY", post.toString());
//                    if (MainActivity.this.data != null) {
//                        if (!MainActivity.this.data.contains(post)) {
//                            MainActivity.this.data.add(post);
//                            MainActivity.this.myAdapter.notifyItemInserted();
//                        }
//                    }
                });




                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("HEY", "runOnUiThread");
                        MainActivity.this.data = data;
                        MainActivity.this.myAdapter = new MainAdapter(MainActivity.this.data);
                        MainActivity.this.rvPosts.setAdapter(MainActivity.this.myAdapter);
                    }
                });

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (MainActivity.this.data != null) {
//                            MainActivity.this.data.clear();
//                            MainActivity.this.data.addAll(data);
//                            MainActivity.this.myAdapter.notifyDataSetChanged();
//                        }
//                    }
//                });

            }
        });


//        this.myAdapter = new MainAdapter(this.data);
//
//        this.rvPosts.setAdapter(this.myAdapter);
    }


    public void toggle_fab_like(View view) {
        FloatingActionButton fab = findViewById(R.id.fab_like);

        isFavorite = !isFavorite;

        if (isFavorite) {
            // State = ON or FAVORITE
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart_on));
        } else {
            // State = OFF or UNFAVORITE
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart_off));
        }
    }
}