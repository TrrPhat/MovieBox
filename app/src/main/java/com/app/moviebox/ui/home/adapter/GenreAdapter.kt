package com.app.moviebox.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.moviebox.R
import com.app.moviebox.databinding.ItemGenreChipBinding

class GenreAdapter(
    private val onGenreClick: (String) -> Unit
) : ListAdapter<String, GenreAdapter.GenreViewHolder>(GenreDiffCallback()) {

    private var selectedGenre: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedGenre(genre: String?) {
        val oldSelected = selectedGenre
        selectedGenre = genre
        if (oldSelected != null) {
            val oldIndex = currentList.indexOf(oldSelected)
            if (oldIndex >= 0) notifyItemChanged(oldIndex)
        }
        if (genre != null) {
            val newIndex = currentList.indexOf(genre)
            if (newIndex >= 0) notifyItemChanged(newIndex)
        }
    }

    inner class GenreViewHolder(
        private val binding: ItemGenreChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: String) {
            val isSelected = genre == selectedGenre
            val context = binding.root.context

            binding.tvGenre.text = genre
            binding.tvGenre.isSelected = isSelected

            if (isSelected) {
                binding.tvGenre.setBackgroundResource(R.drawable.bg_genre_chip_selected)
                binding.tvGenre.setTextColor(ContextCompat.getColor(context, R.color.gold))
            } else {
                binding.tvGenre.setBackgroundResource(R.drawable.bg_genre_chip)
                binding.tvGenre.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            binding.root.setOnClickListener { onGenreClick(genre) }
        }
    }

    private class GenreDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}
