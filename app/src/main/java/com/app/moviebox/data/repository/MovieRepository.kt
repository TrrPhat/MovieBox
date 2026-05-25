package com.app.moviebox.data.repository

import com.app.moviebox.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<List<Movie>>
    fun getPopularMovies(): Flow<List<Movie>>
    fun getTopRatedMovies(): Flow<List<Movie>>
    fun getRecommendedMovies(): Flow<List<Movie>>
    fun searchMovies(query: String): Flow<List<Movie>>
    fun getWishlistedMovies(): Flow<List<Movie>>
    fun getRecentSearches(): Flow<List<String>>

    suspend fun fetchAndCacheTrending(): Result<Unit>
    suspend fun fetchAndCachePopular(page: Int = 1): Result<Unit>
    suspend fun fetchAndCacheTopRated(page: Int = 1): Result<Unit>
    suspend fun fetchAndCacheRecommended(page: Int = 1): Result<Unit>
    suspend fun searchMoviesFromApi(query: String): Result<List<Movie>>
    suspend fun getMovieDetail(movie: Movie): Movie
    suspend fun toggleWishlist(movie: Movie)
    suspend fun saveSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun deleteSearchHistory(query: String)
}
