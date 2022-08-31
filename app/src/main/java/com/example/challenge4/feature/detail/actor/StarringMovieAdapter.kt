package com.example.challenge4.feature.detail.actor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.common.Constant
import com.example.challenge4.databinding.ItemStarringMovieBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.model.Cast

class StarringMovieAdapter : RecyclerView.Adapter<StarringMovieAdapter.ViewHolder>() {

    private var movieList = listOf<Cast>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStarringMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.voteAverage.text = String.format("%.1f", movieList[position].voteAverage)
        holder.movieName.text = movieList[position].title

        holder.character.text = movieList[position].character.ifEmpty {
            holder.itemView.resources.getString(R.string.empty_text)
        }

        holder.voteAverage.run {
            if (movieList[position].voteAverage >= Constant.GOLD_RATE) {
                setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gold))
            } else if (movieList[position].voteAverage >= Constant.SILVER_RATE) {
                setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.silver))
            } else if (movieList[position].voteAverage > 0) {
                setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.bronze))
            } else if (movieList[position].voteAverage == 0.0) {
                setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.extra_grey))
                text = "-"
            }
        }

        holder.adult.run {
            if (movieList[position].adult) {
                show()
            } else {
                hide()
            }
        }

        Glide.with(holder.itemView.context)
            .load(Constant.IMAGE_PATH_PREFIX + movieList[position].posterPath)
            .placeholder(R.drawable.placeholder_movie)
            .into(holder.moviePoster)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMovieList(movies: List<Cast>) {
        movieList = movies
        notifyDataSetChanged()
    }

    override fun getItemCount() = movieList.size

    inner class ViewHolder(binding: ItemStarringMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        val moviePoster: ImageView = binding.moviePoster
        val movieName: TextView = binding.movieName
        val character: TextView = binding.character
        val voteAverage: TextView = binding.voteAverage
        val adult: TextView = binding.adult
    }
}
