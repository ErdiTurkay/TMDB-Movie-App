package com.example.challenge4.feature.list.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.challenge4.R
import com.example.challenge4.common.ActorAdapter
import com.example.challenge4.common.ActorClickListener
import com.example.challenge4.common.MovieAdapter
import com.example.challenge4.common.MovieClickListener
import com.example.challenge4.databinding.FragmentFavoriteBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.feature.MainActivity
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Movie
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment(), MovieClickListener, ActorClickListener {

    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var binding: FragmentFavoriteBinding
    lateinit var mainActivity: MainActivity
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var actorAdapter: ActorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        setToolbar()
        selectTab()
        setRVs()
        setTabLayoutClicks()
        setSwipeRefreshLayout()

        viewModel.initialize()
        return binding.root
    }

    private fun selectTab() {
        // The corresponding tabLayout is selected according to the tabPosition variable in the ViewModel.
        binding.tabLayout.getTabAt(viewModel.tabPosition)?.select()
    }

    private fun setToolbar() {
        // Toolbar has been made visible and its title has been changed.
        mainActivity.toolBar.run {
            show()
            mainActivity.showLayoutMenu(true)
            title = resources.getString(R.string.favorites)
        }
    }

    private fun setSwipeRefreshLayout() {
        // Adjusted which RV will appear according to swipe refresh.
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.initialize()
            binding.tabLayout.getTabAt(viewModel.tabPosition)?.select()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setRVs() {
        binding.movieRV.run {
            // Adapter and LayoutManager are set for favorite movies RV.
            movieAdapter = MovieAdapter(listener = this@FavoriteFragment, showNumber = false, isLarge = true)
            adapter = movieAdapter
            layoutManager = mainActivity.getLayout()
        }

        // LiveData observed to update favorite movies RV.
        viewModel.favoriteMoviesLiveData.observe(viewLifecycleOwner) {
            movieAdapter.setMovieList(it)

            // If the selected tabPosition is 0, the favorite movie RV is displayed.
            if (binding.tabLayout.selectedTabPosition == 0) {
                if (it.isNotEmpty()) {
                    binding.noResultText.hide()
                    binding.movieRV.show()
                    binding.actorRV.hide()
                } else {
                    binding.noResultText.show()
                    binding.movieRV.hide()
                    binding.actorRV.hide()
                }
            }
        }

        binding.actorRV.run {
            // Adapter and LayoutManager are set for favorite actors RV.
            actorAdapter = ActorAdapter(listener = this@FavoriteFragment, isLarge = true)
            adapter = actorAdapter
            layoutManager = mainActivity.getLayout()
        }

        // LiveData observed to update favorite actors RV.
        viewModel.favoriteActorsLiveData.observe(viewLifecycleOwner) {
            actorAdapter.setActorList(it)

            // If the selected tabPosition is 1, the favorite actor RV is displayed.
            if (binding.tabLayout.selectedTabPosition == 1) {
                if (it.isNotEmpty()) {
                    binding.noResultText.hide()
                    binding.actorRV.show()
                    binding.movieRV.hide()
                } else {
                    binding.noResultText.show()
                    binding.actorRV.hide()
                    binding.movieRV.hide()
                }
            }
        }

        // If the preferred Layout Manager has changed, it adjusts the RV's layout manager accordingly.
        mainActivity.viewModel.layoutManager.observe(viewLifecycleOwner) {
            val movieLayout = mainActivity.getLayout()
            val actorLayout = mainActivity.getLayout()

            if (movieLayout is GridLayoutManager) {
                movieAdapter.setType(false)
                actorAdapter.setType(false)
            } else {
                movieAdapter.setType(true)
                actorAdapter.setType(true)
            }

            binding.movieRV.layoutManager = movieLayout
            binding.actorRV.layoutManager = actorLayout
        }
    }

    private fun setTabLayoutClicks() {
        // Adjusted which RV will appear according to tab change.
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.movieRV.show()
                    binding.actorRV.hide()
                    viewModel.getFavoriteMovies()
                    viewModel.tabPosition = 0
                } else {
                    binding.movieRV.hide()
                    binding.actorRV.show()
                    viewModel.getFavoriteActors()
                    viewModel.tabPosition = 1
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun movieOnClick(movie: Movie) {
        // If any movie is clicked, the detail page of that movie is opened.
        val destination = FavoriteFragmentDirections.actionFavoriteFragmentToMovieDetailFragment(movie)
        Navigation.findNavController(requireView()).navigate(destination)
    }

    override fun actorOnClick(actor: Actor) {
        // If any actor is clicked, the detail page of that actor is opened.
        val destination = FavoriteFragmentDirections.actionFavoriteFragmentToActorDetailFragment(actor)
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

    override fun changeFavorite(actor: Actor) {
        // The Snack text has been updated according to whether isFavorite field of the actor is true/false.
        val text = if (!actor.isFavorite) {
            actor.name.plus(" ") + resources.getString(R.string.added_favorites)
        } else {
            actor.name.plus(" ") + resources.getString(R.string.removed_favorites)
        }

        // An information message is displayed on the screen.
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_SHORT
        ).setAction(resources.getString(R.string.close)) {
        }.show()

        // The ViewModel function is called, this function processes whether the movie is a favorite or not.
        viewModel.changeFavorite(actor)
    }
}
