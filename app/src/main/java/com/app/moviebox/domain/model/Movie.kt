package com.app.moviebox.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Float,
    val genre: String,
    val overview: String,
    val releaseYear: Int,
    val imdbId: String? = null,
    val plot: String? = null,
    val director: String? = null,
    val actors: String? = null,
    val runtime: String? = null,
    val language: String? = null,
    val country: String? = null,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isTopRated: Boolean = false,
    val isRecommended: Boolean = false,
    val isWishlisted: Boolean = false
) {
    companion object {
        const val TYPE_TRENDING = "trending"
        const val TYPE_POPULAR = "popular"
        const val TYPE_TOP_RATED = "top_rated"
        const val TYPE_RECOMMENDED = "recommended"
    }
}
