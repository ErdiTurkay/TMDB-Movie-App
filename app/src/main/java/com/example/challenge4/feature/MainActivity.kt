package com.example.challenge4.feature

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challenge4.NavGraphDirections
import com.example.challenge4.R
import com.example.challenge4.databinding.ActivityMainBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNav: BottomNavigationView
    lateinit var toolBar: MaterialToolbar
    lateinit var searchView: SearchView
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Challenge4)
        binding = ActivityMainBinding.inflate(layoutInflater)

        bottomNav = binding.bottomNavigation
        toolBar = binding.topAppBar

        // Fragment transitions are provided according to the clicks in the Bottom Navigation.
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)

        val searchItem = toolBar.menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView

        setSearchListener()
        setToolBarClicks()

        setContentView(binding.root)
    }

    private fun setSearchListener() {
        // Search in ToolBar is listened and Search Fragment is opened.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val destination = NavGraphDirections.actionGlobalSearchFragment(searchView.query.toString())
                navController.navigate(destination)
                searchView.onActionViewCollapsed()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun setToolBarClicks() {
        // In case where RecyclerView Layout is changed, layoutManager is changed.
        toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.linear -> {
                    viewModel.setLayoutCount(LayoutEnum.LINEAR)
                    true
                }
                R.id.two_span -> {
                    viewModel.setLayoutCount(LayoutEnum.GRID_2)
                    true
                } R.id.three_span -> {
                    viewModel.setLayoutCount(LayoutEnum.GRID_3)
                    true
                } else -> false
            }
        }
    }

    fun showLayoutMenu(boolean: Boolean) {
        // It shows or hides the menu items according to the boolean parameter it receives.
        toolBar.menu.run {
            if (boolean) {
                findItem(R.id.two_span).show()
                findItem(R.id.three_span).show()
                findItem(R.id.linear).show()
            } else {
                findItem(R.id.two_span).hide()
                findItem(R.id.three_span).hide()
                findItem(R.id.linear).hide()
            }
        }
    }

    fun getLayout(): RecyclerView.LayoutManager {
        // Returns the LayoutManager according to the selected toolBar menu.
        return when (viewModel.layoutManager.value) {
            LayoutEnum.GRID_2 -> {
                GridLayoutManager(applicationContext, 2)
            }
            LayoutEnum.GRID_3 -> {
                GridLayoutManager(applicationContext, 3)
            }
            LayoutEnum.LINEAR -> {
                LinearLayoutManager(applicationContext)
            }
            else -> {
                GridLayoutManager(applicationContext, 2)
            }
        }
    }

    fun openYoutubeVideo(videoID: String) {
        // According to the key it receives, opens the YouTube Player intent.
        val intent = Intent(this, YoutubeActivity::class.java)
        intent.putExtra("key", videoID)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // If backstack is empty when back key is pressed, it will prompt to close the app.
        if (navHostFragment.childFragmentManager.backStackEntryCount == 0) {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.are_you_sure))
                .setMessage(resources.getString(R.string.close_app_text))
                .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    exitProcess(0)
                }
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
