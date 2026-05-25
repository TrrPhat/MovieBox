package com.app.moviebox.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.moviebox.databinding.FragmentProfileBinding
import com.app.moviebox.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser
        user?.let {
            binding.tvUserName.text = it.displayName ?: "Người dùng MovieBox"
            binding.tvUserEmail.text = it.email ?: ""
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }

        binding.btnSettings.setOnClickListener {
            // Settings navigation - future
        }

        binding.btnAbout.setOnClickListener {
            // About - future
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
