package com.example.challenge4.feature.list.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challenge4.R
import com.example.challenge4.common.MovieAdapter
import com.example.challenge4.common.MovieClickListener
import com.example.challenge4.databinding.FragmentSearchBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.feature.MainActivity
import com.example.challenge4.model.Movie
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), MovieClickListener {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    lateinit var mainActivity: MainActivity
    lateinit var query: String
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        val navArgs by navArgs<SearchFragmentArgs>()
        query = navArgs.query

        setToolBar()
        setRV()
        setButtonClicks()
        observeInternetConnection()

        if (viewModel.page == 1) {
            viewModel.getTopRatedMovies(query)
        }

        return binding.root
    }

    private fun setToolBar() {
        // Toolbar has been made visible and its title has been changed.
        mainActivity.toolBar.run {
            show()
            mainActivity.showLayoutMenu(true)
            title = query
        }
    }

    private fun setRV() {
        binding.movieRV.run {
            // Adapter set for movies RV.
            movieAdapter = MovieAdapter(listener = this@SearchFragment, showNumber = false, isLarge = true)
            binding.movieRV.adapter = movieAdapter
            binding.movieRV.layoutManager = mainActivity.getLayout()

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // The scroll-up button appears when scrolling down.
                    if (dy > 0) {
                        binding.goToTop.show()
                    } else if (dy < 0) {
                        binding.goToTop.hide()
                    }

                    // When the last row of RecyclerView is reached, the next page is loaded.
                    if (!canScrollVertically(1)) {
                        if (viewModel.page <= viewModel.totalPage) {
                            viewModel.getTopRatedMovies(query)
                        }
                    }
                }
            })
        }

        // LiveData observed to update movies RV.
        viewModel.searchMoviesLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                movieAdapter.setMovieList(it)

                if (it.isEmpty()) {
                    binding.shimmerLayout.hide()
                    binding.movieRV.hide()
                    binding.textView.show()
                }
            }

            binding.shimmerLayout.hide()
            binding.movieRV.show()
        }

        // If the preferred Layout Manager has changed, it adjusts the RV's layout manager accordingly.
        mainActivity.viewModel.layoutManager.observe(viewLifecycleOwner) {
            val layout = mainActivity.getLayout()
            movieAdapter.setType(layout !is GridLayoutManager)
            binding.movieRV.layoutManager = layout
        }

        viewModel.checkFavorite()
    }

    private fun setButtonClicks() {
        // Clicking the scroll-up button made the RV return to the beginning.
        binding.goToTop.setOnClickListener {
            binding.movieRV.smoothScrollToPosition(0)
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
                viewModel.getTopRatedMovies(query)
            }
            .show()
    }

    override fun movieOnClick(movie: Movie) {
        // If any movie is clicked, the detail page of that movie is opened.
        val destination = SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(movie)
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
