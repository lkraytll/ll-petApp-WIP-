package com.example.languagelearning.presentation.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.languagelearning.databinding.FragmentQuizImageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizImageFragment : Fragment() {

    private var _binding: FragmentQuizImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    private var selectedOptionId: Int? = null

    @Inject
    lateinit var studyTimeTracker: StudyTimeTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizImageBinding.inflate(inflater, container, false)
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
            val textToSpeak = viewModel.imageByTextQuestions.value.firstOrNull()?.prompt ?: "Ӏаьржа"
            viewModel.speak(textToSpeak)
        }

        binding.btnSubmitImage.setOnClickListener {
            val question = viewModel.imageByTextQuestions.value.firstOrNull()
            val questionId = question?.id ?: 1
            val optionIdToSubmit = selectedOptionId ?: question?.options?.firstOrNull()?.id ?: 1

            viewModel.checkQuestionAnswer(questionId = questionId, optionId = optionIdToSubmit) { isCorrect, _ ->
                val sheet = AnswerFeedbackBottomSheet(isCorrect = isCorrect) {
                    if (isCorrect) {
                        findNavController().navigate(R.id.action_quizImageFragment_to_quizTranslateFragment)
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
                    viewModel.imageByTextQuestions.collect { list ->
                        val question = list.firstOrNull()
                        if (question != null) {
                            if (question.prompt.isNotBlank()) {
                                binding.tvImagePrompt.text = question.prompt
                                viewModel.speak(question.prompt)
                            }

                            val opts = question.options
                            val cards = listOf(
                                Triple(binding.cardImage1, binding.ivImage1, binding.tvImageLabel1),
                                Triple(binding.cardImage2, binding.ivImage2, binding.tvImageLabel2),
                                Triple(binding.cardImage3, binding.ivImage3, binding.tvImageLabel3),
                                Triple(binding.cardImage4, binding.ivImage4, binding.tvImageLabel4),
                            )

                            cards.forEachIndexed { index, (cardLayout, imageView, textView) ->
                                val option = opts.getOrNull(index)
                                if (option != null) {
                                    cardLayout.visibility = View.VISIBLE
                                    textView.text = option.text ?: ""

                                    if (!option.imageUrl.isNullOrBlank()) {
                                        val fullUrl = if (option.imageUrl.startsWith("http")) {
                                            option.imageUrl
                                        } else {
                                            "http://10.0.2.2:3000${if (option.imageUrl.startsWith("/")) "" else "/"}${option.imageUrl}"
                                        }

                                        // Image loading via Coil
                                        imageView.load(fullUrl) {
                                            crossfade(true)
                                            scale(Scale.FILL)
                                            allowHardware(false) // Preserve crisp uncompressed 32-bit PNG quality
                                        }
                                    }

                                    cardLayout.setOnClickListener {
                                        selectedOptionId = option.id
                                        highlightSelectedCard(cards, cardLayout)
                                    }
                                } else {
                                    cardLayout.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun highlightSelectedCard(
        cards: List<Triple<LinearLayout, ImageView, TextView>>,
        selectedCard: LinearLayout
    ) {
        cards.forEach { (card, _, _) ->
            if (card == selectedCard) {
                card.setBackgroundResource(R.drawable.bg_goal_selected)
            } else {
                card.setBackgroundResource(R.drawable.card_background)
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
