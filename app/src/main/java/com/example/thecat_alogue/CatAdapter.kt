package com.example.thecat_alogue

import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.thecat_alogue.databinding.ItemCatBinding

class CatAdapter @JvmOverloads constructor(
    private val catClickListener: (CatImage) -> Unit,
    private val downloadClickListener: ((CatImage) -> Unit)? = null
) : ListAdapter<CatImage, CatAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = getItem(position) ?: return
        val context = holder.itemView.context

        holder.binding.loadingSpinner.visibility = View.VISIBLE

        Glide.with(context)
            .load(cat.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingSpinner.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingSpinner.visibility = View.GONE
                    return false
                }
            })
            .into(holder.binding.catImage)

        holder.binding.catImage.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            catClickListener(cat)
        }

        holder.binding.btnDownload.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            if (downloadClickListener != null) {
                downloadClickListener.invoke(cat)
            } else {
                CatUtils.downloadImage(context, cat.url, cat.id)
            }
        }

        holder.binding.btnShare.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            CatUtils.shareImageWithFileProvider(context, cat.url)
        }
    }

    class ViewHolder(val binding: ItemCatBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatImage>() {
            override fun areItemsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }
}
