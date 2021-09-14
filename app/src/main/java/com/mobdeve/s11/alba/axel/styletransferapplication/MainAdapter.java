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

    public MainAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        // Inflate xml
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.template, parent, false);

        MyViewHolder mainViewHolder = new MyViewHolder(view);

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        // Set res on entry
//        holder.setHeader(this.posts.get(position).getUserImageId(), this.posts.get(position).getUsername(), this.posts.get(position).getLocation());
//        holder.setMainPost(this.posts.get(position).getImageId());
//        holder.setCaption(this.posts.get(position).getUsername(), this.posts.get(position).getCaption(), this.posts.get(position).getDatePosted());
//        holder.setLike(this.posts.get(position).getLiked());
        holder.setHeader(this.posts.get(position).getUsername(), this.posts.get(position).getDatePosted());
        holder.setMainPost(this.posts.get(position).getImageURI());
        holder.setCaption(this.posts.get(position).getCaption(), this.posts.get(position).getDatePosted());
        holder.setLike(this.posts.get(position).getLiked());
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }
}
