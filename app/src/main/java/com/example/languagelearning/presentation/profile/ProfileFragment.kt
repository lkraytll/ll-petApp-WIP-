package com.example.languagelearning.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.languagelearning.R
import com.example.languagelearning.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = copyUriToFile(uri)
            if (file != null && file.exists()) {
                Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .into(binding.ivLogo)
                viewModel.uploadAvatarFile(file)
            }
        }
    }

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

        binding.ivLogo.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserProfile()
    }

    private fun observeViewModel() {
        binding.tvTotalHoursLabel.text = getString(R.string.study_time_label)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { user ->
                        if (user != null) {
                            val cleanName = user.name.replace("null", "").trim()
                            binding.tvAgencyName.text = if (cleanName.isNotBlank()) cleanName else user.email.substringBefore("@")
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

                launch {
                    viewModel.levelInfo.collect { lvl ->
                        binding.tvUserLevel.text =
                            "⭐ Level ${lvl.level} | ⚡ ${lvl.xp} XP"

                        val avatarUrl = lvl.avatarUrl

                        if (!avatarUrl.isNullOrBlank()) {
                            val imageSource: Any = when {
                                avatarUrl.startsWith("http://") ||
                                        avatarUrl.startsWith("https://") -> {
                                    avatarUrl
                                }

                                avatarUrl.startsWith("/") -> {
                                    "http://10.0.2.2:3000$avatarUrl"
                                }

                                else -> {
                                    File(avatarUrl).takeIf { it.exists() }
                                        ?: return@collect
                                }
                            }

                            Glide.with(this@ProfileFragment)
                                .load(imageSource)
                                .circleCrop()
                                .into(binding.ivLogo)
                        }
                    }
                }

                launch {
                    viewModel.avatarUploadMessage.collect { msg ->
                        if (!msg.isNullOrBlank()) {
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }
                    }
                }
            }
        }
    }

    private fun copyUriToFile(uri: Uri): File? {
        return try {
            val context = requireContext()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (_: Exception) {
            null
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
