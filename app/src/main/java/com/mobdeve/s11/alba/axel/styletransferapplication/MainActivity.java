package com.mobdeve.s11.alba.axel.styletransferapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


//private String TAG = "MainActivity";

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private RecyclerView.LayoutManager myManager;
    private MainAdapter myAdapter;
    private ArrayList<Post> data;
    private boolean isFavorite;
    private FloatingActionButton btnStyleTransfer;

//    private lateinit var cameraFragment: CameraFragment
//    private var lensFacing = CameraCharacteristics.LENS_FACING_FRONT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable night mode

        this.initRecyclerView();
        btnStyleTransfer = findViewById(R.id.btn_style_transfer);

        btnStyleTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        // Get reference to RecyclerView
        this.rvPosts = findViewById(R.id.rv_posts);

        // Initialize LayoutManager
        this.myManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvPosts.setLayoutManager(this.myManager);

        // Initialize Adapter
        data = DataHelper.initializeData();
        this.myAdapter = new MainAdapter(this.data);

        this.rvPosts.setAdapter(this.myAdapter);
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