package com.app.moviebox.data.remote

import com.google.gson.annotations.SerializedName

data class TmdbMovieResponse(
    val page: Int,
    val results: List<TmdbMovie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TmdbMovie(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("original_title") val originalTitle: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("media_type") val mediaType: String?,
    val popularity: Double?,
    @SerializedName("adult") val adult: Boolean?
) {
    fun getDisplayTitle(): String = title ?: name ?: originalTitle ?: ""
    fun getDisplayDate(): String = releaseDate ?: firstAirDate ?: ""
}
