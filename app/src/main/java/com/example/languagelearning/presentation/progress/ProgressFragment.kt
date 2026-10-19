package com.example.languagelearning.presentation.progress

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
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.databinding.FragmentProgressBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProgressViewModel by viewModels()

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

        observeViewModel()
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
                    viewModel.state.collect { state ->
                        when (state) {
                            is ProgressState.Loading -> {}
                            is ProgressState.Success -> {}
                            is ProgressState.Error -> {}
                        }
                    }
                }
            }
        }
    }

    private fun updateChart(weeklyTime: WeeklyStudyTime) {
        val maxSeconds = listOf(
            weeklyTime.mon, weeklyTime.tue, weeklyTime.wed,
            weeklyTime.thu, weeklyTime.fri, weeklyTime.sat, weeklyTime.sun, 1200
        ).maxOrNull() ?: 1200

        val density = resources.displayMetrics.density
        val maxBarHeightDp = 100f

        fun setBarData(barView: View, badgeView: TextView, seconds: Int) {
            val heightDp = ((seconds.toFloat() / maxSeconds) * maxBarHeightDp).coerceAtLeast(15f)
            val params = barView.layoutParams
            params.height = (heightDp * density).toInt()
            barView.layoutParams = params

            if (seconds > 0) {
                val mins = seconds / 60
                badgeView.text = if (mins > 0) "${mins}m" else "${seconds}s"
                badgeView.visibility = View.VISIBLE
            } else {
                badgeView.visibility = View.INVISIBLE
            }
        }

        setBarData(binding.vBarMon, binding.tvBadgeMon, weeklyTime.mon)
        setBarData(binding.vBarTue, binding.tvBadgeTue, weeklyTime.tue)
        setBarData(binding.vBarWed, binding.tvBadgeWed, weeklyTime.wed)
        setBarData(binding.vBarThu, binding.tvBadgeThu, weeklyTime.thu)
        setBarData(binding.vBarFri, binding.tvBadgeFri, weeklyTime.fri)
        setBarData(binding.vBarSat, binding.tvBadgeSat, weeklyTime.sat)
        setBarData(binding.vBarSun, binding.tvBadgeSun, weeklyTime.sun)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
