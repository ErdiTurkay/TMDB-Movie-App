package com.example.challenge4.feature.detail.movie

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.common.Constant
import com.example.challenge4.databinding.ItemReviewBinding
import com.example.challenge4.model.Review

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    private var reviewList = listOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.reviewContent.text = reviewList[position].content

        holder.authorName.text = reviewList[position].authorDetails.name.ifEmpty {
            holder.itemView.resources.getString(R.string.no_name)
        }

        // Photo of the author is added to imageView.
        Glide.with(holder.itemView.context)
            .load(Constant.IMAGE_PATH_PREFIX + reviewList[position].authorDetails.avatarPath)
            .placeholder(R.drawable.placeholder_review)
            .fitCenter()
            .into(holder.reviewImage)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReviewList(reviews: List<Review>) {
        reviewList = reviews
        notifyDataSetChanged()
    }

    override fun getItemCount() = reviewList.size

    inner class ViewHolder(binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val reviewImage: ImageView = binding.reviewImage
        val reviewContent: TextView = binding.reviewContent
        val authorName: TextView = binding.authorName
    }
}
