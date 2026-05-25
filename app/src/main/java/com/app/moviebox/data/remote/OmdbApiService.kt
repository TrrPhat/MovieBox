package com.app.moviebox.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    @GET("/")
    suspend fun getMovieByTitle(
        @Query("apikey") apiKey: String,
        @Query("t") title: String,
        @Query("plot") plot: String = "full"
    ): OmdbMovie

    @GET("/")
    suspend fun getMovieByImdbId(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): OmdbMovie

    companion object {
        const val BASE_URL = "https://www.omdbapi.com/"
    }
}
