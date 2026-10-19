package com.example.languagelearning.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.databinding.FragmentProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveProfile.setOnClickListener {
            val fName = binding.etFirstName.text.toString()
            val lName = binding.etLastName.text.toString()
            val email = binding.etEmail.text.toString()

            viewModel.updateProfile(fName, lName, email)
        }

        binding.btnChangePassword.setOnClickListener {
            val currentP = binding.etCurrentPassword.text.toString()
            val newP = binding.etNewPassword.text.toString()

            viewModel.changePassword(currentP, newP)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collect { user ->
                        if (user != null) {
                            if (binding.etFirstName.text.isNullOrEmpty()) {
                                binding.etFirstName.setText(user.firstName)
                            }
                            if (binding.etLastName.text.isNullOrEmpty()) {
                                binding.etLastName.setText(user.lastName ?: "")
                            }
                            if (binding.etEmail.text.isNullOrEmpty()) {
                                binding.etEmail.setText(user.email)
                            }
                        }
                    }
                }

                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is ProfileEditState.Idle -> {
                                binding.btnSaveProfile.isEnabled = true
                                binding.btnChangePassword.isEnabled = true
                            }

                            is ProfileEditState.Loading -> {
                                binding.btnSaveProfile.isEnabled = false
                                binding.btnChangePassword.isEnabled = false
                                binding.tvProfileError.visibility = View.GONE
                                binding.tvPasswordError.visibility = View.GONE
                            }

                            is ProfileEditState.ProfileUpdated -> {
                                binding.btnSaveProfile.isEnabled = true
                                binding.btnChangePassword.isEnabled = true
                                binding.tvProfileError.visibility = View.GONE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetState()
                            }

                            is ProfileEditState.PasswordChanged -> {
                                binding.btnSaveProfile.isEnabled = true
                                binding.btnChangePassword.isEnabled = true
                                binding.tvPasswordError.visibility = View.GONE
                                binding.etCurrentPassword.setText("")
                                binding.etNewPassword.setText("")
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetState()
                            }

                            is ProfileEditState.Error -> {
                                binding.btnSaveProfile.isEnabled = true
                                binding.btnChangePassword.isEnabled = true
                                binding.tvProfileError.text = state.message
                                binding.tvProfileError.visibility = View.VISIBLE
                                viewModel.resetState()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
