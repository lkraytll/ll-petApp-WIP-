package com.example.languagelearning.presentation.mainMenu

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
import com.example.languagelearning.databinding.FragmentMainMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCourses()
    }

    private fun setupListeners() {
        binding.pgCurrentLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_quizSelectWordFragment)
        }

        binding.cardWeeklyGoal.setOnClickListener {
            if (navControllerCanNavigate()) {
                findNavController().navigate(R.id.taskFragment)
            }
        }
    }

    private fun navControllerCanNavigate(): Boolean {
        return findNavController().currentDestination?.id == R.id.mainMenuFragment
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { user ->
                        if (user != null) {
                            val displayName = user.name.replace("null", "").trim()
                            binding.tvGreeting.text = if (displayName.isNotBlank()) "Hello, $displayName" else "Hello, Learner"
                        }
                    }
                }

                launch {
                    viewModel.completedLessonsCount.collect { count ->
                        binding.progressCircle.progressCircle.max = 20
                        binding.progressCircle.progressCircle.setProgress(count, true)
                        binding.progressCircle.tvProgress.text = "$count/20"
                    }
                }

                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is MainMenuState.Loading -> {}
                            is MainMenuState.Success -> {
                                val firstCourse = state.courses.firstOrNull()
                                if (firstCourse != null) {
                                    binding.tvCourseTitle.text = firstCourse.title
                                }
                            }
                            is MainMenuState.Error -> {}
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
