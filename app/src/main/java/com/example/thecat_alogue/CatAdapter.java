package com.example.thecat_alogue;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.thecat_alogue.databinding.ItemCatBinding;

import java.util.Objects;

public class CatAdapter extends ListAdapter<CatImage, CatAdapter.ViewHolder> {

    public interface OnFavouriteClickListener {
        void onFavouriteClick(CatImage cat);
    }

    public interface OnCatClickListener {
        void onCatClick(CatImage cat);
    }

    private final OnFavouriteClickListener favouriteClickListener;
    private final OnCatClickListener catClickListener;

    public static final DiffUtil.ItemCallback<CatImage> DIFF_CALLBACK = new DiffUtil.ItemCallback<CatImage>() {
        @Override
        public boolean areItemsTheSame(@NonNull CatImage oldItem, @NonNull CatImage newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CatImage oldItem, @NonNull CatImage newItem) {
            return Objects.equals(oldItem.getUrl(), newItem.getUrl());
        }
    };

    public CatAdapter(OnFavouriteClickListener favouriteClickListener, OnCatClickListener catClickListener) {
        super(DIFF_CALLBACK);
        this.favouriteClickListener = favouriteClickListener;
        this.catClickListener = catClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCatBinding binding = ItemCatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatImage cat = getItem(position);
        if (cat == null) return;

        holder.binding.loadingSpinner.setVisibility(View.VISIBLE);

        Context context = holder.itemView.getContext();

        Glide.with(context)
                .load(cat.getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.binding.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.binding.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.binding.catImage);

        holder.binding.catImage.setOnClickListener(v -> {
            if (catClickListener != null) {
                catClickListener.onCatClick(cat);
            }
        });

        holder.binding.btnFavourite.setOnClickListener(v -> {
            if (favouriteClickListener != null) {
                favouriteClickListener.onFavouriteClick(cat);
            }
        });

        holder.binding.btnShare.setOnClickListener(v -> {
            String message = context.getString(R.string.share_cat_message, cat.getUrl());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)));
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCatBinding binding;

        public ViewHolder(@NonNull ItemCatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
