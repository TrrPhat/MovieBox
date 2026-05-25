package com.app.moviebox.data.repository

import com.app.moviebox.domain.model.Resource
import com.app.moviebox.domain.model.User
import com.app.moviebox.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService
) : AuthRepository {

    override val currentUser: User?
        get() = authService.currentUser?.let { firebaseUser ->
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
        }

    override val isLoggedIn: Boolean
        get() = authService.isLoggedIn

    override fun login(email: String, password: String): Flow<Resource<User>> =
        authService.loginWithEmail(email, password)

    override fun register(email: String, password: String, displayName: String): Flow<Resource<User>> =
        authService.registerWithEmail(email, password, displayName)

    override fun loginWithGoogle(idToken: String): Flow<Resource<User>> =
        authService.loginWithGoogle(idToken)

    override fun sendPasswordReset(email: String): Flow<Resource<Unit>> =
        authService.sendPasswordResetEmail(email)

    override fun logout() = authService.logout()

    override fun observeAuthState(): Flow<User?> = authService.observeAuthState()
}
