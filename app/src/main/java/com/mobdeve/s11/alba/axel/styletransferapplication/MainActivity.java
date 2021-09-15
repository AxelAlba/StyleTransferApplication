package com.mobdeve.s11.alba.axel.styletransferapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;

import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileOutputStream;
import java.util.ArrayList;
import android.widget.ImageView;

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
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable night mode

        // Google Sign in
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            // see if user can be find
            Log.d(TAG, "Display name: "+signInAccount.getDisplayName());
            Log.d(TAG, "email: "+ signInAccount.getEmail());
        }

        this.firestoreHelper = FirestoreHelper.getInstance();


        // initialize recycler view
        this.initRecyclerView();

        imageView = (ImageView) findViewById(R.id.my_avatar);

        // FAB action
        btnStyleTransfer = findViewById(R.id.btn_style_transfer);
        btnStyleTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectImage(MainActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),LandingActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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


        this.firestoreHelper.readAllDataListen(new FirestoreHelper.readAllDataCallback() {
            @Override
            public void result(ArrayList<Post> data) {
                data.forEach(post -> {
                    Log.d("MAINACTIVITY", post.toString());
                });


                if (MainActivity.this.data != null) {
                    MainActivity.this.data.clear();
                    MainActivity.this.data.addAll(data);
                } else {
                    MainActivity.this.data = data;
                    MainActivity.this.myAdapter = new MainAdapter(MainActivity.this.data);
                    MainActivity.this.rvPosts.setAdapter(MainActivity.this.myAdapter);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Save state
                        recyclerViewState = rvPosts.getLayoutManager().onSaveInstanceState();

                        // Notify data update
                        MainActivity.this.myAdapter.notifyDataSetChanged();

                        // Restore state
                        rvPosts.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
                });
            }
        });
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