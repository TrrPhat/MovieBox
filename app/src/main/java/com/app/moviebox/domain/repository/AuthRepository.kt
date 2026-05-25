package com.app.moviebox.domain.repository

import com.app.moviebox.domain.model.Resource
import com.app.moviebox.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?
    val isLoggedIn: Boolean

    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(email: String, password: String, displayName: String): Flow<Resource<User>>
    fun loginWithGoogle(idToken: String): Flow<Resource<User>>
    fun sendPasswordReset(email: String): Flow<Resource<Unit>>
    fun logout()
    fun observeAuthState(): Flow<User?>
}
