package com.example.languagelearning.presentation.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.R
import com.example.languagelearning.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.button.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim()

            var isValid = true

            if (firstName.isBlank()) {
                binding.tvFirstNameError.text = "Enter your first name"
                binding.tvFirstNameError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.tvFirstNameError.visibility = View.GONE
            }

            if (lastName.isBlank()) {
                binding.tvLastNameError.text = "Enter your last name"
                binding.tvLastNameError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.tvLastNameError.visibility = View.GONE
            }

            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tvEmailError.text = "Enter a valid email address"
                binding.tvEmailError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.tvEmailError.visibility = View.GONE
            }

            if (isValid) {
                viewModel.validateAndSetPersonalData(firstName, lastName, email)
                findNavController().navigate(
                    R.id.action_registrationFragment2_to_passwordChooseFragment,
                )
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registrationFragment2_to_loginFragment2,
            )
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
