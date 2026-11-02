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
import com.example.languagelearning.R
import com.example.languagelearning.data.local.StudyTimeTracker
import com.example.languagelearning.databinding.FragmentQuizSelectWordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizSelectWordFragment : Fragment() {

    private var _binding: FragmentQuizSelectWordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    @Inject
    lateinit var studyTimeTracker: StudyTimeTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizSelectWordBinding.inflate(inflater, container, false)
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

        binding.soundButton.setOnClickListener {
            val textToSpeak = viewModel.multipleChoiceQuestions.value.firstOrNull()?.prompt
                ?: viewModel.multipleChoiceQuestions.value.firstOrNull()?.correctAnswer
                ?: "Ӏаьржа"
            viewModel.speak(textToSpeak)
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.stopSpeech()

            val question = viewModel.multipleChoiceQuestions.value.firstOrNull()
            val questionId = question?.id ?: 1
            val options = question?.options ?: emptyList()

            val selectedOptionId = when (binding.rgAnswers.checkedRadioButtonId) {
                R.id.rb_variant_1 -> options.getOrNull(0)?.id
                R.id.rb_variant_2 -> options.getOrNull(1)?.id
                R.id.rb_variant_3 -> options.getOrNull(2)?.id
                R.id.rb_variant_4 -> options.getOrNull(3)?.id
                else -> null
            }

            viewModel.checkQuestionAnswer(questionId = questionId, optionId = selectedOptionId) { isCorrect, _ ->
                val sheet = AnswerFeedbackBottomSheet(isCorrect = isCorrect) {
                    if (isCorrect) {
                        findNavController().navigate(R.id.action_quizSelectWordFragment_to_quizMeaningFragment)
                    }
                }
                sheet.show(parentFragmentManager, AnswerFeedbackBottomSheet.TAG)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.multipleChoiceQuestions.collect { list ->
                        val firstQuestion = list.firstOrNull()
                        if (firstQuestion != null) {
                            if (firstQuestion.prompt.isNotBlank()) {
                                binding.tvTextToImageHint.text = firstQuestion.prompt
                                viewModel.speak(firstQuestion.prompt)
                            }
                            val opts = firstQuestion.options
                            if (opts.isNotEmpty()) {
                                val radioButtons = listOf(
                                    binding.rbVariant1,
                                    binding.rbVariant2,
                                    binding.rbVariant3,
                                    binding.rbVariant4
                                )
                                radioButtons.forEachIndexed { index, rb ->
                                    val option = opts.getOrNull(index)
                                    if (option != null) {
                                        rb.text = option.text ?: "Option ${index + 1}"
                                        rb.visibility = View.VISIBLE
                                    } else {
                                        rb.visibility = View.GONE
                                    }
                                }
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
