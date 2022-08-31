package com.example.challenge4.feature.detail.movie

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.common.Constant
import com.example.challenge4.databinding.ItemVideoBinding
import com.example.challenge4.model.Video

class VideoAdapter(var listener: VideoClickListener) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var videoList = listOf<Video>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoName.text = videoList[position].name

        // YouTube thumbnail of the video is added to imageView.
        Glide.with(holder.itemView.context)
            .load(Constant.YOUTUBE_THUMBNAIL_PREFIX + videoList[position].key + Constant.YOUTUBE_THUMBNAIL_SUFFIX)
            .placeholder(R.drawable.placeholder_movie)
            .into(holder.videoThumbnail)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setVideoList(videos: List<Video>) {
        videoList = videos
        notifyDataSetChanged()
    }

    override fun getItemCount() = videoList.size

    inner class ViewHolder(binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        val videoName: TextView = binding.videoName
        val videoThumbnail: ImageView = binding.videoThumbnail
        private val videoCard: CardView = binding.videoCard

        init {
            videoCard.setOnClickListener {
                listener.videoOnClick(videoList[bindingAdapterPosition])
            }
        }
    }
}

interface VideoClickListener {
    fun videoOnClick(video: Video)
}
