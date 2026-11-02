package com.example.languagelearning.presentation.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.languagelearning.R
import com.example.languagelearning.databinding.DialogAnswerFeedbackBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AnswerFeedbackBottomSheet(
    private val isCorrect: Boolean,
    private val onActionClicked: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogAnswerFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAnswerFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isCorrect) {
            binding.llFeedbackRoot.setBackgroundResource(R.drawable.bg_feedback_correct)
            binding.tvFeedbackTitle.text = "Correct Answer!"
            binding.btnFeedbackAction.text = "Next"
            binding.btnFeedbackAction.setTextColor(android.graphics.Color.parseColor("#5072FF"))
        } else {
            binding.llFeedbackRoot.setBackgroundResource(R.drawable.bg_feedback_wrong)
            binding.tvFeedbackTitle.text = "Wrong Answer!"
            binding.btnFeedbackAction.text = "Try again"
            binding.btnFeedbackAction.setTextColor(android.graphics.Color.parseColor("#E52E63"))
        }

        binding.btnFeedbackAction.setOnClickListener {
            dismiss()
            onActionClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AnswerFeedbackBottomSheet"
    }
}
