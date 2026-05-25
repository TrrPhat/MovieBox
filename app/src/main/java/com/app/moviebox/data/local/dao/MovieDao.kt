package com.app.moviebox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.moviebox.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE movie_type = :type ORDER BY last_updated DESC")
    fun getMoviesByType(type: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE movie_type = :type ORDER BY last_updated DESC")
    suspend fun getMoviesByTypeSync(type: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE is_wishlisted = 1 ORDER BY last_updated DESC")
    fun getWishlistedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY rating DESC LIMIT 50")
    suspend fun searchMovies(query: String): List<MovieEntity>

    @Query("UPDATE movies SET is_wishlisted = :isWishlisted WHERE id = :movieId")
    suspend fun updateWishlistStatus(movieId: Int, isWishlisted: Boolean)

    @Query("DELETE FROM movies WHERE movie_type = :type")
    suspend fun clearByType(type: String)

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
