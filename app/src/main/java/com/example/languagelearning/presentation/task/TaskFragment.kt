package com.example.languagelearning.presentation.task

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.languagelearning.R
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.databinding.FragmentTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()

    private var selectedGoalDays = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoalSelection()
        setupListeners()
        observeViewModel()
    }

    private fun setupGoalSelection() {
        val goalViews = mapOf(
            3 to binding.tvGoal3,
            4 to binding.tvGoal4,
            5 to binding.tvGoal5,
            6 to binding.tvGoal6,
            7 to binding.tvGoal7,
        )

        goalViews.forEach { (days, textView) ->
            textView.setOnClickListener {
                selectedGoalDays = days
                highlightSelectedGoal(goalViews, days)
            }
        }
    }

    private fun highlightSelectedGoal(goalViews: Map<Int, TextView>, selectedDays: Int) {
        goalViews.forEach { (days, textView) ->
            if (days == selectedDays) {
                textView.setBackgroundResource(R.drawable.bg_goal_selected)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.bg_goal)
                textView.setTextColor(Color.parseColor("#4B4B59"))
            }
        }
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.setGoal(selectedGoalDays)
            Toast.makeText(
                requireContext(),
                "Новая цель установлена: $selectedGoalDays дней в неделю!",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goalInfo.collect { goal ->
                    selectedGoalDays = goal.targetDays

                    binding.progressGoal.max = goal.targetDays
                    binding.progressGoal.progress = goal.completedDays.coerceAtMost(goal.targetDays)

                    binding.tvGoalProgress.text = "${goal.completedDays} / ${goal.targetDays} дн."
                    binding.tvStreak.text = "${goal.streakDays} дн."

                    val goalViews = mapOf(
                        3 to binding.tvGoal3,
                        4 to binding.tvGoal4,
                        5 to binding.tvGoal5,
                        6 to binding.tvGoal6,
                        7 to binding.tvGoal7,
                    )

                    updateLockState(goal, goalViews)
                }
            }
        }
    }

    private fun updateLockState(goal: GoalInfo, goalViews: Map<Int, TextView>) {
        val isLocked = goal.completedDays < goal.targetDays

        if (isLocked) {
            binding.llLockBanner.visibility = View.VISIBLE
            binding.llLockBanner.setBackgroundResource(R.drawable.bg_lock_banner)
            binding.tvLockIcon.text = "🔒"
            binding.tvLockMessage.text = "Выбор заблокирован! Выполните текущую цель (${goal.completedDays}/${goal.targetDays} дн.), чтобы изменить её."
            binding.scrollGoals.alpha = 0.5f
            binding.btnContinue.isEnabled = false
            binding.btnContinue.text = "🔒 Цель заблокирована"
            binding.btnContinue.alpha = 0.6f

            goalViews.forEach { (days, textView) ->
                textView.isClickable = false
                if (days == goal.targetDays) {
                    textView.setBackgroundResource(R.drawable.bg_goal_locked)
                    textView.setTextColor(Color.parseColor("#333333"))
                    textView.text = "$days\n🔒"
                } else {
                    textView.setBackgroundResource(R.drawable.bg_goal)
                    textView.setTextColor(Color.parseColor("#888899"))
                    textView.text = "$days\nDays"
                }
            }
        } else {
            binding.llLockBanner.visibility = View.VISIBLE
            binding.llLockBanner.setBackgroundResource(R.drawable.bg_reward)
            binding.tvLockIcon.text = "🎉"
            binding.tvLockMessage.text = "Поздравляем! Ваша текущая цель выполнена. Вы можете выбрать новую цель!"
            binding.scrollGoals.alpha = 1.0f
            binding.btnContinue.isEnabled = true
            binding.btnContinue.text = "Сохранить новую цель"
            binding.btnContinue.alpha = 1.0f

            goalViews.forEach { (days, textView) ->
                textView.isClickable = true
                textView.text = "$days\nDays"
            }
            highlightSelectedGoal(goalViews, goal.targetDays)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
