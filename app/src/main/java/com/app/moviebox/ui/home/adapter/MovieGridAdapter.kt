package com.app.moviebox.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.app.moviebox.R
import com.app.moviebox.databinding.ItemMovieCardGridBinding
import com.app.moviebox.domain.model.Movie

class MovieGridAdapter(
    private val onItemClick: (Movie) -> Unit,
    private val onWishlistClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieGridAdapter.GridViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemMovieCardGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GridViewHolder(
        private val binding: ItemMovieCardGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                ivPoster.load(movie.posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_poster)
                    error(R.drawable.placeholder_poster)
                    transformations(RoundedCornersTransformation(24f))
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
