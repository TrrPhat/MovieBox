package com.app.moviebox.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moviebox.domain.model.Resource
import com.app.moviebox.domain.model.User
import com.app.moviebox.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

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

    private val _resetPasswordState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val resetPasswordState: StateFlow<Resource<Unit>> = _resetPasswordState.asStateFlow()

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
        val emailError = validateEmail(email)
        if (emailError != null) {
            _loginState.value = Resource.Error(emailError)
            return
        }

        if (password.isEmpty()) {
            _loginState.value = Resource.Error("Vui lòng nhập mật khẩu")
            return
        }

        if (password.length < 6) {
            _loginState.value = Resource.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                _loginState.value = result
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank()) {
            _registerState.value = Resource.Error("Vui lòng nhập họ tên")
            return
        }

        val emailError = validateEmail(email)
        if (emailError != null) {
            _registerState.value = Resource.Error(emailError)
            return
        }

        if (password.isEmpty()) {
            _registerState.value = Resource.Error("Vui lòng nhập mật khẩu")
            return
        }

        if (password.length < 6) {
            _registerState.value = Resource.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        if (password != confirmPassword) {
            _registerState.value = Resource.Error("Mật khẩu xác nhận không khớp")
            return
        }

        viewModelScope.launch {
            authRepository.register(email, password, name).collect { result ->
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

    fun sendPasswordReset(email: String) {
        val emailError = validateEmail(email)
        if (emailError != null) {
            _resetPasswordState.value = Resource.Error(emailError)
            return
        }

        viewModelScope.launch {
            authRepository.sendPasswordReset(email).collect { result ->
                _resetPasswordState.value = result
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _loginState.value = Resource.Idle()
        _registerState.value = Resource.Idle()
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "Vui lòng nhập email"
        }
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        if (!emailPattern.matcher(email).matches()) {
            return "Email không hợp lệ"
        }
        return null
    }

    fun resetLoginState() {
        _loginState.value = Resource.Idle()
    }

    fun resetRegisterState() {
        _registerState.value = Resource.Idle()
    }

    fun resetPasswordResetState() {
        _resetPasswordState.value = Resource.Idle()
    }
}
