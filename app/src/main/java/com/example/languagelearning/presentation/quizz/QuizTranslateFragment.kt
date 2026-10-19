package com.example.languagelearning.presentation.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.languagelearning.R
import com.example.languagelearning.data.local.StudyTimeTracker
import com.example.languagelearning.databinding.FragmentQuizTranslateBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizTranslateFragment : Fragment() {

    private var _binding: FragmentQuizTranslateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()

    @Inject
    lateinit var studyTimeTracker: StudyTimeTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitWarningDialog()
                }
            },
        )

        binding.backIcon.setOnClickListener {
            showExitWarningDialog()
        }

        binding.ivClose.setOnClickListener {
            showExitWarningDialog()
        }

        binding.btnSubmitTranslate.setOnClickListener {
            viewModel.completeLesson(lessonId = 1, isCompleted = true, score = 100)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is QuizState.Idle -> {
                            binding.btnSubmitTranslate.isEnabled = true
                        }

                        is QuizState.Submitting -> {
                            binding.btnSubmitTranslate.isEnabled = false
                        }

                        is QuizState.Completed -> {
                            binding.btnSubmitTranslate.isEnabled = true
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Поздравляем! 🎉")
                                .setMessage("Урок успешно завершен! Ваш результат сохранен.")
                                .setPositiveButton("Отлично") { _, _ ->
                                    findNavController().navigate(R.id.action_quizTranslateFragment_to_mainMenuFragment)
                                }
                                .setCancelable(false)
                                .show()
                        }

                        is QuizState.Error -> {
                            binding.btnSubmitTranslate.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showExitWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выйти из урока?")
            .setMessage("Ваш текущий прогресс в уроке будет потерян. Вы уверены, что хотите выйти?")
            .setPositiveButton("Выйти") { _, _ ->
                findNavController().navigate(R.id.action_global_mainMenuFragment)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        studyTimeTracker.startSession()
    }

    override fun onPause() {
        super.onPause()
        studyTimeTracker.stopAndSaveSession(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
