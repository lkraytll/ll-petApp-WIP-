package com.example.languagelearning.presentation.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import coil.size.Scale
import com.example.languagelearning.R
import com.example.languagelearning.data.local.StudyTimeTracker
import com.example.languagelearning.databinding.FragmentQuizMeaningBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizMeaningFragment : Fragment() {

    private var _binding: FragmentQuizMeaningBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    @Inject
    lateinit var studyTimeTracker: StudyTimeTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizMeaningBinding.inflate(inflater, container, false)
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

        val checkAndNavigate = { optionIndex: Int ->
            val question = viewModel.translationByImageQuestions.value.firstOrNull()
            val questionId = question?.id ?: 1
            val optionId = question?.options?.getOrNull(optionIndex)?.id

            viewModel.checkQuestionAnswer(questionId = questionId, optionId = optionId) { isCorrect, _ ->
                val sheet = AnswerFeedbackBottomSheet(isCorrect = isCorrect) {
                    if (isCorrect) {
                        findNavController().navigate(R.id.action_quizMeaningFragment_to_quizImageFragment)
                    }
                }
                sheet.show(parentFragmentManager, AnswerFeedbackBottomSheet.TAG)
            }
        }

        binding.rbAnswer1.setOnClickListener { checkAndNavigate(0) }
        binding.tvAnswer1.setOnClickListener { checkAndNavigate(0) }
        binding.rbAnswer2.setOnClickListener { checkAndNavigate(1) }
        binding.tvAnswer2.setOnClickListener { checkAndNavigate(1) }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.translationByImageQuestions.collect { list ->
                        val question = list.firstOrNull()
                        if (question != null) {
                            if (question.prompt.isNotBlank()) {
                                binding.tvMeaningPrompt.text = question.prompt
                            }

                            if (!question.imageUrl.isNullOrBlank()) {
                                val fullUrl = if (question.imageUrl.startsWith("http")) {
                                    question.imageUrl
                                } else {
                                    "http://10.0.2.2:3000${if (question.imageUrl.startsWith("/")) "" else "/"}${question.imageUrl}"
                                }

                                binding.ivMeaningImage.load(fullUrl) {
                                    crossfade(true)
                                    scale(Scale.FILL)
                                    allowHardware(false)
                                }
                            }

                            val opts = question.options
                            if (opts.isNotEmpty()) {
                                if (opts.size > 0 && opts[0].text != null) binding.tvAnswer1.text = opts[0].text
                                if (opts.size > 1 && opts[1].text != null) binding.tvAnswer2.text = opts[1].text
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showExitWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exit_lesson_title))
            .setMessage(getString(R.string.exit_lesson_message))
            .setPositiveButton(getString(R.string.exit)) { _, _ ->
                findNavController().navigate(R.id.action_global_mainMenuFragment)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
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
