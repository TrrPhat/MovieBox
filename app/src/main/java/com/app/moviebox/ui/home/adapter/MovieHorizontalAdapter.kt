package com.app.moviebox.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.app.moviebox.R
import com.app.moviebox.databinding.ItemMovieCardHorizontalBinding
import com.app.moviebox.domain.model.Movie

class MovieHorizontalAdapter(
    private val onItemClick: (Movie) -> Unit,
    private val onWishlistClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieHorizontalAdapter.HorizontalViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = ItemMovieCardHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HorizontalViewHolder(
        private val binding: ItemMovieCardHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                ivPoster.load(movie.posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_poster)
                    error(R.drawable.placeholder_poster)
                    transformations(RoundedCornersTransformation(16f))
                }

                tvTitle.text = movie.title
                tvRating.text = String.format("%.1f", movie.rating)

                ivWishlist.setImageResource(
                    if (movie.isWishlisted) R.drawable.ic_wishlist
                    else R.drawable.ic_wishlist_outline
                )

                cardPoster.setOnClickListener { onItemClick(movie) }
                ivWishlist.setOnClickListener { onWishlistClick(movie) }
            }
        }
    }
}
