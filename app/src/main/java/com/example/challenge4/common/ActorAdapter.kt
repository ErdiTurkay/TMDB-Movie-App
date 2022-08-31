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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.model.Actor

class ActorAdapter(
    var listener: ActorClickListener,
    var isLarge: Boolean
) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    private var actorList = listOf<Actor>()
    private val listItem = 0
    private val gridItem = 1
    private var isLinear = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_actor_small, parent, false)

        // The relevant Layout is selected according to the ViewType.
        if (viewType == gridItem) {
            if (isLarge) {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_actor_large, parent, false)
            }
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_actor_linear, parent, false)
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.actorName.text = actorList[position].name
        holder.department.text = actorList[position].knownForDepartment
        holder.popularity.text = actorList[position].popularity.toString()

        holder.gender.run {
            // The appropriate icon has been set according to the gender of the actor.
            when (actorList[position].gender) {
                1 -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_female_24, 0, 0, 0)
                    setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.female))
                }
                2 -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_male_24, 0, 0, 0)
                    setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.male))
                }
                else -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_transgender_24, 0, 0, 0)
                }
            }
        }

        // The appropriate icon is set depending on whether the actor is added to the favorites or not.
        holder.favorite.run {
            if (actorList[position].isFavorite) {
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

        // Photo of the actor is added to imageView.
        Glide.with(holder.itemView.context)
            .load(Constant.IMAGE_PATH_PREFIX + actorList[position].profilePath)
            .placeholder(R.drawable.placeholder_person)
            .fitCenter()
            .into(holder.actorImage)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setActorList(actors: List<Actor>) {
        actorList = actors
        notifyDataSetChanged()
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

    override fun getItemCount() = actorList.size

    inner class ViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val actorImage: ImageView = _itemView.findViewById(R.id.actor_image)
        val actorName: TextView = _itemView.findViewById(R.id.actor_name)
        val favorite: ImageButton = _itemView.findViewById(R.id.add_favorite)
        val department: TextView = _itemView.findViewById(R.id.department)
        val popularity: TextView = _itemView.findViewById(R.id.popularity)
        val gender: TextView = _itemView.findViewById(R.id.gender)
        private val actorCard: CardView = _itemView.findViewById(R.id.actor_card)

        init {
            actorCard.setOnClickListener {
                listener.actorOnClick(actorList[bindingAdapterPosition])
            }

            favorite.setOnClickListener {
                // Clicking the Favorite imageButton will change isFavorite icon.
                favorite.run {
                    if (!actorList[bindingAdapterPosition].isFavorite) {
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

                listener.changeFavorite(actorList[bindingAdapterPosition])
            }
        }
    }
}

interface ActorClickListener {
    fun actorOnClick(actor: Actor)
    fun changeFavorite(actor: Actor)
}
