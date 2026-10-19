package com.example.languagelearning

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.languagelearning.presentation.task.TaskFragment
import com.example.languagelearning.presentation.mainMenu.MainMenuFragment
import com.example.languagelearning.presentation.profile.ProfileFragment
import com.example.languagelearning.presentation.progress.ProgressFragment

class MainPagerAdapter(
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainMenuFragment()
            1 -> TaskFragment()
            2 -> ProgressFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException(
                "Unknown position: $position"
            )
        }
    }
}