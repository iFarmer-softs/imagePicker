package com.example.iimagepicker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.iimagepicker.R;
import com.example.iimagepicker.views.MainActivity;
import com.example.iimagepicker.databinding.RowGridImageBinding;
import com.example.iimagepicker.models.Image;

import java.util.ArrayList;

public class ImageRvGridAdapter extends RecyclerView.Adapter<ImageRvGridAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Image> images;

    public ImageRvGridAdapter(@NonNull Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowGridImageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(images.get(position).getImagePath())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_gallery2)
                .into(holder.binding.img);
        if(MainActivity.selectedImages.containsKey(images.get(position).getImagePath())){
            holder.binding.cvCount.setVisibility(View.VISIBLE);
            holder.binding.tvCount.setText(MainActivity.selectedImages.get(images.get(position).getImagePath()));
        }else {
            holder.binding.cvCount.setVisibility(View.GONE);
        }



        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images.get(position).setSelected(!images.get(position).isSelected());
                if(!images.get(position).isSelected()){
                    MainActivity.selectedImages.remove(images.get(position).getImagePath());
                }else {
                    MainActivity.selectedImages.put(images.get(position).getImagePath(), "");
                }
                //images.get(position).setCountPosition(countPosition);
                int count = 0;
                for (String s : MainActivity.selectedImages.keySet()) {
                    MainActivity.selectedImages.put(s, (++count) + "");
                }

                for (String s : MainActivity.selectedImages.keySet()) {
                    Log.e("log", MainActivity.selectedImages.get(s) + " " + s);
                }
                //Log.e("size", countIndex.size() + "");
                //notifyItemRangeChanged(0, images.size());
                notifyDataSetChanged();
                ((MainActivity) context).setImageCount();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private RowGridImageBinding binding;
        public ViewHolder(@NonNull RowGridImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
