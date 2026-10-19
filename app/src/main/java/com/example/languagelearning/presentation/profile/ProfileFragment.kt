package com.example.languagelearning.presentation.profile

import android.os.Bundle
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
import com.example.languagelearning.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        binding.tvTotalHoursLabel.text = getString(R.string.study_time_label)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { user ->
                        if (user != null) {
                            if (user.name.isNotBlank()) {
                                binding.tvAgencyName.text = user.name
                            }
                            if (user.email.isNotBlank()) {
                                binding.tvJoinDate.text = user.email
                            }
                        }
                    }
                }

                launch {
                    viewModel.studyTimeSeconds.collect { totalSeconds ->
                        binding.tvTotalHoursValue.text = formatStudyTime(totalSeconds)
                    }
                }
            }
        }
    }

    private fun formatStudyTime(totalSeconds: Int): String {
        if (totalSeconds < 60) {
            return "$totalSeconds sec"
        }
        val minutes = totalSeconds / 60
        val hours = minutes / 60
        val remMinutes = minutes % 60

        return if (hours > 0) {
            "$hours h $remMinutes min"
        } else {
            "$minutes min"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
