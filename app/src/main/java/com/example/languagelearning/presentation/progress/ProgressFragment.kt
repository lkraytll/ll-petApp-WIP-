package com.example.languagelearning.presentation.progress

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.languagelearning.R
import com.example.languagelearning.adapters.CompletedTaskAdapter
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.databinding.FragmentProgressBinding
import com.example.languagelearning.presentation.items.TaskItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProgressViewModel by viewModels()
    private lateinit var taskAdapter: CompletedTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserProgress()
    }

    private fun setupRecyclerView() {
        taskAdapter = CompletedTaskAdapter()
        binding.rvCompletedTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCompletedTasks.adapter = taskAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weeklyStudyTime.collect { timeMap ->
                        updateChart(timeMap)
                    }
                }

                launch {
                    viewModel.dailyProgress.collect { daily ->
                        if (daily != null) {
                            val mins = if (daily.timeMinutes > 0) daily.timeMinutes else daily.timeSeconds / 60
                            binding.tvThisWeek.text = "${daily.day ?: "Today"}: ${mins}m"
                        }
                    }
                }

                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is ProgressState.Loading -> {}
                            is ProgressState.Success -> {
                                val completedCount = state.progressList.count { it.completed }
                                val totalCount = state.progressList.size.coerceAtLeast(1)
                                binding.tvCourseProgress.text = "$completedCount / $totalCount lessons"
                                binding.courseProgressBar.max = totalCount
                                binding.courseProgressBar.progress = completedCount

                                val taskItems = state.progressList.mapIndexed { index, p ->
                                    TaskItem(
                                        id = p.id,
                                        title = "Lesson ${p.lessonId}",
                                        lesson = "Score: ${p.score ?: 100}",
                                        isComplete = p.completed
                                    )
                                }
                                taskAdapter.submitList(taskItems)
                            }
                            is ProgressState.Error -> {}
                        }
                    }
                }
            }
        }
    }

    private fun updateChart(weeklyTime: WeeklyStudyTime) {
        val maxSeconds = maxOf(
            weeklyTime.mon, weeklyTime.tue, weeklyTime.wed,
            weeklyTime.thu, weeklyTime.fri, weeklyTime.sat, weeklyTime.sun
        )

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val density = resources.displayMetrics.density
        val maxBarHeightDp = 100f

        fun setBarData(barView: View, badgeView: TextView, seconds: Int, dayOfWeek: Int) {
            val isCurrentDay = (dayOfWeek == currentDay)

            if (isCurrentDay) {
                barView.setBackgroundResource(R.drawable.selected_chart_bar)
                badgeView.setBackgroundResource(R.drawable.selected_chart_bar)
                badgeView.setTextColor(Color.WHITE)
            } else {
                barView.setBackgroundResource(R.drawable.chart_bar)
                badgeView.background = null
                badgeView.setTextColor(Color.parseColor("#333333"))
            }

            if (maxSeconds <= 0 || seconds <= 0) {
                val params = barView.layoutParams
                params.height = (4f * density).toInt() // subtle baseline indicator
                barView.layoutParams = params
                badgeView.visibility = View.INVISIBLE
                return
            }

            val heightDp = (seconds.toFloat() / maxSeconds.toFloat()) * maxBarHeightDp
            val params = barView.layoutParams
            params.height = (heightDp * density).toInt().coerceAtLeast((8f * density).toInt())
            barView.layoutParams = params

            val mins = seconds / 60
            badgeView.text = if (mins > 0) "${mins}m" else "${seconds}s"
            badgeView.visibility = View.VISIBLE
        }

        setBarData(binding.vBarMon, binding.tvBadgeMon, weeklyTime.mon, Calendar.MONDAY)
        setBarData(binding.vBarTue, binding.tvBadgeTue, weeklyTime.tue, Calendar.TUESDAY)
        setBarData(binding.vBarWed, binding.tvBadgeWed, weeklyTime.wed, Calendar.WEDNESDAY)
        setBarData(binding.vBarThu, binding.tvBadgeThu, weeklyTime.thu, Calendar.THURSDAY)
        setBarData(binding.vBarFri, binding.tvBadgeFri, weeklyTime.fri, Calendar.FRIDAY)
        setBarData(binding.vBarSat, binding.tvBadgeSat, weeklyTime.sat, Calendar.SATURDAY)
        setBarData(binding.vBarSun, binding.tvBadgeSun, weeklyTime.sun, Calendar.SUNDAY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
