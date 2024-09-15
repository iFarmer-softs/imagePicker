package com.example.iimagepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;Glide
import com.bumptech.glide.Glide;
import com.example.iimagepicker.databinding.UnitHomeBinding;

import java.util.ArrayList;

public class ImageRvAdapter extends RecyclerView.Adapter<ImageRvAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;
    public ImageRvAdapter(@NonNull Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UnitHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(holder.binding.ivImages);
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
