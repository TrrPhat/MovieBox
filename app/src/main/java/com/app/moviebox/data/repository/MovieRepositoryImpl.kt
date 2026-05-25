package com.app.moviebox.data.repository

import com.app.moviebox.BuildConfig
import com.app.moviebox.data.local.dao.MovieDao
import com.app.moviebox.data.local.dao.SearchHistoryDao
import com.app.moviebox.data.local.entity.MovieEntity
import com.app.moviebox.data.local.entity.SearchHistoryEntity
import com.app.moviebox.data.remote.OmdbApiService
import com.app.moviebox.data.remote.TmdbApiService
import com.app.moviebox.data.remote.TmdbMovie
import com.app.moviebox.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService,
    private val omdbApiService: OmdbApiService,
    private val movieDao: MovieDao,
    private val searchHistoryDao: SearchHistoryDao
) : MovieRepository {

    private val apiKey = BuildConfig.TMDB_API_KEY
    private val omdbApiKey = BuildConfig.OMDB_API_KEY

    override fun getTrendingMovies(): Flow<List<Movie>> {
        return movieDao.getMoviesByType(Movie.TYPE_TRENDING).map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override fun getPopularMovies(): Flow<List<Movie>> {
        return movieDao.getMoviesByType(Movie.TYPE_POPULAR).map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override fun getTopRatedMovies(): Flow<List<Movie>> {
        return movieDao.getMoviesByType(Movie.TYPE_TOP_RATED).map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override fun getRecommendedMovies(): Flow<List<Movie>> {
        return movieDao.getMoviesByType(Movie.TYPE_RECOMMENDED).map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override fun searchMovies(query: String): Flow<List<Movie>> {
        return movieDao.getMoviesByType(Movie.TYPE_POPULAR).map { entities ->
            entities.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.genre.contains(query, ignoreCase = true)
            }.map { it.toMovie() }
        }
    }

    override fun getWishlistedMovies(): Flow<List<Movie>> {
        return movieDao.getWishlistedMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override suspend fun fetchAndCacheTrending(): Result<Unit> {
        return try {
            val response = tmdbApiService.getTrendingMovies(apiKey)
            val movies = response.results.map { it.toEntity(Movie.TYPE_TRENDING) }
            movieDao.clearByType(Movie.TYPE_TRENDING)
            movieDao.insertAll(movies)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchAndCachePopular(page: Int): Result<Unit> {
        return try {
            val response = tmdbApiService.getPopularMovies(apiKey, page)
            val movies = response.results.map { it.toEntity(Movie.TYPE_POPULAR) }
            if (page == 1) {
                movieDao.clearByType(Movie.TYPE_POPULAR)
            }
            movieDao.insertAll(movies)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchAndCacheTopRated(page: Int): Result<Unit> {
        return try {
            val response = tmdbApiService.getTopRatedMovies(apiKey, page)
            val movies = response.results.map { it.toEntity(Movie.TYPE_TOP_RATED) }
            if (page == 1) {
                movieDao.clearByType(Movie.TYPE_TOP_RATED)
            }
            movieDao.insertAll(movies)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchAndCacheRecommended(page: Int): Result<Unit> {
        return try {
            val response = tmdbApiService.getNowPlayingMovies(apiKey, page)
            val movies = response.results.map { it.toEntity(Movie.TYPE_RECOMMENDED) }
            if (page == 1) {
                movieDao.clearByType(Movie.TYPE_RECOMMENDED)
            }
            movieDao.insertAll(movies)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMoviesFromApi(query: String): Result<List<Movie>> {
        return try {
            val response = tmdbApiService.searchMovies(apiKey, query)
            val movies = response.results.map { it.toEntity(Movie.TYPE_POPULAR) }
            movieDao.insertAll(movies)
            Result.success(movies.map { it.toMovie() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(movie: Movie): Movie {
        return try {
            val existingEntity = movieDao.getMovieById(movie.id)
            if (existingEntity?.plot != null && existingEntity.plot.isNotEmpty()) {
                return existingEntity.toMovie()
            }
            val omdbMovie = omdbApiService.getMovieByTitle(omdbApiKey, movie.title)
            val updatedEntity = existingEntity?.copy(
                plot = omdbMovie.plot,
                director = omdbMovie.director,
                actors = omdbMovie.actors,
                runtime = omdbMovie.runtime,
                language = omdbMovie.language,
                country = omdbMovie.country,
                imdbId = omdbMovie.imdbId,
                lastUpdated = System.currentTimeMillis()
            ) ?: movie.toEntity(Movie.TYPE_POPULAR).copy(
                plot = omdbMovie.plot,
                director = omdbMovie.director,
                actors = omdbMovie.actors,
                runtime = omdbMovie.runtime,
                language = omdbMovie.language,
                country = omdbMovie.country,
                imdbId = omdbMovie.imdbId
            )
            movieDao.insert(updatedEntity)
            updatedEntity.toMovie()
        } catch (e: Exception) {
            movie
        }
    }

    override suspend fun toggleWishlist(movie: Movie) {
        movieDao.updateWishlistStatus(movie.id, !movie.isWishlisted)
    }

    override fun getRecentSearches(): Flow<List<String>> {
        return searchHistoryDao.getRecentSearches().map { entities ->
            entities.map { it.query }
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        if (query.isNotBlank()) {
            searchHistoryDao.insert(SearchHistoryEntity(query = query.trim()))
        }
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
    }

    override suspend fun deleteSearchHistory(query: String) {
        searchHistoryDao.delete(query)
    }

    private fun TmdbMovie.toEntity(type: String): MovieEntity {
        val year = getDisplayDate().take(4).toIntOrNull() ?: 0
        return MovieEntity(
            id = id,
            title = getDisplayTitle(),
            posterUrl = TmdbApiService.getPosterUrl(posterPath),
            backdropUrl = TmdbApiService.getBackdropUrl(backdropPath),
            rating = voteAverage?.toFloat() ?: 0f,
            genre = genreIds?.take(3)?.joinToString(", ") ?: "",
            overview = overview ?: "",
            releaseYear = year,
            movieType = type,
            imdbId = null,
            plot = null,
            director = null,
            actors = null,
            runtime = null,
            language = null,
            country = null,
            isWishlisted = false,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun MovieEntity.toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            rating = rating,
            genre = genre,
            overview = overview,
            releaseYear = releaseYear,
            imdbId = imdbId,
            plot = plot,
            director = director,
            actors = actors,
            runtime = runtime,
            language = language,
            country = country,
            isTrending = movieType == Movie.TYPE_TRENDING,
            isPopular = movieType == Movie.TYPE_POPULAR,
            isTopRated = movieType == Movie.TYPE_TOP_RATED,
            isRecommended = movieType == Movie.TYPE_RECOMMENDED,
            isWishlisted = isWishlisted
        )
    }

    private fun Movie.toEntity(type: String): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            rating = rating,
            genre = genre,
            overview = overview,
            releaseYear = releaseYear,
            movieType = type,
            imdbId = imdbId,
            plot = plot,
            director = director,
            actors = actors,
            runtime = runtime,
            language = language,
            country = country,
            isWishlisted = isWishlisted,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
