package com.example.challenge4.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.model.Movie

class MovieAdapter(
    var listener: MovieClickListener,
    var showNumber: Boolean,
    var isLarge: Boolean
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private var movieList = listOf<Movie>()
    private val listItem = 0
    private val gridItem = 1
    private var isLinear = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_small, parent, false)

        // The relevant Layout is selected according to the ViewType.
        if (viewType == gridItem) {
            if (isLarge) {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_large, parent, false)
            }
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_linear, parent, false)
        }

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.voteAverage.text = String.format("%.1f", movieList[position].voteAverage)
        holder.movieOverview.text = movieList[position].overview
        holder.originalLanguage.text = movieList[position].originalLanguage.uppercase()

        holder.movieName.text = if (showNumber) {
            "#${position + 1} ${movieList[position].title}"
        } else {
            movieList[position].title
        }

        holder.releaseDate.text = if (movieList[position].releaseDate.length > Constant.YEAR_LENGTH) {
            movieList[position].releaseDate.substring(0, Constant.YEAR_LENGTH)
        } else {
            holder.itemView.resources.getString(R.string.empty_text)
        }

        /* The film is evaluated as bronze, silver or gold according to the vote average
        and the background color is set. */
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

        // The appropriate icon is set depending on whether the movie is added to the favorites or not.
        holder.favorite.run {
            if (movieList[position].isFavorite) {
                if (!isLinear) {
                    setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red))
                } else {
                    setImageResource(R.drawable.ic_baseline_favorite_red_24)
                }
            } else {
                if (!isLinear) {
                    setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                } else {
                    setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        }

        // Depending on whether the movie is adult or not, the warning view is displayed.
        holder.adult.run {
            if (movieList[position].adult) {
                show()
            } else {
                hide()
            }
        }

        // Poster of the movie is added to imageView.
        Glide.with(holder.itemView.context)
            .load(Constant.IMAGE_PATH_PREFIX + movieList[position].posterPath)
            .placeholder(R.drawable.placeholder_movie)
            .into(holder.moviePoster)
    }

    fun setMovieList(newMovieList: List<Movie>) {
        val newList: MutableList<Movie> = mutableListOf()
        newList.addAll(newMovieList)

        val diffUtil = MovieDiffUtil(movieList, newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        movieList = newList
        diffResults.dispatchUpdatesTo(this)
    }

    fun setType(isLinearM: Boolean) {
        isLinear = isLinearM
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLinear) {
            listItem
        } else {
            gridItem
        }
    }

    override fun getItemCount() = movieList.size

    inner class ViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val moviePoster: ImageView = _itemView.findViewById(R.id.movie_poster)
        val movieName: TextView = _itemView.findViewById(R.id.movie_name)
        val voteAverage: TextView = _itemView.findViewById(R.id.vote_average)
        val favorite: ImageButton = _itemView.findViewById(R.id.add_favorite)
        val adult: TextView = _itemView.findViewById(R.id.adult)
        val movieOverview: TextView = _itemView.findViewById(R.id.movie_overview)
        val originalLanguage: TextView = _itemView.findViewById(R.id.original_language)
        val releaseDate: TextView = _itemView.findViewById(R.id.release_date)
        private val movieCard: CardView = _itemView.findViewById(R.id.movie_card)

        init {
            movieCard.setOnClickListener {
                listener.movieOnClick(movieList[bindingAdapterPosition])
            }
            favorite.setOnClickListener {
                // Clicking the Favorite imageButton will change isFavorite icon.
                favorite.run {
                    if (!movieList[bindingAdapterPosition].isFavorite) {
                        if (!isLinear) {
                            setColorFilter(ContextCompat.getColor(_itemView.context, R.color.red))
                        } else {
                            setImageResource(R.drawable.ic_baseline_favorite_red_24)
                        }
                    } else {
                        if (!isLinear) {
                            setColorFilter(ContextCompat.getColor(_itemView.context, R.color.white))
                        } else {
                            setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        }
                    }
                }

                listener.changeFavorite(movieList[bindingAdapterPosition])
            }
        }
    }
}

class MovieDiffUtil(
    private val oldList: List<Movie>,
    private val newList: List<Movie>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }
}

interface MovieClickListener {
    fun movieOnClick(movie: Movie)
    fun changeFavorite(movie: Movie)
}
