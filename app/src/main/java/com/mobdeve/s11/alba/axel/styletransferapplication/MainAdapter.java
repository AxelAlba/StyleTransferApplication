package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<Post> posts;
    private FirestoreHelper firestoreHelper;

    public MainAdapter(ArrayList<Post> posts) {
        this.posts = posts;
        this.firestoreHelper = FirestoreHelper.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        // Inflate xml
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.template, parent, false);

        MyViewHolder mainViewHolder = new MyViewHolder(itemView);
        mainViewHolder.setLikeBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post tempPost = posts.get(mainViewHolder.getAdapterPosition());
                MainAdapter.this.firestoreHelper.addLike(tempPost.getFirestoreID());
            }
        });

        return mainViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.setHeader(this.posts.get(position).getUsername(), this.posts.get(position).getDatePosted());
        holder.setMainPost(this.posts.get(position).getImageURI());
        holder.setCaption(this.posts.get(position).getCaption(), this.posts.get(position).getDatePosted());
        holder.setNumLikes(this.posts.get(position).getNumLikes());
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }
}
