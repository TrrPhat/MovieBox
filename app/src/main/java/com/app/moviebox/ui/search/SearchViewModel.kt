package com.app.moviebox.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.moviebox.data.repository.MovieRepository
import com.app.moviebox.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    companion object {
        val GENRES = listOf(
            "Action", "Adventure", "Animation", "Comedy",
            "Crime", "Documentary", "Drama", "Family",
            "Fantasy", "History", "Horror", "Music",
            "Mystery", "Romance", "Science Fiction",
            "Thriller", "War", "Western"
        )
    }

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val recentSearches: LiveData<List<String>> = repository.getRecentSearches().asLiveData()

    private val _recommendedMovies = MutableLiveData<List<Movie>>()
    val recommendedMovies: LiveData<List<Movie>> = _recommendedMovies

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

    private var searchJob: Job? = null

    init {
        loadRecommendedMovies()
    }

    private fun loadRecommendedMovies() {
        viewModelScope.launch {
            repository.getRecommendedMovies().collect { movies ->
                _recommendedMovies.value = movies.take(10)
            }
        }
    }

    fun search(query: String) {
        searchJob?.cancel()

        if (query.length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            _error.value = null

            delay(500) // 500ms debounce

            repository.searchMoviesFromApi(query)
                .onSuccess { movies ->
                    _searchResults.value = movies
                    _isSearching.value = false
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Search failed"
                    _isSearching.value = false
                }
        }
    }

    fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            repository.saveSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun deleteSearchHistory(query: String) {
        viewModelScope.launch {
            repository.deleteSearchHistory(query)
        }
    }

    fun selectCategory(genre: String) {
        _selectedCategory.value = genre
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _isSearching.value = false
        _error.value = null
        _selectedCategory.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun toggleWishlist(movie: Movie) {
        viewModelScope.launch {
            repository.toggleWishlist(movie)
            val list = _searchResults.value?.toMutableList() ?: return@launch
            val index = list.indexOfFirst { it.id == movie.id }
            if (index >= 0) {
                list[index] = movie.copy(isWishlisted = !movie.isWishlisted)
                _searchResults.value = list
            }
        }
    }
}
