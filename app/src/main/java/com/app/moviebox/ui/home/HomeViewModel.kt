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

    init {
        loadAllData()
    }

    private fun loadAllData() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getTrendingMovies()
                .catch { _isLoading.value = false }
                .collect { movies ->
                    _trendingMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getPopularMovies()
                .catch { _isLoading.value = false }
                .collect { movies ->
                    _popularMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getTopRatedMovies()
                .catch { _isLoading.value = false }
                .collect { movies ->
                    _topRatedMovies.value = movies
                }
        }
        viewModelScope.launch {
            repository.getRecommendedMovies()
                .catch { _isLoading.value = false }
                .collect { movies ->
                    _recommendedMovies.value = movies
                    _isLoading.value = false
                }
        }
    }

    fun toggleWishlist(movie: Movie) {
        val list = _popularMovies.value?.toMutableList() ?: return
        val index = list.indexOfFirst { it.id == movie.id }
        if (index >= 0) {
            list[index] = movie.copy(isWishlisted = !movie.isWishlisted)
            _popularMovies.value = list
        }
    }
}
