package com.example.aashish.instasaverapp.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.aashish.instasaverapp.R;
import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.listener.OnImageClickListener;
import com.example.aashish.instasaverapp.widget.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterImages extends RecyclerView.Adapter<AdapterImages.ViewHolder> {

    List<ImageData> data;
    OnImageClickListener onImageClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        CustomTextView imagePlay;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            imagePlay = (CustomTextView) view.findViewById(R.id.image_play);
            imagePlay.setVisibility(View.GONE);
        }
    }

    public AdapterImages(List<ImageData> data, OnImageClickListener onImageClickListener) {
        this.data = data;
        this.onImageClickListener = onImageClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ImageData imageData = data.get(position);
        if (imageData.is_Video) {
            holder.imagePlay.setVisibility(View.VISIBLE);
        }
        holder.image.setVisibility(View.VISIBLE);
        Picasso.get().load(imageData.url).fit().centerCrop().into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onImageClickListener != null)
                    onImageClickListener.onImageClick(imageData);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}