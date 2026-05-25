package com.app.moviebox.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.moviebox.R
import com.app.moviebox.databinding.FragmentSearchBinding
import com.app.moviebox.ui.home.adapter.GenreAdapter
import com.app.moviebox.ui.home.adapter.MovieGridAdapter
import com.app.moviebox.ui.home.adapter.MovieHorizontalAdapter
import com.app.moviebox.ui.search.adapter.SearchHistoryAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter: MovieGridAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var recommendedAdapter: MovieHorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupSearchInput()
        observeViewModel()
    }

    private fun setupAdapters() {
        searchAdapter = MovieGridAdapter(
            onItemClick = { /* Navigate to detail */ },
            onWishlistClick = { movie -> viewModel.toggleWishlist(movie) }
        )
        binding.recyclerSearch.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = searchAdapter
        }

        historyAdapter = SearchHistoryAdapter(
            onItemClick = { query ->
                binding.etSearch.setText(query)
                binding.etSearch.setSelection(query.length)
                viewModel.search(query)
            },
            onDeleteClick = { query -> viewModel.deleteSearchHistory(query) }
        )
        binding.recyclerRecentSearches.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = historyAdapter
        }

        genreAdapter = GenreAdapter { genre ->
            binding.etSearch.setText(genre)
            binding.etSearch.setSelection(genre.length)
            viewModel.search(genre)
        }
        binding.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
        }
        genreAdapter.submitList(SearchViewModel.GENRES)

        recommendedAdapter = MovieHorizontalAdapter(
            onItemClick = { /* Navigate to detail */ },
            onWishlistClick = { movie -> viewModel.toggleWishlist(movie) }
        )
        binding.recyclerRecommended.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedAdapter
        }

        binding.tvClearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.search(query)
                binding.ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text?.toString() ?: ""
                if (query.isNotEmpty()) {
                    viewModel.saveSearchQuery(query)
                    viewModel.search(query)
                }
                true
            } else {
                false
            }
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearSearch()
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            searchAdapter.submitList(movies)
            updateContentVisibility(binding.etSearch.text?.length ?: 0, movies)
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.searchProgress.isVisible = isSearching
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showErrorSnackbar(error)
                viewModel.clearError()
            }
        }

        viewModel.recentSearches.observe(viewLifecycleOwner) { searches ->
            historyAdapter.submitList(searches)
            binding.recentSearchesSection.isVisible = searches.isNotEmpty()
        }

        viewModel.recommendedMovies.observe(viewLifecycleOwner) { movies ->
            recommendedAdapter.submitList(movies)
            binding.recommendedSection.isVisible = movies.isNotEmpty()
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            genreAdapter.setSelectedGenre(category)
        }
    }

    private fun updateContentVisibility(queryLength: Int, results: List<*>) {
        val isSearching = queryLength >= 2

        binding.defaultContent.isVisible = !isSearching

        if (isSearching) {
            if (results.isEmpty()) {
                binding.emptyState.isVisible = true
                binding.recyclerSearch.isVisible = false
            } else {
                binding.emptyState.isVisible = false
                binding.recyclerSearch.isVisible = true
            }
        } else {
            binding.emptyState.isVisible = false
            binding.recyclerSearch.isVisible = false
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.card_dark))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.gold))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
