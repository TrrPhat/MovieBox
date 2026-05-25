package com.app.moviebox.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.moviebox.R
import com.app.moviebox.databinding.LoginActivityBinding
import com.app.moviebox.domain.model.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { viewModel.loginWithGoogle(it) }
        } catch (e: ApiException) {
            showError("Đăng nhập Google thất bại: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
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
            val email = binding.tilEmail.editText?.text?.toString()?.trim() ?: ""
            val password = binding.tilPassword.editText?.text?.toString() ?: ""
            viewModel.login(email, password)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnFacebook.setOnClickListener {
            showError("Đăng nhập Facebook sẽ sớm ra mắt")
        }

        binding.tvForgot.setOnClickListener {
            showResetPasswordDialog()
        }

        binding.tvRegister.setOnClickListener {
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
                            binding.btnGoogle.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnGoogle.isEnabled = true
                            navigateToMain()
                        }
                        is Resource.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnGoogle.isEnabled = true
                            showError(state.message)
                            viewModel.resetLoginState()
                        }
                        is Resource.Idle -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnGoogle.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val editText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Đặt lại mật khẩu")
            .setMessage("Chúng tôi sẽ gửi email hướng dẫn đặt lại mật khẩu đến email của bạn.")
            .setView(dialogView)
            .setPositiveButton("Gửi") { _, _ ->
                val email = editText?.text?.toString()?.trim() ?: ""
                if (email.isNotEmpty()) {
                    viewModel.sendPasswordReset(email)
                    observeResetPasswordState()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun observeResetPasswordState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPasswordState.collect { state ->
                    when (state) {
                        is Resource.Success -> {
                            showError("Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư.")
                            viewModel.resetPasswordResetState()
                        }
                        is Resource.Error -> {
                            showError(state.message)
                            viewModel.resetPasswordResetState()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, com.app.moviebox.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(android.graphics.Color.parseColor("#B3261E"))
            .setTextColor(android.graphics.Color.WHITE)
            .show()
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}
