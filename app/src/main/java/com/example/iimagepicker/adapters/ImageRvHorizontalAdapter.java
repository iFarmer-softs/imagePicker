package com.example.iimagepicker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;Glide
import com.bumptech.glide.Glide;
import com.example.iimagepicker.databinding.UnitHomeBinding;
import com.example.iimagepicker.models.Image;

import java.util.ArrayList;

public class ImageRvHorizontalAdapter extends RecyclerView.Adapter<ImageRvHorizontalAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Image> images;
    public ImageRvHorizontalAdapter(@NonNull Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UnitHomeBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(images.get(position).getImagePath()).into(holder.binding.ivImages);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private UnitHomeBinding binding;
        public ViewHolder(@NonNull UnitHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
