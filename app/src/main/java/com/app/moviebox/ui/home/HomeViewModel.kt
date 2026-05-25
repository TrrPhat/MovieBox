package com.app.moviebox.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moviebox.data.repository.MovieRepository
import com.app.moviebox.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _trendingMovies = MutableLiveData<List<Movie>>()
    val trendingMovies: LiveData<List<Movie>> = _trendingMovies

    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    private val _recommendedMovies = MutableLiveData<List<Movie>>()
    val recommendedMovies: LiveData<List<Movie>> = _recommendedMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1

    init {
        loadAllData()
    }

    private fun loadAllData() {
        _isLoading.value = true
        _error.value = null
        observeFromDatabase()
        refresh()
    }

    private fun observeFromDatabase() {
        viewModelScope.launch {
            repository.getTrendingMovies()
                .catch { }
                .collect { movies ->
                    _trendingMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getPopularMovies()
                .catch { }
                .collect { movies ->
                    _popularMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getTopRatedMovies()
                .catch { }
                .collect { movies ->
                    _topRatedMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getRecommendedMovies()
                .catch { }
                .collect { movies ->
                    _recommendedMovies.value = movies
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null

            val trendingResult = repository.fetchAndCacheTrending()
            val popularResult = repository.fetchAndCachePopular(1)
            val topRatedResult = repository.fetchAndCacheTopRated(1)
            val recommendedResult = repository.fetchAndCacheRecommended(1)

            currentPage = 1
            _isLoading.value = false
            _isRefreshing.value = false

            if (trendingResult.isFailure && popularResult.isFailure &&
                topRatedResult.isFailure && recommendedResult.isFailure) {
                _error.value = trendingResult.exceptionOrNull()?.message ?: "Failed to load movies"
            }
        }
    }

    fun loadMorePopular() {
        viewModelScope.launch {
            val nextPage = currentPage + 1
            val result = repository.fetchAndCachePopular(nextPage)
            if (result.isSuccess) {
                currentPage = nextPage
            }
        }
    }

    fun toggleWishlist(movie: Movie) {
        viewModelScope.launch {
            repository.toggleWishlist(movie)
            val list = _popularMovies.value?.toMutableList() ?: return@launch
            val index = list.indexOfFirst { it.id == movie.id }
            if (index >= 0) {
                list[index] = movie.copy(isWishlisted = !movie.isWishlisted)
                _popularMovies.value = list
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
