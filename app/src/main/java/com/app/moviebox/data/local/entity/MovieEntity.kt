package com.app.moviebox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    @ColumnInfo(name = "poster_url")
    val posterUrl: String,
    @ColumnInfo(name = "backdrop_url")
    val backdropUrl: String,
    val rating: Float,
    val genre: String,
    val overview: String,
    @ColumnInfo(name = "release_year")
    val releaseYear: Int,
    @ColumnInfo(name = "movie_type")
    val movieType: String,
    @ColumnInfo(name = "imdb_id")
    val imdbId: String?,
    val plot: String?,
    val director: String?,
    val actors: String?,
    val runtime: String?,
    val language: String?,
    val country: String?,
    @ColumnInfo(name = "is_wishlisted")
    val isWishlisted: Boolean = false,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
