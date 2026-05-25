package com.app.moviebox.data.repository

import com.app.moviebox.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor() : MovieRepository {

    private val mockTrending = listOf(
        Movie(
            id = 1,
            title = "Avengers: Endgame",
            posterUrl = "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg",
            rating = 8.5f,
            genre = "Action, Sci-Fi, Adventure",
            overview = "After the devastating events of Avengers: Infinity War, the universe is in ruins.",
            releaseYear = 2019,
            isTrending = true
        ),
        Movie(
            id = 2,
            title = "Spider-Man: No Way Home",
            posterUrl = "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/iQFcwSGbZXMkeyKrxbPnwnRo5fl.jpg",
            rating = 8.2f,
            genre = "Action, Adventure, Sci-Fi",
            overview = "With Spider-Man's identity now revealed, Peter asks Doctor Strange for help.",
            releaseYear = 2021,
            isTrending = true
        ),
        Movie(
            id = 3,
            title = "Dune: Part Two",
            posterUrl = "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
            rating = 8.6f,
            genre = "Sci-Fi, Adventure, Drama",
            overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge.",
            releaseYear = 2024,
            isTrending = true
        )
    )

    private val mockPopular = listOf(
        Movie(id = 4, title = "The Batman", posterUrl = "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg", backdropUrl = "", rating = 7.9f, genre = "Action, Crime, Drama", overview = "", releaseYear = 2022, isPopular = true),
        Movie(id = 5, title = "Oppenheimer", posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", backdropUrl = "", rating = 8.4f, genre = "Biography, Drama, History", overview = "", releaseYear = 2023, isPopular = true),
        Movie(id = 6, title = "Guardians of the Galaxy Vol. 3", posterUrl = "https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg", backdropUrl = "", rating = 8.0f, genre = "Action, Adventure, Comedy", overview = "", releaseYear = 2023, isPopular = true),
        Movie(id = 7, title = "John Wick: Chapter 4", posterUrl = "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg", backdropUrl = "", rating = 7.8f, genre = "Action, Crime, Thriller", overview = "", releaseYear = 2023, isPopular = true),
        Movie(id = 8, title = "The Super Mario Bros. Movie", posterUrl = "https://image.tmdb.org/t/p/w500/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg", backdropUrl = "", rating = 7.1f, genre = "Animation, Adventure, Comedy", overview = "", releaseYear = 2023, isPopular = true),
        Movie(id = 9, title = "Black Panther: Wakanda Forever", posterUrl = "https://image.tmdb.org/t/p/w500/sv1xJUazXeYqALzczSZ3O6nkH75.jpg", backdropUrl = "", rating = 7.3f, genre = "Action, Adventure, Drama", overview = "", releaseYear = 2022, isPopular = true)
    )

    private val mockTopRated = listOf(
        Movie(id = 10, title = "The Shawshank Redemption", posterUrl = "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg", backdropUrl = "", rating = 9.3f, genre = "Drama", overview = "", releaseYear = 1994, isTopRated = true),
        Movie(id = 11, title = "The Godfather", posterUrl = "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg", backdropUrl = "", rating = 9.2f, genre = "Crime, Drama", overview = "", releaseYear = 1972, isTopRated = true),
        Movie(id = 12, title = "The Dark Knight", posterUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", backdropUrl = "", rating = 9.0f, genre = "Action, Crime, Drama", overview = "", releaseYear = 2008, isTopRated = true),
        Movie(id = 13, title = "Pulp Fiction", posterUrl = "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg", backdropUrl = "", rating = 8.9f, genre = "Crime, Drama", overview = "", releaseYear = 1994, isTopRated = true),
        Movie(id = 14, title = "Forrest Gump", posterUrl = "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg", backdropUrl = "", rating = 8.8f, genre = "Drama, Romance", overview = "", releaseYear = 1994, isTopRated = true),
        Movie(id = 15, title = "Inception", posterUrl = "https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg", backdropUrl = "", rating = 8.8f, genre = "Action, Adventure, Sci-Fi", overview = "", releaseYear = 2010, isTopRated = true)
    )

    private val mockRecommended = listOf(
        Movie(id = 16, title = "Interstellar", posterUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", backdropUrl = "", rating = 8.6f, genre = "Adventure, Drama, Sci-Fi", overview = "", releaseYear = 2014, isRecommended = true),
        Movie(id = 17, title = "The Matrix", posterUrl = "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg", backdropUrl = "", rating = 8.7f, genre = "Action, Sci-Fi", overview = "", releaseYear = 1999, isRecommended = true),
        Movie(id = 18, title = "Parasite", posterUrl = "https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg", backdropUrl = "", rating = 8.5f, genre = "Comedy, Drama, Thriller", overview = "", releaseYear = 2019, isRecommended = true),
        Movie(id = 19, title = "Joker", posterUrl = "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg", backdropUrl = "", rating = 8.4f, genre = "Crime, Drama, Thriller", overview = "", releaseYear = 2019, isRecommended = true),
        Movie(id = 20, title = "Everything Everywhere All at Once", posterUrl = "https://image.tmdb.org/t/p/w500/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg", backdropUrl = "", rating = 8.0f, genre = "Action, Adventure, Comedy", overview = "", releaseYear = 2022, isRecommended = true),
        Movie(id = 21, title = "The Wolf of Wall Street", posterUrl = "https://image.tmdb.org/t/p/w500/pclDCHsbqNO5Gi4PFCY3HYTh4Xc.jpg", backdropUrl = "", rating = 8.2f, genre = "Comedy, Crime, Drama", overview = "", releaseYear = 2013, isRecommended = true)
    )

    override fun getTrendingMovies(): Flow<List<Movie>> = flow { emit(mockTrending) }

    override fun getPopularMovies(): Flow<List<Movie>> = flow { emit(mockPopular) }

    override fun getTopRatedMovies(): Flow<List<Movie>> = flow { emit(mockTopRated) }

    override fun getRecommendedMovies(): Flow<List<Movie>> = flow { emit(mockRecommended) }

    override fun searchMovies(query: String): Flow<List<Movie>> {
        val all = mockPopular + mockTopRated + mockRecommended
        return flow {
            emit(all.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.genre.contains(query, ignoreCase = true)
            })
        }
    }
}
