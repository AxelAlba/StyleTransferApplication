package com.mobdeve.s11.alba.axel.styletransferapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;


//private String TAG = "MainActivity";

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private RecyclerView.LayoutManager myManager;
    private MainAdapter myAdapter;
    private ArrayList<Post> data;
    private boolean isFavorite;
    private FloatingActionButton btnStyleTransfer;
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

        // FAB action
        btnStyleTransfer = findViewById(R.id.btn_style_transfer);
        btnStyleTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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