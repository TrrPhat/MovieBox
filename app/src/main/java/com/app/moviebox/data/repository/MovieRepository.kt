package com.app.moviebox.data.repository

import com.app.moviebox.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<List<Movie>>
    fun getPopularMovies(): Flow<List<Movie>>
    fun getTopRatedMovies(): Flow<List<Movie>>
    fun getRecommendedMovies(): Flow<List<Movie>>
    fun searchMovies(query: String): Flow<List<Movie>>
}
