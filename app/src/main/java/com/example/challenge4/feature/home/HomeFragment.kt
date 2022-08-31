package com.example.challenge4.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.example.challenge4.R
import com.example.challenge4.common.MovieAdapter
import com.example.challenge4.common.MovieClickListener
import com.example.challenge4.databinding.FragmentHomeBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.feature.MainActivity
import com.example.challenge4.model.Movie
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), MovieClickListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        setSwipeRefresh()
        setToolBar()
        setImageSlider()
        setCardViewTitles()
        setRVs()
        setButtonClicks()
        observeInternetConnection()

        return binding.root
    }

    private fun setSwipeRefresh() {
        // The fragment is refreshed.
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.initialize()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setToolBar() {
        // Toolbar has been made visible and its title has been changed.
        mainActivity.toolBar.run {
            show()
            mainActivity.showLayoutMenu(false)
            title = resources.getString(R.string.app_name)
        }
    }

    private fun setImageSlider() {
        // It feeds the ImageSlider with data from the API.
        viewModel.imageSliderLiveData.observe(viewLifecycleOwner) {
            binding.imageSlider.setImageList(it, ScaleTypes.CENTER_CROP)
            binding.shimmerImageSlider.hide()
            binding.imageSlider.show()
        }
    }

    private fun setCardViewTitles() {
        // Titles of CardViews are set.
        binding.includedPopular.title.text = resources.getString(R.string.popular_movies)
        binding.includedTopRated.title.text = resources.getString(R.string.top_rated_movies)
        binding.includedUpcoming.title.text = resources.getString(R.string.upcoming_movies)
    }

    private fun setRVs() {
        binding.includedPopular.run {
            // Adapter set for popular movies RV.
            val popularMovieAdapter = MovieAdapter(listener = this@HomeFragment, showNumber = true, isLarge = false)
            recyclerView.adapter = popularMovieAdapter

            // LiveData observed to update popular movies RV.
            viewModel.popularMoviesLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    popularMovieAdapter.setMovieList(it)
                    shimmer.hide()
                }
                recyclerView.show()
            }
        }

        binding.includedTopRated.run {
            // Adapter set for top rated movies RV.
            val topRatedMovieAdapter = MovieAdapter(listener = this@HomeFragment, showNumber = true, isLarge = false)
            recyclerView.adapter = topRatedMovieAdapter

            // LiveData observed to update top rated movies RV.
            viewModel.topRatedMoviesLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    topRatedMovieAdapter.setMovieList(it)
                    shimmer.hide()
                }
                recyclerView.show()
            }
        }

        binding.includedUpcoming.run {
            // Adapter set for upcoming movies RV.
            val upcomingMovieAdapter = MovieAdapter(listener = this@HomeFragment, showNumber = false, isLarge = false)
            recyclerView.adapter = upcomingMovieAdapter

            // LiveData observed to update upcoming movies RV.
            viewModel.upcomingMoviesLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    upcomingMovieAdapter.setMovieList(it)
                    shimmer.hide()
                }
                recyclerView.show()
            }
        }

        viewModel.checkFavorite()
    }

    private fun setButtonClicks() {
        // When the View All button is clicked, fragment transition is provided.
        binding.includedPopular.allMoviesButton.setOnClickListener {
            mainActivity.bottomNav.selectedItemId = R.id.popularFragment
        }

        // When the View All button is clicked, fragment transition is provided.
        binding.includedTopRated.allMoviesButton.setOnClickListener {
            mainActivity.bottomNav.selectedItemId = R.id.topRatedFragment
        }

        // When the View All button is clicked, fragment transition is provided.
        binding.includedUpcoming.allMoviesButton.setOnClickListener {
            mainActivity.bottomNav.selectedItemId = R.id.upcomingFragment
        }
    }

    private fun observeInternetConnection() {
        // Dialog is displayed in case of no internet connection.
        viewModel.isOnline.observe(viewLifecycleOwner) {
            if (!it) {
                showInternetError()
            }
        }
    }

    private fun showInternetError() {
        // Material Alert Dialog for Internet Connection
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.offline))
            .setNegativeButton(resources.getString(R.string.see_favorites)) { _, _ ->
                mainActivity.bottomNav.selectedItemId = R.id.favoriteFragment
            }
            .setPositiveButton(resources.getString(R.string.try_again)) { _, _ ->
                viewModel.initialize()
            }
            .show()
    }

    override fun movieOnClick(movie: Movie) {
        // If any movie is clicked, the detail page of that movie is opened.
        val destination = HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(movie)
        Navigation.findNavController(requireView()).navigate(destination)
    }

    override fun changeFavorite(movie: Movie) {
        // The Snack text has been updated according to whether isFavorite field of the movie is true/false.
        val text = if (!movie.isFavorite) {
            movie.title.plus(" ") + resources.getString(R.string.added_favorites)
        } else {
            movie.title.plus(" ") + resources.getString(R.string.removed_favorites)
        }

        // An information message is displayed on the screen.
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_SHORT
        ).setAction(resources.getString(R.string.close)) {
        }.show()

        // The ViewModel function is called, this function processes whether the movie is a favorite or not.
        viewModel.changeFavorite(movie)
    }
}
