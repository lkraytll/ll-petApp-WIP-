package com.example.languagelearning.presentation.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.R
import com.example.languagelearning.databinding.FragmentPasswordChooseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordChooseFragment : Fragment() {

    private var _binding: FragmentPasswordChooseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPasswordChooseBinding.inflate(inflater, container, false)
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

        binding.bRegSignup.setOnClickListener {
            val password = binding.etRegPassword.text.toString()
            val confirmPassword = binding.etRegConfirmPassword.text.toString()
            viewModel.register(password, confirmPassword)
        }

        binding.tvRegLogin.setOnClickListener {
            findNavController().navigate(R.id.action_passwordChooseFragment_to_loginFragment2)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is RegistrationState.Idle -> {
                            binding.bRegSignup.isEnabled = true
                            binding.tvRegPasswordError.visibility = View.GONE
                        }

                        is RegistrationState.Loading -> {
                            binding.bRegSignup.isEnabled = false
                            binding.tvRegPasswordError.visibility = View.GONE
                        }

                        is RegistrationState.Error -> {
                            binding.bRegSignup.isEnabled = true
                            binding.tvRegPasswordError.text = state.message
                            binding.tvRegPasswordError.visibility = View.VISIBLE
                        }

                        is RegistrationState.Success -> {
                            binding.bRegSignup.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "Registration successful! Welcome ${state.user.firstName}",
                                Toast.LENGTH_SHORT,
                            ).show()
                            viewModel.resetState()
                            findNavController().navigate(R.id.action_passwordChooseFragment_to_mainMenuFragment)
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
