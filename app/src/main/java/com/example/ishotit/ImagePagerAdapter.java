package com.example.ishotit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private LayoutInflater inflater;

    public ImagePagerAdapter(Context context, List<String> imageUrls) {
        this.inflater = LayoutInflater.from(context);
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrls.get(position))
                .centerCrop()
                .into(holder.imageView);

        // Si c'est la dernière image, chargez également la première pour obtenir un effet de défilement continu
        if (position == getItemCount() - 1) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrls.get(0))
                    .centerCrop()
                    .into(holder.imageViewNext);
        } else {
            // Chargez l'image suivante avec des marges négatives pour l'effet de continuité
            Glide.with(holder.itemView.getContext())
                    .load(imageUrls.get(position + 1))
                    .centerCrop()
                    .into(holder.imageViewNext);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imageViewNext;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.currentImage);
            imageViewNext = itemView.findViewById(R.id.nextImage);
        }
    }
}