package com.example.languagelearning.presentation.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.R
import com.example.languagelearning.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.bLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            var isValid = true

            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tvLoginEmailError.text = "Enter a valid email address"
                binding.tvLoginEmailError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.tvLoginEmailError.visibility = View.GONE
            }

            if (password.isBlank()) {
                binding.tvLoginPasswordError.text = "Enter your password"
                binding.tvLoginPasswordError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.tvLoginPasswordError.visibility = View.GONE
            }

            if (isValid) {
                viewModel.login(email, password)
            }
        }

        binding.bRegister.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment2_to_registrationFragment2,
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is LoginState.Idle -> {
                            binding.bLogin.isEnabled = true
                        }

                        is LoginState.Loading -> {
                            binding.bLogin.isEnabled = false
                        }

                        is LoginState.Error -> {
                            binding.bLogin.isEnabled = true
                            binding.tvLoginEmailError.text = state.message
                            binding.tvLoginEmailError.visibility = View.VISIBLE
                            viewModel.resetState()
                        }

                        is LoginState.Success -> {
                            binding.bLogin.isEnabled = true
                            findNavController().navigate(R.id.action_loginFragment2_to_mainMenuFragment)
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
