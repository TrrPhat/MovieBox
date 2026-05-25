package com.app.moviebox.data.repository

import com.app.moviebox.domain.model.Resource
import com.app.moviebox.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: com.google.firebase.auth.FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = currentUser != null

    fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Đăng nhập thất bại"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(getFirebaseErrorMessage(e.errorCode)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đăng nhập thất bại"))
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
                val profile = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profile).await()
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Đăng ký thất bại"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(getFirebaseErrorMessage(e.errorCode)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đăng ký thất bại"))
        }
    }

    fun loginWithGoogle(idToken: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Đăng nhập Google thất bại"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(getFirebaseErrorMessage(e.errorCode)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đăng nhập Google thất bại"))
        }
    }

    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(Unit))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(getFirebaseErrorMessage(e.errorCode)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gửi email đặt lại mật khẩu thất bại"))
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser?.toUser()
            trySend(user)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private fun com.google.firebase.auth.FirebaseUser.toUser() = User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )

    private fun getFirebaseErrorMessage(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_CREDENTIAL",
            "ERROR_WRONG_PASSWORD" -> "Mật khẩu không đúng"
            "ERROR_USER_NOT_FOUND",
            "ERROR_INVALID_EMAIL" -> "Email không tồn tại"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email đã được sử dụng"
            "ERROR_WEAK_PASSWORD" -> "Mật khẩu phải có ít nhất 6 ký tự"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Lỗi kết nối mạng"
            "ERROR_TOO_MANY_REQUESTS" -> "Quá nhiều yêu cầu. Vui lòng thử lại sau"
            "ERROR_USER_DISABLED" -> "Tài khoản đã bị vô hiệu hóa"
            "ERROR_OPERATION_NOT_ALLOWED" -> "Đăng nhập bằng email/password chưa được bật"
            else -> "Đã xảy ra lỗi. Vui lòng thử lại"
        }
    }
}
