# FILMY PROJECT SPECIFICATION
## Complete Guide for Building an Improved Movie App

---

## TABLE OF CONTENTS

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Project Structure](#3-project-structure)
4. [Database Schema](#4-database-schema)
5. [API Integration](#5-api-integration)
6. [Dependencies](#6-dependencies)
7. [Features](#7-features)
8. [Code Guidelines](#8-code-guidelines)
9. [UI/UX Improvements](#9-uiux-improvements)
10. [Authentication Guide](#10-authentication-guide)
11. [Layout Files Reference](#11-layout-files-reference)
12. [Pre-Build Checklist](#12-pre-build-checklist)

---

## 1. PROJECT OVERVIEW

**Project Name:** Filmy (Movie Database App)
**Type:** Android Native Application
**Language:** Kotlin
**Min SDK:** 24
**Target SDK:** 35
**Compile SDK:** 36

### Current Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| Architecture | Clean Architecture + MVVM |
| DI | Dagger Hilt |
| Database | Room |
| Networking | Retrofit + OkHttp |
| Image Loading | Glide |
| Navigation | Jetpack Navigation |
| Async | Kotlin Coroutines + Flow |
| Theme | Material Design 3 |

### Recommended Tech Stack (Improved)

| Component | Technology | Reason |
|-----------|------------|--------|
| Language | Kotlin | - |
| Architecture | Clean Architecture + MVVM | - |
| DI | Hilt | - |
| Database | Room | - |
| Networking | Retrofit + OkHttp | - |
| Image Loading | **Coil** | Modern, Kotlin-first, lighter |
| Navigation | Navigation Component | - |
| Async | Coroutines + Flow | - |
| Theme | Material Design 3 | - |
| Auth | **Firebase Auth** | User accounts |
| Cloud Sync | **Firestore** | Cross-device sync |
| Preferences | **DataStore** | Modern replacement for SharedPreferences |
| Paging | **Paging 3** | Efficient large list loading |

---

## 2. ARCHITECTURE

### Clean Architecture Layers

```
┌────────────────────────────────────────────────────────────┐
│                      UI LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  Activities  │  │  Fragments   │  │  ViewModels  │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
├────────────────────────────────────────────────────────────┤
│                    DOMAIN LAYER                            │
│  ┌──────────────────────────────────────────────────┐    │
│  │              Repository Interfaces                 │    │
│  │  MovieRepository, AuthRepository, UserRepository  │    │
│  └──────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐    │
│  │                   Use Cases                       │    │
│  │  GetTrendingUseCase, SearchMoviesUseCase, etc.   │    │
│  └──────────────────────────────────────────────────┘    │
├────────────────────────────────────────────────────────────┤
│                     DATA LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │ Local Data  │  │ Remote Data │  │  Auth Service   │  │
│  │   (Room)    │  │ (Retrofit)  │  │   (Firebase)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

### MVVM Pattern

```
User Action → ViewModel → Repository → DataSource (Local/Remote)
     ↑                                                    │
     └──────────────── StateFlow ─────────────────────────┘
```

---

## 3. PROJECT STRUCTURE

```
app/src/main/java/com/yourapp/filmyplus/
├── FilmyApplication.kt                    # Application class with Hilt
│
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── FilmyDatabase.kt        # Room database
│   │   │   ├── dao/
│   │   │   │   ├── MovieDao.kt
│   │   │   │   └── MovieDetailsDao.kt
│   │   │   └── entity/
│   │   │       ├── Movie.kt
│   │   │       ├── MovieDetails.kt
│   │   │       └── User.kt
│   │   └── datastore/
│   │       └── UserPreferencesDataStore.kt
│   │
│   ├── remote/
│   │   ├── api/
│   │   │   ├── TmdbApiService.kt
│   │   │   └── AuthApiService.kt
│   │   └── dto/
│   │       └── MovieDto.kt
│   │
│   └── repository/
│       ├── MovieRepositoryImpl.kt
│       ├── AuthRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── Movie.kt
│   │   ├── User.kt
│   │   └── Resource.kt
│   │
│   ├── repository/
│   │   ├── MovieRepository.kt          # Interface
│   │   └── AuthRepository.kt          # Interface
│   │
│   └── usecase/
│       ├── GetTrendingMoviesUseCase.kt
│       ├── GetMovieDetailsUseCase.kt
│       ├── SearchMoviesUseCase.kt
│       ├── LoginUseCase.kt
│       └── RegisterUseCase.kt
│
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── AuthModule.kt
│
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.kt
│   │   ├── RegisterActivity.kt
│   │   ├── AuthViewModel.kt
│   │   └── layout/
│   │
│   ├── home/
│   │   ├── MainActivity.kt
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── layout/
│   │
│   ├── details/
│   │   ├── MovieDetailsActivity.kt
│   │   ├── MovieDetailsViewModel.kt
│   │   └── layout/
│   │
│   ├── search/
│   │   ├── SearchFragment.kt
│   │   ├── SearchViewModel.kt
│   │   └── layout/
│   │
│   ├── profile/
│   │   ├── ProfileFragment.kt
│   │   ├── ProfileViewModel.kt
│   │   └── layout/
│   │
│   ├── collections/
│   │   ├── CollectionsFragment.kt
│   │   ├── FavoritesFragment.kt
│   │   ├── WatchlistFragment.kt
│   │   └── layout/
│   │
│   ├── settings/
│   │   ├── SettingsFragment.kt
│   │   └── layout/
│   │
│   └── common/
│       ├── adapters/
│       │   ├── MovieAdapter.kt
│       │   └── CastAdapter.kt
│       └── base/
│           ├── BaseActivity.kt
│           └── BaseFragment.kt
│
├── util/
│   ├── Constants.kt
│   ├── Extensions.kt
│   ├── NetworkUtils.kt
│   └── ImageLoader.kt
│
└── workers/
    └── SyncWorker.kt
```

---

## 4. DATABASE SCHEMA

### Room Database Setup

```kotlin
// Database class
@Database(
    entities = [Movie::class, MovieDetails::class, User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FilmyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieDetailsDao(): MovieDetailsDao
    abstract fun userDao(): UserDao
}
```

### Movie Entity

```kotlin
@Entity(tableName = "movies", primaryKeys = ["id", "type"])
data class Movie(
    @PrimaryKey val id: Int,
    val title: String?,
    val originalTitle: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val genreIds: ArrayList<Int>,
    val originalLanguage: String?,
    val adult: Boolean?,
    val video: Boolean?,
    val type: Int = 0  // 0=Trending, 1=InTheaters, 2=Upcoming
)
```

### MovieDetails Entity

```kotlin
@Entity(tableName = "movie_details", primaryKeys = ["id", "type"])
data class MovieDetails(
    @PrimaryKey val id: Int,
    
    // Basic Info
    val title: String?,
    val originalTitle: String?,
    val overview: String?,
    val tagline: String?,
    val status: String?,
    val releaseDate: String?,
    
    // Media
    val posterPath: String?,
    val backdropPath: String?,
    
    // Ratings
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    
    // Details
    val runtime: Int?,
    val budget: Int?,
    val revenue: Int?,
    val imdbId: String?,
    
    // Relations
    val genres: ArrayList<Genres>,
    val spokenLanguages: ArrayList<SpokenLanguages>,
    val productionCompanies: ArrayList<ProductionCompanies>,
    val belongsToCollection: Collection?,
    val trailers: Trailers?,
    
    // User Data (local)
    var isFavorite: Boolean = false,
    var isWatchlist: Boolean = false,
    var userId: String? = null,  // Link to Firebase user
    
    val type: Int = 0
)
```

### User Entity (for local caching)

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long? = null
)
```

### Favorite Entity (for cloud sync)

```kotlin
@Entity(tableName = "favorites")
data class FavoriteMovie(
    @PrimaryKey val movieId: Int,
    val userId: String,
    val movieJson: String,  // Serialized MovieDetails
    val addedAt: Long = System.currentTimeMillis()
)
```

---

## 5. API INTEGRATION

### TMDB API Service

```kotlin
interface TmdbApiService {
    
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        
        // Image sizes
        const val POSTER_SIZE_SMALL = "w185"
        const val POSTER_SIZE_MEDIUM = "w500"
        const val POSTER_SIZE_LARGE = "w780"
        const val BACKDROP_SIZE = "w1280"
        const val ORIGINAL = "original"
    }
    
    // ==================== MOVIES ====================
    
    @GET("movie/popular")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    @GET("movie/upcoming")
    suspend fun getUpcoming(
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("append_to_response") append: String = "videos,credits,recommendations"
    ): MovieDetailsResponse
    
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: String
    ): CreditsResponse
    
    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendations(
        @Path("movie_id") movieId: String,
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: String,
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    // ==================== SEARCH ====================
    
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): MoviesResponse
    
    // ==================== PERSON ====================
    
    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: String
    ): PersonDetails
    
    @GET("person/{person_id}/movie_credits")
    suspend fun getPersonMovies(
        @Path("person_id") personId: String
    ): PersonMovieCredits
    
    // ==================== TRENDING ====================
    
    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMoviesByTime(
        @Path("time_window") timeWindow: String = "week",  // day or week
        @Query("page") page: Int = 1
    ): MoviesResponse
    
    // ==================== GENRES ====================
    
    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenresResponse
}
```

### Response Models

```kotlin
data class MoviesResponse(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)

data class MovieDetailsResponse(
    val id: Int,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val tagline: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val budget: Int?,
    val revenue: Int?,
    val status: String?,
    val imdbId: String?,
    val genres: List<Genre>,
    val spokenLanguages: List<SpokenLanguage>,
    val productionCompanies: List<ProductionCompany>,
    val belongsToCollection: Collection?,
    val videos: VideosResponse?,
    val credits: CreditsResponse?
)

data class CreditsResponse(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class VideosResponse(
    val id: Int,
    val results: List<Video>
)

data class Video(
    val id: String,
    val key: String,         // YouTube video ID
    val name: String,
    val site: String,        // "YouTube"
    val type: String,        // "Trailer", "Teaser", "Clip"
    val official: Boolean,
    val publishedAt: String
)
```

### Network Module (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)
}

@Interceptor
class AuthInterceptor @Inject constructor(
    @Named("tmdb_api_key") private val apiKey: String
) {
    @OkHttpAnnotations
    fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()
        val request = original.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
```

---

## 6. DEPENDENCIES

### build.gradle (Project level)

```groovy
// Top-level build file
buildscript {
    ext {
        kotlin_version = '1.9.22'
        hilt_version = '2.51.1'
        room_version = '2.6.1'
        nav_version = '2.8.3'
        lifecycle_version = '2.8.7'
        coroutines_version = '1.8.1'
        retrofit_version = '2.11.0'
        okhttp_version = '4.12.0'
        coil_version = '2.7.0'
        paging_version = '3.3.5'
        datastore_version = '1.1.1'
        firebase_bom_version = '33.6.0'
    }
}

plugins {
    id 'com.android.application' version '8.2.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.22' apply false
    id 'com.google.dagger.hilt.android' version '2.51.1' apply false
    id 'com.google.devtools.ksp' version '1.9.22-1.0.17' apply false
    id 'com.google.gms.google-services' version '4.4.2' apply false
}
```

### build.gradle (App level)

```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.dagger.hilt.android'
    id 'com.google.devtools.ksp'
    id 'kotlin-parcelize'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.yourapp.filmyplus'
    compileSdk 36

    defaultConfig {
        applicationId "com.yourapp.filmyplus"
        minSdk 24
        targetSdk 35
        versionCode 1
        versionName "1.0.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        
        // API Keys (use secrets.properties)
        buildConfigField("String", "TMDB_API_KEY", getSecret("TMDB_API_KEY", ""))
        
        vectorDrawables {
            useSupportLibrary true
        }
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
            debuggable true
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }
    
    buildFeatures {
        viewBinding true
        buildConfig true
    }
}

def getSecret(String key, String defaultValue) {
    def secretsFile = file('secrets.properties')
    if (secretsFile.exists()) {
        Properties props = new Properties()
        props.load(new FileInputStream(secretsFile))
        return props.getProperty(key, defaultValue)
    }
    return defaultValue
}

dependencies {
    // ==================== CORE ====================
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.activity:activity-ktx:1.9.3'
    implementation 'androidx.fragment:fragment-ktx:1.8.5'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.1'
    
    // ==================== MATERIAL DESIGN ====================
    implementation 'com.google.android.material:material:1.12.0'
    
    // ==================== LIFECYCLE ====================
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$rootProject.ext.lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$rootProject.ext.lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$rootProject.ext.lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-common-java8:$rootProject.ext.lifecycle_version"
    
    // ==================== NAVIGATION ====================
    implementation "androidx.navigation:navigation-fragment-ktx:$rootProject.ext.nav_version"
    implementation "androidx.navigation:navigation-ui-ktx:$rootProject.ext.nav_version"
    
    // ==================== ROOM DATABASE ====================
    implementation "androidx.room:room-runtime:$rootProject.ext.room_version"
    implementation "androidx.room:room-ktx:$rootProject.ext.room_version"
    implementation "androidx.room:room-rxjava3:$rootProject.ext.room_version"
    ksp "androidx.room:room-compiler:$rootProject.ext.room_version"
    
    // ==================== HILT DI ====================
    implementation "com.google.dagger:hilt-android:$rootProject.ext.hilt_version"
    ksp "com.google.dagger:hilt-compiler:$rootProject.ext.hilt_version"
    implementation 'androidx.hilt:hilt-work:1.2.0'
    ksp 'androidx.hilt:hilt-compiler:1.2.0'
    
    // ==================== NETWORKING ====================
    implementation "com.squareup.retrofit2:retrofit:$rootProject.ext.retrofit_version"
    implementation "com.squareup.retrofit2:converter-gson:$rootProject.ext.retrofit_version"
    implementation "com.squareup.retrofit2:adapter-rxjava3:$rootProject.ext.retrofit_version"
    implementation "com.squareup.okhttp3:okhttp:$rootProject.ext.okhttp_version"
    implementation "com.squareup.okhttp3:logging-interceptor:$rootProject.ext.okhttp_version"
    
    // ==================== IMAGE LOADING ====================
    implementation "io.coil-kt:coil:$rootProject.ext.coil_version"
    implementation "io.coil-kt:coil-base:$rootProject.ext.coil_version"
    implementation 'io.coil-kt:coil-compose:2.7.0'  // For Compose if needed
    
    // ==================== COROUTINES ====================
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$rootProject.ext.coroutines_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$rootProject.ext.coroutines_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$rootProject.ext.coroutines_version"
    
    // ==================== PAGING ====================
    implementation "androidx.paging:paging-runtime-ktx:$rootProject.ext.paging_version"
    implementation "androidx.paging:paging-rxjava3:$rootProject.ext.paging_version"
    
    // ==================== DATASTORE ====================
    implementation "androidx.datastore:datastore-preferences:$rootProject.ext.datastore_version"
    implementation "androidx.datastore:datastore-preferences-rxjava3:$rootProject.ext.datastore_version"
    
    // ==================== FIREBASE ====================
    implementation platform("com.google.firebase:firebase-bom:$rootProject.ext.firebase_bom_version")
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-crashlytics-ktx'
    
    // ==================== GOOGLE SIGN IN ====================
    implementation 'com.google.android.gms:play-services-auth:21.3.0'
    
    // ==================== WORK MANAGER ====================
    implementation 'androidx.work:work-runtime-ktx:2.9.1'
    
    // ==================== CHROME CUSTOM TABS ====================
    implementation 'androidx.browser:browser:1.8.0'
    
    // ==================== PALETTE ====================
    implementation 'androidx.palette:palette-ktx:1.0.0'
    
    // ==================== SPLASH SCREEN ====================
    implementation 'androidx.core:core-splashscreen:1.0.1'
    
    // ==================== ACCOMPANIST ====================
    implementation 'com.google.accompanist:accompanist-systemuicontroller:0.34.0'
    implementation 'com.google.accompanist:accompanist-placeholder-material:0.34.0'
    implementation 'com.google.accompanist:accompanist-swiperefresh:0.34.0'
    
    // ==================== KOTLIN SERIALIZATION ====================
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3'
    
    // ==================== TESTING ====================
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.2.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
}
```

### secrets.properties (Create this file)

```properties
# TMDB API Key (Get from https://www.themoviedb.org/settings/api)
TMDB_API_KEY=your_tmdb_api_key_here

# Firebase (google-services.json will be auto-loaded)
```

---

## 7. FEATURES

### Current Features (Existing in Filmy)

| Feature | Description | Status |
|---------|-------------|--------|
| **Home Screen** | Display Trending, Now Playing, Upcoming movies | ✅ Done |
| **Search** | Real-time search with 300ms debounce | ✅ Done |
| **Movie Details** | Full info, trailer, cast, crew, similar | ✅ Done |
| **Ratings Display** | TMDB, IMDB, Rotten Tomatoes, Metacritic | ✅ Done |
| **Favorites** | Save favorite movies locally | ✅ Done |
| **Watchlist** | Save movies to watch later | ✅ Done |
| **Dark Mode** | System/Light/Dark theme support | ✅ Done |
| **Offline Mode** | Room database caching | ✅ Done |
| **Share** | Share movie info via Intent | ✅ Done |
| **Settings** | Theme, image quality preferences | ✅ Done |
| **Onboarding** | Intro screens for first launch | ✅ Done |

### Features to Add/Improve

| Feature | Priority | Description |
|---------|----------|-------------|
| **Login/Register** | 🔴 HIGH | Firebase Auth with email & Google |
| **User Profile** | 🔴 HIGH | Avatar, display name, stats, settings |
| **Cloud Sync** | 🟡 MEDIUM | Sync favorites/watchlist across devices |
| **Push Notifications** | 🟡 MEDIUM | New movies, trending alerts |
| **TV Shows Support** | 🟡 MEDIUM | Extend to support TV series |
| **Advanced Search** | 🟡 MEDIUM | Filter by genre, year, rating, actor |
| **Watch History** | 🟡 MEDIUM | Track watched movies |
| **Movie Reviews** | 🟢 LOW | User reviews and ratings |
| **Watch Providers** | 🟢 LOW | Streaming platform info |
| **Related Content** | 🟢 LOW | Movie news, articles |
| **Widgets** | 🟢 LOW | Home screen widgets |

### Feature Priority Matrix

```
        Technical Complexity
           Low        Medium        High
        ┌─────────┬─────────┬─────────┐
    Low │ Watch   │ Reviews │ Cloud   │
        │ History │         │ Sync    │
Impact  ├─────────┼─────────┼─────────┤
   High │ Profile │ Adv.    │ Login/  │
        │         │ Search  │ Register│
        └─────────┴─────────┴─────────┘
```

---

## 8. CODE GUIDELINES

### 8.1 Repository Pattern

```kotlin
// Domain Layer - Repository Interface
interface MovieRepository {
    fun getTrending(): Flow<Resource<List<Movie>>>
    fun getNowPlaying(): Flow<Resource<List<Movie>>>
    fun getUpcoming(): Flow<Resource<List<Movie>>>
    fun getMovieDetails(id: Int): Flow<Resource<MovieDetails>>
    fun searchMovies(query: String): Flow<Resource<List<Movie>>>
    suspend fun addToFavorites(movie: MovieDetails)
    suspend fun removeFromFavorites(movieId: Int)
    fun getFavorites(): Flow<List<MovieDetails>>
}

// Data Layer - Repository Implementation
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao
) : MovieRepository {
    
    fun getTrending(): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            // Try local first
            val localMovies = movieDao.getTrending()
            if (localMovies.isNotEmpty()) {
                emit(Resource.Success(localMovies))
            }
            
            // Fetch from network
            val response = apiService.getTrendingMovies()
            val movies = response.results
            
            // Cache locally
            movies.forEach { it.type = MovieType.TRENDING }
            movieDao.insertAll(movies)
            
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            // If local has data, emit it with error
            val localMovies = movieDao.getTrending()
            if (localMovies.isNotEmpty()) {
                emit(Resource.Success(localMovies))
            } else {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }.flowOn(Dispatchers.IO)
}
```

### 8.2 Resource Wrapper

```kotlin
// For handling Loading/Success/Error states
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
```

### 8.3 ViewModel with StateFlow

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _trendingMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val trendingMovies: StateFlow<Resource<List<Movie>>> = _trendingMovies.asStateFlow()
    
    private val _nowPlayingMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val nowPlayingMovies: StateFlow<Resource<List<Movie>>> = _nowPlayingMovies.asStateFlow()
    
    init {
        loadTrendingMovies()
        loadNowPlayingMovies()
    }
    
    fun loadTrendingMovies() {
        viewModelScope.launch {
            movieRepository.getTrending().collect { result ->
                _trendingMovies.value = result
            }
        }
    }
    
    fun refresh() {
        loadTrendingMovies()
        loadNowPlayingMovies()
    }
}
```

### 8.4 Fragment with State Collection

```kotlin
@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var movieAdapter: MovieAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }
    
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trendingMovies.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> {
                            resource.data?.let { movies ->
                                movieAdapter.submitList(movies)
                                showContent()
                            }
                        }
                        is Resource.Error -> {
                            showError(resource.message)
                        }
                    }
                }
            }
        }
    }
}
```

### 8.5 DI Module Example

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): Application = app
    
    @Provides
    @Named("tmdb_api_key")
    fun provideTmdbApiKey(): String = BuildConfig.TMDB_API_KEY
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): FilmyDatabase {
        return Room.databaseBuilder(
            app,
            FilmyDatabase::class.java,
            "filmy_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideMovieDao(database: FilmyDatabase): MovieDao = database.movieDao()
    
    @Provides
    @Singleton
    fun provideMovieDetailsDao(database: FilmyDatabase): MovieDetailsDao = database.movieDetailsDao()
}
```

### 8.6 Room DAO Example

```kotlin
@Dao
interface MovieDao {
    
    @Query("SELECT * FROM movies WHERE type = :type ORDER BY popularity DESC")
    fun getMoviesByType(type: Int): Flow<List<Movie>>
    
    @Query("SELECT * FROM movies WHERE type = 0 ORDER BY popularity DESC")
    fun getTrending(): List<Movie>
    
    @Query("SELECT * FROM movies WHERE type = 1 ORDER BY releaseDate DESC")
    fun getNowPlaying(): List<Movie>
    
    @Query("SELECT * FROM movies WHERE type = 2 ORDER BY releaseDate DESC")
    fun getUpcoming(): List<Movie>
    
    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY popularity DESC")
    fun searchMovies(query: String): Flow<List<Movie>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)
    
    @Delete
    suspend fun delete(movie: Movie)
    
    @Query("DELETE FROM movies")
    suspend fun deleteAll()
    
    @Query("DELETE FROM movies WHERE type = :type")
    suspend fun deleteByType(type: Int)
}
```

---

## 9. UI/UX IMPROVEMENTS

### 9.1 Modern Theme Setup

```xml
<!-- res/values/themes.xml -->
<resources>
    <!-- Base Theme -->
    <style name="Theme.FilmyPlus" parent="Theme.Material3.Light.NoActionBar">
        <!-- Primary colors -->
        <item name="colorPrimary">@color/md_theme_light_primary</item>
        <item name="colorOnPrimary">@color/md_theme_light_onPrimary</item>
        <item name="colorPrimaryContainer">@color/md_theme_light_primaryContainer</item>
        <item name="colorOnPrimaryContainer">@color/md_theme_light_onPrimaryContainer</item>
        
        <!-- Secondary colors -->
        <item name="colorSecondary">@color/md_theme_light_secondary</item>
        <item name="colorOnSecondary">@color/md_theme_light_onSecondary</item>
        <item name="colorSecondaryContainer">@color/md_theme_light_secondaryContainer</item>
        
        <!-- Tertiary colors -->
        <item name="colorTertiary">@color/md_theme_light_tertiary</item>
        <item name="colorOnTertiary">@color/md_theme_light_onTertiary</item>
        
        <!-- Background colors -->
        <item name="android:colorBackground">@color/md_theme_light_background</item>
        <item name="colorSurface">@color/md_theme_light_surface</item>
        <item name="colorSurfaceVariant">@color/md_theme_light_surfaceVariant</item>
        
        <!-- Error colors -->
        <item name="colorError">@color/md_theme_light_error</item>
        <item name="colorOnError">@color/md_theme_light_onError</item>
        
        <!-- Outline -->
        <item name="colorOutline">@color/md_theme_light_outline</item>
    </style>
    
    <!-- Splash Theme -->
    <style name="Theme.FilmyPlus.Splash" parent="Theme.SplashScreen">
        <item name="windowSplashScreenBackground">@color/md_theme_light_primary</item>
        <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
        <item name="postSplashScreenTheme">@style/Theme.FilmyPlus</item>
    </style>
</resources>
```

### 9.2 Material Design 3 Color Scheme

```xml
<!-- res/values/colors.xml -->
<resources>
    <!-- Light Theme -->
    <color name="md_theme_light_primary">#6750A4</color>
    <color name="md_theme_light_onPrimary">#FFFFFF</color>
    <color name="md_theme_light_primaryContainer">#EADDFF</color>
    <color name="md_theme_light_onPrimaryContainer">#21005D</color>
    
    <color name="md_theme_light_secondary">#625B71</color>
    <color name="md_theme_light_onSecondary">#FFFFFF</color>
    <color name="md_theme_light_secondaryContainer">#E8DEF8</color>
    <color name="md_theme_light_onSecondaryContainer">#1D192B</color>
    
    <color name="md_theme_light_tertiary">#7D5260</color>
    <color name="md_theme_light_onTertiary">#FFFFFF</color>
    <color name="md_theme_light_tertiaryContainer">#FFD8E4</color>
    <color name="md_theme_light_onTertiaryContainer">#31111D</color>
    
    <color name="md_theme_light_error">#B3261E</color>
    <color name="md_theme_light_onError">#FFFFFF</color>
    <color name="md_theme_light_errorContainer">#F9DEDC</color>
    <color name="md_theme_light_onErrorContainer">#410E0B</color>
    
    <color name="md_theme_light_background">#FFFBFE</color>
    <color name="md_theme_light_onBackground">#1C1B1F</color>
    <color name="md_theme_light_surface">#FFFBFE</color>
    <color name="md_theme_light_onSurface">#1C1B1F</color>
    <color name="md_theme_light_surfaceVariant">#E7E0EC</color>
    <color name="md_theme_light_onSurfaceVariant">#49454F</color>
    
    <color name="md_theme_light_outline">#79747E</color>
    <color name="md_theme_light_outlineVariant">#CAC4D0</color>
    
    <!-- Dark Theme -->
    <color name="md_theme_dark_primary">#D0BCFF</color>
    <color name="md_theme_dark_onPrimary">#381E72</color>
    <color name="md_theme_dark_primaryContainer">#4F378B</color>
    <color name="md_theme_dark_onPrimaryContainer">#EADDFF</color>
    
    <color name="md_theme_dark_secondary">#CCC2DC</color>
    <color name="md_theme_dark_onSecondary">#332D41</color>
    <color name="md_theme_dark_secondaryContainer">#4A4458</color>
    <color name="md_theme_dark_onSecondaryContainer">#E8DEF8</color>
    
    <color name="md_theme_dark_tertiary">#EFB8C8</color>
    <color name="md_theme_dark_onTertiary">#492532</color>
    <color name="md_theme_dark_tertiaryContainer">#633B48</color>
    <color name="md_theme_dark_onTertiaryContainer">#FFD8E4</color>
    
    <color name="md_theme_dark_error">#F2B8B5</color>
    <color name="md_theme_dark_onError">#601410</color>
    <color name="md_theme_dark_errorContainer">#8C1D18</color>
    <color name="md_theme_dark_onErrorContainer">#F9DEDC</color>
    
    <color name="md_theme_dark_background">#1C1B1F</color>
    <color name="md_theme_dark_onBackground">#E6E1E5</color>
    <color name="md_theme_dark_surface">#1C1B1F</color>
    <color name="md_theme_dark_onSurface">#E6E1E5</color>
    <color name="md_theme_dark_surfaceVariant">#49454F</color>
    <color name="md_theme_dark_onSurfaceVariant">#CAC4D0</color>
    
    <color name="md_theme_dark_outline">#938F99</color>
    <color name="md_theme_dark_outlineVariant">#49454F</color>
</resources>
```

### 9.3 Modern Movie Card Layout

```xml
<!-- res/layout/item_movie_card.xml -->
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="160dp"
    android:layout_height="wrap_content"
    android:layout_marginEnd="12dp"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    app:strokeWidth="0dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <!-- Poster Image -->
        <ImageView
            android:id="@+id/ivPoster"
            android:layout_width="match_parent"
            android:layout_height="240dp"
            android:scaleType="centerCrop"
            android:contentDescription="@string/poster"
            app:layout_constraintTop_toTopOf="parent"/>

        <!-- Rating Badge -->
        <com.google.android.material.card.MaterialCardView
            android:id="@+id/cvRating"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            app:cardBackgroundColor="?attr/colorPrimaryContainer"
            app:cardCornerRadius="8dp"
            app:cardElevation="0dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:gravity="center_vertical"
                android:orientation="horizontal"
                android:paddingHorizontal="6dp"
                android:paddingVertical="4dp">

                <ImageView
                    android:layout_width="12dp"
                    android:layout_height="12dp"
                    android:src="@drawable/ic_star"
                    app:tint="?attr/colorOnPrimaryContainer"
                    android:contentDescription="@string/rating"/>

                <TextView
                    android:id="@+id/tvRating"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="4dp"
                    android:textColor="?attr/colorOnPrimaryContainer"
                    android:textSize="11sp"
                    android:textStyle="bold"/>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Favorite Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnFavorite"
            style="@style/Widget.Material3.Button.IconButton"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:layout_margin="4dp"
            android:contentDescription="@string/favorite"
            app:icon="@drawable/ic_favorite_border"
            app:iconSize="20dp"
            app:iconTint="@android:color/white"
            app:layout_constraintBottom_toBottomOf="@id/ivPoster"
            app:layout_constraintEnd_toEndOf="parent"/>

        <!-- Gradient Overlay -->
        <View
            android:layout_width="match_parent"
            android:layout_height="80dp"
            android:background="@drawable/gradient_bottom"
            app:layout_constraintBottom_toBottomOf="@id/ivPoster"/>

        <!-- Title -->
        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ellipsize="end"
            android:maxLines="2"
            android:padding="12dp"
            android:paddingBottom="0dp"
            android:textAppearance="?attr/textAppearanceTitleSmall"
            app:layout_constraintTop_toBottomOf="@id/ivPoster"/>

        <!-- Year -->
        <TextView
            android:id="@+id/tvYear"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:alpha="0.6"
            android:paddingHorizontal="12dp"
            android:paddingBottom="12dp"
            android:textAppearance="?attr/textAppearanceBodySmall"
            app:layout_constraintTop_toBottomOf="@id/tvTitle"
            app:layout_constraintStart_toStartOf="parent"/>

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

### 9.4 Shimmer Loading Effect

```kotlin
// ShimmerAdapter wrapper for RecyclerView
class ShimmerMovieAdapter : RecyclerView.Adapter<ShimmerMovieAdapter.ViewHolder>() {
    
    private var isLoading = true
    
    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShimmerMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(isLoading)
    }
    
    inner class ViewHolder(private val binding: ItemShimmerMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(isLoading: Boolean) {
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.stopShimmer()
            }
        }
    }
}
```

### 9.5 Collapsing Toolbar for Details

```xml
<!-- res/layout/activity_movie_details.xml -->
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBarLayout"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:fitsSystemWindows="true"
        android:theme="@style/ThemeOverlay.Material3.Dark.ActionBar">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/collapsingToolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fitsSystemWindows="true"
            app:contentScrim="?attr/colorPrimary"
            app:expandedTitleGravity="bottom|start"
            app:expandedTitleMarginBottom="72dp"
            app:expandedTitleMarginStart="16dp"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:collapsedTitleTextAppearance="@style/TextAppearance.Material3.TitleLarge"
            app:expandedTitleTextAppearance="@style/TextAppearance.Material3.HeadlineLarge">

            <ImageView
                android:id="@+id/ivBackdrop"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:contentDescription="@string/backdrop"
                android:fitsSystemWindows="true"
                android:scaleType="centerCrop"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.7"/>

            <View
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/gradient_backdrop"
                app:layout_collapseMode="parallax"/>

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin"
                app:navigationIcon="@drawable/ic_arrow_back"/>

        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <!-- Movie details content -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">
            
            <!-- Rating, Runtime, Year -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">
                
                <TextView
                    android:id="@+id/tvRating"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"/>
                
                <TextView
                    android:id="@+id/tvRuntime"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="16dp"/>
                
                <TextView
                    android:id="@+id/tvYear"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="16dp"/>
            </LinearLayout>
            
            <!-- Genres -->
            <com.google.android.material.chip.ChipGroup
                android:id="@+id/chipGroupGenres"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"/>
            
            <!-- Overview -->
            <TextView
                android:id="@+id/tvOverview"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"/>
            
            <!-- Cast -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="24dp"
                android:text="@string/cast"
                android:textAppearance="?attr/textAppearanceTitleMedium"/>
            
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rvCast"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:clipToPadding="false"/>
            
            <!-- Trailer -->
            <com.google.android.material.card.MaterialCardView
                android:id="@+id/cardTrailer"
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:layout_marginTop="24dp"
                app:cardCornerRadius="12dp">
                
                <ImageView
                    android:id="@+id/ivTrailer"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:scaleType="centerCrop"/>
                
                <ImageView
                    android:layout_width="64dp"
                    android:layout_height="64dp"
                    android:layout_gravity="center"
                    android:src="@drawable/ic_play_circle"
                    android:contentDescription="@string/play_trailer"/>
            </com.google.android.material.card.MaterialCardView>
            
            <!-- Similar Movies -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="24dp"
                android:text="@string/similar_movies"
                android:textAppearance="?attr/textAppearanceTitleMedium"/>
            
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rvSimilar"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:clipToPadding="false"/>
                
        </LinearLayout>
    </androidx.core.widget.NestedScrollView>

    <!-- FAB for favorite -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabFavorite"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:contentDescription="@string/favorite"
        app:srcCompat="@drawable/ic_favorite_border"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

---

## 10. AUTHENTICATION GUIDE

### 10.1 Firebase Setup

1. Create Firebase project at https://console.firebase.google.com
2. Add Android app with your package name
3. Download `google-services.json` to `app/`
4. Enable Authentication:
   - Email/Password
   - Google Sign-In
5. Create Firestore Database:
   - Start in test mode
   - Add security rules for authenticated users

### 10.2 Auth Service

```kotlin
@Singleton
class FirebaseAuthService @Inject constructor() {
    
    private val auth: FirebaseAuth = Firebase.auth
    
    val currentUser: FirebaseUser? = auth.currentUser
    
    val isLoggedIn: Boolean get() = currentUser != null
    
    fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Login failed"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.message ?: "Login failed"))
        }
    }
    
    fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Update display name
                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profile).await()
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Registration failed"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.message ?: "Registration failed"))
        }
    }
    
    fun loginWithGoogle(idToken: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Google login failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Google login failed"))
        }
    }
    
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to send reset email"))
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }
}

private fun FirebaseUser.toUser() = User(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString()
)
```

### 10.3 Auth Repository

```kotlin
interface AuthRepository {
    val currentUser: User?
    val isLoggedIn: Boolean
    
    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(email: String, password: String, displayName: String): Flow<Resource<User>>
    fun loginWithGoogle(idToken: String): Flow<Resource<User>>
    fun logout()
    fun observeAuthState(): Flow<User?>
}

class AuthRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService
) : AuthRepository {
    
    override val currentUser: User?
        get() = authService.currentUser?.toUser()
    
    override val isLoggedIn: Boolean
        get() = authService.isLoggedIn
    
    override fun login(email: String, password: String): Flow<Resource<User>> =
        authService.loginWithEmail(email, password)
    
    override fun register(email: String, password: String, displayName: String): Flow<Resource<User>> =
        authService.registerWithEmail(email, password, displayName)
    
    override fun loginWithGoogle(idToken: String): Flow<Resource<User>> =
        authService.loginWithGoogle(idToken)
    
    override fun logout() = authService.logout()
    
    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser?.toUser()
            trySend(user)
        }
        authService.addAuthStateListener(listener)
        awaitClose { authService.removeAuthStateListener(listener) }
    }
}
```

### 10.4 Auth ViewModel

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Resource<User>>(Resource.Idle())
    val loginState: StateFlow<Resource<User>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<User>>(Resource.Idle())
    val registerState: StateFlow<Resource<User>> = _registerState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        observeAuthState()
    }
    
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { user ->
                _currentUser.value = user
            }
        }
    }
    
    fun login(email: String, password: String) {
        if (!validateEmail(email)) {
            _loginState.value = Resource.Error("Invalid email")
            return
        }
        if (password.length < 6) {
            _loginState.value = Resource.Error("Password must be at least 6 characters")
            return
        }
        
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                _loginState.value = result
            }
        }
    }
    
    fun register(email: String, password: String, displayName: String) {
        if (!validateEmail(email)) {
            _registerState.value = Resource.Error("Invalid email")
            return
        }
        if (password.length < 6) {
            _registerState.value = Resource.Error("Password must be at least 6 characters")
            return
        }
        if (displayName.isBlank()) {
            _registerState.value = Resource.Error("Display name is required")
            return
        }
        
        viewModelScope.launch {
            authRepository.register(email, password, displayName).collect { result ->
                _registerState.value = result
            }
        }
    }
    
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken).collect { result ->
                _loginState.value = result
            }
        }
    }
    
    fun logout() {
        authRepository.logout()
        _loginState.value = Resource.Idle()
        _registerState.value = Resource.Idle()
    }
    
    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun resetLoginState() {
        _loginState.value = Resource.Idle()
    }
    
    fun resetRegisterState() {
        _registerState.value = Resource.Idle()
    }
}
```

### 10.5 Login Screen

```kotlin
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupGoogleSignIn()
        setupClickListeners()
        observeState()
    }
    
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }
        
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
        
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            binding.btnLogin.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            navigateToHome()
                        }
                        is Resource.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            showError(state.message)
                        }
                        is Resource.Idle -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                        }
                    }
                }
            }
        }
    }
    
    private fun signInWithGoogle() {
        googleSignInClient.signInIntent.also { intent ->
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { viewModel.loginWithGoogle(it) }
            } catch (e: ApiException) {
                showError("Google sign in failed: ${e.message}")
            }
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
    
    private fun showError(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
    }
    
    companion object {
        private const val RC_SIGN_IN = 1001
    }
}
```

---

## 11. LAYOUT FILES REFERENCE

### 11.1 Main Activity Layout

```xml
<!-- res/layout/activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/navHostFragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavigation"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavigation"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:menu="@menu/bottom_nav_menu"
        app:labelVisibilityMode="labeled"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 11.2 Bottom Navigation Menu

```xml
<!-- res/menu/bottom_nav_menu.xml -->
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    
    <item
        android:id="@+id/home"
        android:icon="@drawable/ic_home"
        android:title="@string/home"/>
    
    <item
        android:id="@+id/search"
        android:icon="@drawable/ic_search"
        android:title="@string/search"/>
    
    <item
        android:id="@+id/collections"
        android:icon="@drawable/ic_collections"
        android:title="@string/collections"/>
    
    <item
        android:id="@+id/profile"
        android:icon="@drawable/ic_person"
        android:title="@string/profile"/>
    
    <item
        android:id="@+id/settings"
        android:icon="@drawable/ic_settings"
        android:title="@string/settings"/>
        
</menu>
```

### 11.3 Navigation Graph

```xml
<!-- res/navigation/nav_graph.xml -->
<?xml version="1.0" encoding="utf-8"?>
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/home">

    <fragment
        android:id="@+id/home"
        android:name="com.yourapp.filmyplus.ui.home.HomeFragment"
        android:label="@string/home"
        tools:layout="@layout/fragment_home">
        
        <action
            android:id="@+id/action_home_to_details"
            app:destination="@id/movieDetails"/>
    </fragment>

    <fragment
        android:id="@+id/search"
        android:name="com.yourapp.filmyplus.ui.search.SearchFragment"
        android:label="@string/search"
        tools:layout="@layout/fragment_search">
        
        <action
            android:id="@+id/action_search_to_details"
            app:destination="@id/movieDetails"/>
    </fragment>

    <fragment
        android:id="@+id/collections"
        android:name="com.yourapp.filmyplus.ui.collections.CollectionsFragment"
        android:label="@string/collections"
        tools:layout="@layout/fragment_collections">
        
        <action
            android:id="@+id/action_collections_to_details"
            app:destination="@id/movieDetails"/>
    </fragment>

    <fragment
        android:id="@+id/profile"
        android:name="com.yourapp.filmyplus.ui.profile.ProfileFragment"
        android:label="@string/profile"
        tools:layout="@layout/fragment_profile"/>
        
    <fragment
        android:id="@+id/settings"
        android:name="com.yourapp.filmyplus.ui.settings.SettingsFragment"
        android:label="@string/settings"
        tools:layout="@layout/fragment_settings"/>

    <fragment
        android:id="@+id/movieDetails"
        android:name="com.yourapp.filmyplus.ui.details.MovieDetailsFragment"
        android:label="Movie Details"
        tools:layout="@layout/fragment_movie_details">
        
        <argument
            android:name="movieId"
            app:argType="integer"/>
    </fragment>

</navigation>
```

---

## 12. PRE-BUILD CHECKLIST

### Setup Tasks

- [ ] **Android Studio Setup**
  - [ ] Install Android Studio Hedgehog (2023.1.1) or newer
  - [ ] Install Kotlin plugin
  - [ ] Install Hilt plugin
  - [ ] Install KSP plugin

- [ ] **Firebase Setup**
  - [ ] Create Firebase project
  - [ ] Add Android app (package name)
  - [ ] Download `google-services.json`
  - [ ] Enable Authentication (Email/Password, Google)
  - [ ] Enable Firestore Database
  - [ ] Enable Analytics
  - [ ] Enable Crashlytics

- [ ] **TMDB API Setup**
  - [ ] Create TMDB account
  - [ ] Request API key
  - [ ] Add API key to `secrets.properties`

- [ ] **Google Sign-In Setup**
  - [ ] Create OAuth 2.0 client ID (Web application type)
  - [ ] Add Web Client ID to Firebase project
  - [ ] Add SHA-1 fingerprint to Firebase project

### Project Configuration

- [ ] **Gradle Files**
  - [ ] `settings.gradle` - repository and plugin configuration
  - [ ] `build.gradle` (project level) - version catalog
  - [ ] `build.gradle` (app level) - dependencies and config
  - [ ] `gradle.properties` - memory and JVM settings
  - [ ] `local.properties` - SDK path
  - [ ] `secrets.properties` - API keys

- [ ] **Android Manifest**
  - [ ] Package name
  - [ ] App name
  - [ ] Permissions (INTERNET, ACCESS_NETWORK_STATE)
  - [ ] Activity declarations
  - [ ] App theme

- [ ] **Resource Files**
  - [ ] `colors.xml` - Material Design 3 colors
  - [ ] `strings.xml` - All strings
  - [ ] `dimens.xml` - Dimensions
  - [ ] `themes.xml` - Light theme
  - [ ] `themes.xml` (night) - Dark theme
  - [ ] `drawables/` - Icons and shapes
  - [ ] `navigation/nav_graph.xml` - Navigation graph

### Code Implementation

- [ ] **Data Layer**
  - [ ] Room Database
  - [ ] Entities
  - [ ] DAOs
  - [ ] Type Converters
  - [ ] Retrofit API Service
  - [ ] Response Models (DTOs)
  - [ ] Repositories

- [ ] **Domain Layer**
  - [ ] Repository interfaces
  - [ ] Use cases

- [ ] **DI Layer**
  - [ ] Hilt modules
  - [ ] Application class
  - [ ] Singleton components

- [ ] **UI Layer**
  - [ ] Activities/Fragments
  - [ ] ViewModels
  - [ ] Adapters
  - [ ] Layouts

- [ ] **Auth**
  - [ ] Firebase Auth Service
  - [ ] Auth Repository
  - [ ] Auth ViewModel
  - [ ] Login Activity
  - [ ] Register Activity

### Features Checklist

- [ ] **Core Features**
  - [ ] Home screen (Trending, Now Playing, Upcoming)
  - [ ] Movie details
  - [ ] Search
  - [ ] Favorites
  - [ ] Watchlist
  - [ ] Dark mode

- [ ] **Auth Features**
  - [ ] Login with email
  - [ ] Register with email
  - [ ] Login with Google
  - [ ] Password reset
  - [ ] Logout
  - [ ] Profile screen

- [ ] **Cloud Features**
  - [ ] Cloud sync favorites
  - [ ] Cloud sync watchlist
  - [ ] User preferences sync

### Security Checklist

- [ ] **API Keys**
  - [ ] Store in `secrets.properties`
  - [ ] Never commit to git
  - [ ] Use BuildConfig for access

- [ ] **ProGuard/R8**
  - [ ] Enable minification
  - [ ] Add Keep rules for models
  - [ ] Add Keep rules for Retrofit
  - [ ] Test release build

- [ ] **Firebase Security**
  - [ ] Firestore rules (authenticated users only)
  - [ ] Storage rules

### Testing Checklist

- [ ] **Unit Tests**
  - [ ] Repository tests
  - [ ] ViewModel tests
  - [ ] Use case tests

- [ ] **UI Tests**
  - [ ] Navigation tests
  - [ ] Screen tests

- [ ] **Build Verification**
  - [ ] Debug build succeeds
  - [ ] Release build succeeds
  - [ ] APK generated correctly

### Release Checklist

- [ ] **App Signing**
  - [ ] Generate keystore
  - [ ] Configure signing in `build.gradle`
  - [ ] Store keystore securely

- [ ] **Play Store**
  - [ ] Create listing
  - [ ] Add screenshots
  - [ ] Write description
  - [ ] Set up pricing
  - [ ] Content rating
  - [ ] Create production release

- [ ] **Post-Release**
  - [ ] Monitor Crashlytics
  - [ ] Monitor Analytics
  - [ ] Set up crash reporting alerts
  - [ ] Create update plan

---

## APPENDIX: VERSION COMPATIBILITY MATRIX

| Library | Version | Min SDK | Notes |
|---------|---------|---------|-------|
| Kotlin | 1.9.22 | - | Latest stable |
| Android Gradle Plugin | 8.2.2 | - | Android Studio Hedgehog |
| Hilt | 2.51.1 | 24 | - |
| Room | 2.6.1 | 24 | KSP support |
| Navigation | 2.8.3 | 24 | - |
| Lifecycle | 2.8.7 | 24 | - |
| Coroutines | 1.8.1 | 24 | - |
| Retrofit | 2.11.0 | 24 | - |
| OkHttp | 4.12.0 | 24 | - |
| Coil | 2.7.0 | 24 | Kotlin-first |
| Paging | 3.3.5 | 24 | - |
| DataStore | 1.1.1 | 24 | - |
| Firebase BOM | 33.6.0 | 24 | - |
| Material | 1.12.0 | 24 | MD3 support |
| WorkManager | 2.9.1 | 24 | - |

---

## APPENDIX: FILE NAMING CONVENTIONS

| Component | Convention | Example |
|-----------|------------|---------|
| Activity | `*Activity.kt` | `LoginActivity.kt` |
| Fragment | `*Fragment.kt` | `HomeFragment.kt` |
| ViewModel | `*ViewModel.kt` | `HomeViewModel.kt` |
| Adapter | `*Adapter.kt` | `MovieAdapter.kt` |
| Layout | `activity_*.xml` | `activity_login.xml` |
| Layout (Fragment) | `fragment_*.xml` | `fragment_home.xml` |
| Layout (Item) | `item_*.xml` | `item_movie.xml` |
| Navigation | `nav_graph.xml` | `nav_graph.xml` |
| Menu | `*_menu.xml` | `bottom_nav_menu.xml` |
| Entity | `*.kt` (in entity/) | `Movie.kt` |
| DAO | `*Dao.kt` | `MovieDao.kt` |
| Repository | `*Repository.kt` | `MovieRepository.kt` |
| API Service | `*Service.kt` | `TmdbApiService.kt` |
| Module | `*Module.kt` | `DatabaseModule.kt` |

---

## APPENDIX: COMMON ERRORS & SOLUTIONS

### Error: "API key not found"
**Solution:** Add API key to `secrets.properties`:
```properties
TMDB_API_KEY=your_api_key_here
```

### Error: "Firebase not initialized"
**Solution:** Ensure `google-services.json` is in `app/` folder and sync project.

### Error: "Hilt not generating components"
**Solution:** 
1. Clean and rebuild project
2. Ensure `@AndroidEntryPoint` annotation is on Activities/Fragments
3. Ensure `@InstallIn` annotation is on modules

### Error: "Room schema export failed"
**Solution:** Add to `build.gradle`:
```groovy
kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}
```

### Error: "Navigation graph not found"
**Solution:** Ensure navigation graph is in `res/navigation/` folder and correctly referenced in `NavHostFragment`.

---

*Document Version: 1.0*
*Last Updated: May 2026*
*Project: Filmy (Movie Database App)*
