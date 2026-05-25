package com.app.moviebox.ui.auth

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
import com.app.moviebox.databinding.RegisterActivityBinding
import com.app.moviebox.domain.model.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
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
        binding = RegisterActivityBinding.inflate(layoutInflater)
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
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.tilName.editText?.text?.toString()?.trim() ?: ""
            val email = binding.tilEmail.editText?.text?.toString()?.trim() ?: ""
            val password = binding.tilPassword.editText?.text?.toString() ?: ""
            val confirmPassword = binding.tilConfirmPassword.editText?.text?.toString() ?: ""

            if (!binding.cbTerms.isChecked) {
                showError("Vui lòng đồng ý với Điều khoản dịch vụ")
                return@setOnClickListener
            }

            viewModel.register(name, email, password, confirmPassword)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnFacebook.setOnClickListener {
            showError("Đăng ký bằng Facebook sẽ sớm ra mắt")
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            binding.btnRegister.isEnabled = false
                            binding.btnGoogle.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
                            binding.btnGoogle.isEnabled = true
                            showSuccess("Đăng ký thành công! Vui lòng đăng nhập.")
                            viewModel.resetRegisterState()
                            finish()
                        }
                        is Resource.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
                            binding.btnGoogle.isEnabled = true
                            showError(state.message)
                            viewModel.resetRegisterState()
                        }
                        is Resource.Idle -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
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

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(android.graphics.Color.parseColor("#B3261E"))
            .setTextColor(android.graphics.Color.WHITE)
            .show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(android.graphics.Color.parseColor("#4CAF50"))
            .setTextColor(android.graphics.Color.WHITE)
            .show()
    }
}
