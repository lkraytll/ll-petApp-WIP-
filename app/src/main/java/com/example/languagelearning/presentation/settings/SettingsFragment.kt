package com.example.languagelearning.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.R
import com.example.languagelearning.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.clEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileEditFragment)
        }

        binding.clGeneralSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show()
        }

        binding.clMyLanguages.setOnClickListener {
            Toast.makeText(requireContext(), "My Languages: Ingush, English", Toast.LENGTH_SHORT).show()
        }

        binding.clHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Help & Support", Toast.LENGTH_SHORT).show()
        }

        binding.clLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out") { _, _ ->
                    viewModel.logout()
                    findNavController().navigate(R.id.action_global_loginFragment2)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
