package com.app.moviebox.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.moviebox.R
import com.app.moviebox.databinding.FragmentSearchBinding
import com.app.moviebox.ui.home.adapter.MovieGridAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter: MovieGridAdapter

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
        setupSearchInput()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.search(query)
            }
        })
    }

    private fun setupRecyclerView() {
        searchAdapter = MovieGridAdapter(
            onItemClick = { /* Navigate to detail */ },
            onWishlistClick = { /* Add to wishlist */ }
        )
        binding.recyclerSearch.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = searchAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            searchAdapter.submitList(movies)
            updateEmptyState(movies.isEmpty() && binding.etSearch.text?.length ?: 0 >= 2)
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
    }

    private fun updateEmptyState(showEmpty: Boolean) {
        binding.emptyState.isVisible = showEmpty && binding.searchProgress.visibility != View.VISIBLE
        binding.recyclerSearch.isVisible = !showEmpty
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
