package com.example.thecat_alogue;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Changed from Button
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {

    private List<CatImage> catList;
    private CatApiService apiService;
    private String apiKey;

    // Constructor
    public CatAdapter(List<CatImage> catList, CatApiService apiService, String apiKey) {
        this.catList = catList;
        this.apiService = apiService;
        this.apiKey = apiKey;
    }

    // 1. Helper to REPLACE data (Pull-to-Refresh)
    public void updateData(List<CatImage> newCats) {
        this.catList = newCats;
        notifyDataSetChanged();
    }

    // 2. Helper to APPEND data (Infinite Scroll)
    public void addData(List<CatImage> moreCats) {
        int startPos = this.catList.size();
        this.catList.addAll(moreCats);
        notifyItemRangeInserted(startPos, moreCats.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatImage cat = catList.get(position);

        // --- IMAGE LOADING ---
        holder.loadingSpinner.setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(cat.getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.catImage);

        // --- FAVOURITE BUTTON (Icon) ---
        holder.btnFavourite.setOnClickListener(v -> {
            FavouriteRequest req = new FavouriteRequest(cat.getId(), "user_123");
            apiService.addFavourite(apiKey, req).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Saved! ⭐", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Error saving", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // --- SHARE BUTTON (Icon) ---
        holder.btnShare.setOnClickListener(v -> {
            String message = "Check out this cat! 🐱\n" + cat.getUrl();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        ImageButton btnFavourite; // Updated type
        ImageButton btnShare;     // Updated type
        ProgressBar loadingSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catImage);
            btnFavourite = itemView.findViewById(R.id.btnFavourite);
            btnShare = itemView.findViewById(R.id.btnShare);
            loadingSpinner = itemView.findViewById(R.id.loadingSpinner);
        }
    }
}