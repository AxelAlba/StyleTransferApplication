package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView ivRoundPlaceholder;
    private ImageView ivMainPost;
    private TextView tvUsername;
    private TextView tvLocation;
    private TextView tvCaptionUsername;
    private TextView tvCaption;
    private TextView tvDatePosted;
    private FloatingActionButton fabLike;
    private boolean liked;
    private Context context;

    public MyViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.context = itemView.getContext();

        // Header
        this.ivRoundPlaceholder = itemView.findViewById(R.id.iv_round_placeholder);
        this.tvUsername = itemView.findViewById(R.id.tv_username);
        this.tvDatePosted = itemView.findViewById(R.id.tv_date);
        // Image
        this.ivMainPost = itemView.findViewById(R.id.iv_main_post);
        // Caption Section
        this.tvCaption = itemView.findViewById(R.id.tv_caption_text);
        this.fabLike = itemView.findViewById(R.id.fab_like);
        this.fabLike.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        this.liked = !this.liked;
        this.setLike(this.liked);
    }
    // old
//    public void setHeader(int profileImage, String username, String location) {
//        this.ivRoundPlaceholder.setImageResource(profileImage);
//        this.tvUsername.setText(username);
//        if (location != null) {
//            this.tvLocation.setText(location);
//        } else {
//            this.tvLocation.setVisibility(View.GONE);
//        }
//    }

    public void setHeader(String username, String date) {
        this.tvUsername.setText(username);
        if (date != null) {
            this.tvDatePosted.setText(date);
        } else {
            this.tvDatePosted.setVisibility(View.GONE);
        }
    }
//Glide.with(this).load(URL_TO_IMAGE).into(imageView);

//    public void setMainPost(int mainPostImage) {
//        this.ivMainPost.setImageResource(mainPostImage);
//    }

    public void setMainPost(String url) {
        Glide.with(this.context).load(url).into(this.ivMainPost);
    }

    public void setCaption(String caption, String datePosted) {
        if (caption != null) {
            this.tvCaption.setText(caption);
        } else {
            this.tvCaption.setVisibility(View.GONE);
        }

        this.tvDatePosted.setText(datePosted);
    }

    public void setLike(boolean liked) {
        // Save to local variable
        this.liked = liked;

        if (liked) {
            this.fabLike.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.heart_on));
        } else {
            this.fabLike.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.heart_off));
        }
    }



}
