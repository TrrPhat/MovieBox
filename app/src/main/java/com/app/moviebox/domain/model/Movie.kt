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
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isTopRated: Boolean = false,
    val isRecommended: Boolean = false,
    val isWishlisted: Boolean = false
)
