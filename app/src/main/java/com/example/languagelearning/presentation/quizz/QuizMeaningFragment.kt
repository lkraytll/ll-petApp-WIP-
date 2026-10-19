package com.example.languagelearning.presentation.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.example.languagelearning.R
import com.example.languagelearning.data.local.StudyTimeTracker
import com.example.languagelearning.databinding.FragmentQuizMeaningBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizMeaningFragment : Fragment() {

    private var _binding: FragmentQuizMeaningBinding? = null
    private val binding get() = _binding!!

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

        val navigateToNext = {
            findNavController().navigate(R.id.action_quizMeaningFragment_to_quizImageFragment)
        }

        binding.rbAnswer1.setOnClickListener { navigateToNext() }
        binding.rbAnswer2.setOnClickListener { navigateToNext() }
        binding.tvAnswer1.setOnClickListener { navigateToNext() }
        binding.tvAnswer2.setOnClickListener { navigateToNext() }
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
