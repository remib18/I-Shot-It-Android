package com.example.ishotit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ishotit.BackendConnector.Picture;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final List<Picture.PictureResponse> pictures;

    public ImageAdapter(List<Picture.PictureResponse> pictures) {
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picture.PictureResponse picture = pictures.get(position);
        Picasso.get().load(picture.picturePath).into(holder.imageView);
        holder.usernameTextView.setText(picture.userId);
        holder.locationTextView.setText(picture.locationName);
        holder.dateTextView.setText(picture.date.toString());
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView usernameTextView;
        TextView locationTextView;
        TextView dateTextView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            locationTextView = itemView.findViewById(R.id.location_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }
}