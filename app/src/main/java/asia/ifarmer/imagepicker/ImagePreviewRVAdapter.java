package asia.ifarmer.imagepicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import asia.ifarmer.imagepicker.databinding.RowGridImageBinding;

public class ImagePreviewRVAdapter extends RecyclerView.Adapter<ImagePreviewRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> images;
    private OnItemCLickedListener onItemCLickedListener;

    public ImagePreviewRVAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImagePreviewRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowGridImageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePreviewRVAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(images.get(position))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.img);
        holder.binding.cvCount.setVisibility(View.GONE);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("daf", "dasf w");
                if(onItemCLickedListener != null){
                    Log.e("daf", "dasf");
                    onItemCLickedListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setOnItemCLickedListener(OnItemCLickedListener onItemCLickedListener) {
        this.onItemCLickedListener = onItemCLickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowGridImageBinding binding;
        public ViewHolder(@NonNull RowGridImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    interface OnItemCLickedListener{
        void onItemClick(int position);
    }
}
