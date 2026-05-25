package com.app.moviebox.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.app.moviebox.R
import com.app.moviebox.databinding.ItemHeroBannerBinding
import com.app.moviebox.domain.model.Movie

class BannerAdapter(
    private val onItemClick: (Movie) -> Unit
) : ListAdapter<Movie, BannerAdapter.BannerViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemHeroBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BannerViewHolder(
        private val binding: ItemHeroBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                ivPoster.load(movie.backdropUrl.ifEmpty { movie.posterUrl }) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_poster)
                    error(R.drawable.placeholder_poster)
                    transformations(RoundedCornersTransformation(0f))
                }

                tvTitle.text = movie.title
                tvGenre.text = movie.genre
                tvRating.text = String.format("%.1f", movie.rating)

                btnWatchNow.setOnClickListener { onItemClick(movie) }
                ivWishlist.setOnClickListener {
                    // Toggle wishlist - future implementation
                }

                ivWishlist.setImageResource(
                    if (movie.isWishlisted) R.drawable.ic_wishlist
                    else R.drawable.ic_wishlist_outline
                )
            }
        }
    }
}
