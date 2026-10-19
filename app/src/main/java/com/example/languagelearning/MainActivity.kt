package com.example.languagelearning

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.languagelearning.data.local.TokenManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var tabLayout: TabLayout
    private lateinit var navController: NavController

    private val tabTitles = listOf("Home", "Courses", "Progress", "Profile")
    private val tabIcons = listOf(
        R.drawable.ic_home,
        R.drawable.ic_courses,
        R.drawable.ic_progress,
        R.drawable.ic_profile,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tabLayout = findViewById(R.id.tabLayout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupTabs()
        setupNavigation()
    }

    private fun setupTabs() {
        tabTitles.forEachIndexed { index, title ->
            val tab = tabLayout.newTab()
            val tabView = layoutInflater.inflate(R.layout.tab_item, tabLayout, false)
            val iconView = tabView.findViewById<ImageView>(R.id.tabIcon)
            val textView = tabView.findViewById<TextView>(R.id.tabText)

            iconView.setImageResource(tabIcons[index])
            textView.text = title
            tab.customView = tabView
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> if (navController.currentDestination?.id != R.id.mainMenuFragment) {
                        navController.navigate(R.id.mainMenuFragment)
                    }
                    1 -> if (navController.currentDestination?.id != R.id.taskFragment) {
                        navController.navigate(R.id.taskFragment)
                    }
                    2 -> if (navController.currentDestination?.id != R.id.progressFragment) {
                        navController.navigate(R.id.progressFragment)
                    }
                    3 -> if (navController.currentDestination?.id != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupNavigation() {
        lifecycleScope.launch {
            val token = tokenManager.tokenFlow.firstOrNull()
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            if (!token.isNullOrEmpty()) {
                navGraph.setStartDestination(R.id.mainMenuFragment)
            } else {
                navGraph.setStartDestination(R.id.loginFragment2)
            }
            navController.graph = navGraph
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainMenuFragment -> {
                    tabLayout.visibility = View.VISIBLE
                    selectTabSilently(0)
                }
                R.id.taskFragment -> {
                    tabLayout.visibility = View.VISIBLE
                    selectTabSilently(1)
                }
                R.id.progressFragment -> {
                    tabLayout.visibility = View.VISIBLE
                    selectTabSilently(2)
                }
                R.id.profileFragment -> {
                    tabLayout.visibility = View.VISIBLE
                    selectTabSilently(3)
                }
                else -> {
                    tabLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun selectTabSilently(position: Int) {
        val tab = tabLayout.getTabAt(position)
        if (tab != null && tabLayout.selectedTabPosition != position) {
            tab.select()
        }
        for (i in 0 until tabLayout.tabCount) {
            val t = tabLayout.getTabAt(i)
            t?.customView?.isSelected = (i == position)
        }
    }
}
