package com.example.challenge4.feature.detail.movie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.challenge4.R
import com.example.challenge4.common.ActorAdapter
import com.example.challenge4.common.ActorClickListener
import com.example.challenge4.common.Constant
import com.example.challenge4.common.MovieAdapter
import com.example.challenge4.common.MovieClickListener
import com.example.challenge4.databinding.FragmentMovieDetailBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.feature.MainActivity
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Movie
import com.example.challenge4.model.Video
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : Fragment(), ActorClickListener, MovieClickListener, VideoClickListener {

    private val viewModel: MovieDetailViewModel by viewModels()
    private lateinit var binding: FragmentMovieDetailBinding
    lateinit var mainActivity: MainActivity
    lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        val navArgs by navArgs<MovieDetailFragmentArgs>()
        movie = navArgs.movie

        setToolBar()
        observeInternetConnection()
        setRVs()
        setMovieDetails()
        observeFavoriteStatus()

        viewModel.initialize(movie)
        return binding.root
    }

    private fun setToolBar() {
        // Toolbar has been made invisible.
        mainActivity.toolBar.hide()

        // The appropriate icon is set according to whether the isFavorite field of the movie is true or false.
        val menuItem = binding.flexibleExampleToolbar.menu.findItem(R.id.add_favorite)
        menuItem.run {
            if (movie.isFavorite) {
                setIcon(R.drawable.ic_baseline_favorite_red_24)
            } else {
                setIcon(R.drawable.ic_baseline_favorite_24)
            }
        }

        // When back button in toolbar is pressed, fragment is closed.
        binding.flexibleExampleToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        binding.flexibleExampleToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                /* When the AddFavorite menu item is clicked,
                adding or removing the favorite is done. */
                R.id.add_favorite -> {
                    if (!movie.isFavorite) {
                        menuItem.setIcon(R.drawable.ic_baseline_favorite_red_24)
                    } else {
                        menuItem.setIcon(R.drawable.ic_baseline_favorite_24)
                    }
                    changeFavorite(movie)
                    true
                }
                // When the Share menu item is clicked, the sharing intent is open.
                R.id.share_button -> {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        movie.title.plus(" ") + resources.getString(R.string.share_button_content)
                    )
                    startActivity(Intent.createChooser(shareIntent, movie.title))

                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMovieDetails() {
        binding.collapsing.title = movie.title
        binding.releaseDate.text = if (movie.releaseDate.length >= Constant.YEAR_LENGTH) {
            movie.releaseDate.substring(0, Constant.YEAR_LENGTH)
        } else {
            resources.getString(R.string.empty_text)
        }
        binding.movieOverview.text = movie.overview
        binding.originalLanguage.text = movie.originalLanguage.uppercase()
        binding.voteAverage.text = "${String.format("%.1f", movie.voteAverage)} (${movie.voteCount})"

        binding.voteAverage.run {
            if (movie.voteAverage >= Constant.GOLD_RATE) {
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gold))
            } else if (movie.voteAverage >= Constant.SILVER_RATE) {
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.silver))
            } else if (movie.voteAverage > 0) {
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bronze))
            } else if (movie.voteAverage == 0.0) {
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.extra_grey))
            }
        }

        binding.noOverviewText.run {
            if (movie.overview.isEmpty()) {
                show()
            } else {
                hide()
            }
        }

        viewModel.movieDetailLiveData.observe(viewLifecycleOwner) {
            binding.runtime.text = if (it.runtime != null && it.runtime != 0) {
                it.runtime.toString().plus("m")
            } else {
                resources.getString(R.string.empty_text)
            }

            binding.detailShimmer.hide()
            binding.detailLayout.show()
        }

        viewModel.genreLiveData.observe(viewLifecycleOwner) {
            binding.movieGenres.text = it.toString().subSequence(1, it.toString().length - 1)
            binding.movieGenresShimmer.hide()

            if (it.isNotEmpty()) {
                binding.movieGenres.show()
                binding.noGenresText.hide()
            } else {
                binding.noGenresText.show()
            }
        }

        // Backdrop image of the movie is added to the toolbar background.
        Glide.with(requireContext())
            .load(Constant.IMAGE_PATH_PREFIX + movie.backdropPath)
            .placeholder(R.drawable.placeholder_movie)
            .into(binding.imageView)
    }

    @SuppressLint("SetTextI18n")
    private fun setRVs() {
        binding.videoRV.run {
            // Adapter set for video RV.
            val videoAdapter = VideoAdapter(this@MovieDetailFragment)
            adapter = videoAdapter

            // LiveData observed to update video RV.
            viewModel.videoLiveData.observe(viewLifecycleOwner) {
                videoAdapter.setVideoList(it)
                binding.movieVideosShimmer.hide()
                binding.videoCount.text = "(${it.size})"

                if (it.isNotEmpty()) {
                    show()
                    binding.noVideosText.hide()
                } else {
                    binding.noVideosText.show()
                }
            }
        }

        binding.actorRV.run {
            // Adapter set for actor RV.
            val actorAdapter = ActorAdapter(listener = this@MovieDetailFragment, isLarge = false)
            adapter = actorAdapter

            // LiveData observed to update actor RV.
            viewModel.actorLiveData.observe(viewLifecycleOwner) {
                actorAdapter.setActorList(it)
                binding.movieActorsShimmer.hide()
                binding.actorCount.text = "(${it.size})"

                if (it.isNotEmpty()) {
                    show()
                    binding.noActorsText.hide()
                } else {
                    binding.noActorsText.show()
                }
            }
        }

        binding.reviewRV.run {
            // Adapter set for review RV.
            val reviewAdapter = ReviewAdapter()
            adapter = reviewAdapter

            // LiveData observed to update review RV.
            viewModel.reviewLiveData.observe(viewLifecycleOwner) {
                reviewAdapter.setReviewList(it)

                if (it.isNotEmpty()) {
                    show()
                    binding.noReviewsText.hide()
                } else {
                    binding.noReviewsText.show()
                }
            }
        }

        binding.similarMovieRV.run {
            // Adapter set for similar movie RV.
            val similarMovieAdapter = MovieAdapter(
                listener = this@MovieDetailFragment,
                showNumber = false,
                isLarge = false
            )
            adapter = similarMovieAdapter

            // LiveData observed to update similar movie RV.
            viewModel.similarMovieLiveData.observe(viewLifecycleOwner) {
                similarMovieAdapter.setMovieList(it)
                binding.movieSimilarMoviesShimmer.hide()

                if (it.isNotEmpty()) {
                    show()
                    binding.noSimilarMoviesText.hide()
                } else {
                    binding.noSimilarMoviesText.show()
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            // The appropriate icon is set according to whether the isFavorite field of the movie is true or false.
            val menuItem = binding.flexibleExampleToolbar.menu.findItem(R.id.add_favorite)
            menuItem.run {
                if (it) {
                    setIcon(R.drawable.ic_baseline_favorite_red_24)
                } else {
                    setIcon(R.drawable.ic_baseline_favorite_24)
                }
            }
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
            .setMessage(resources.getString(R.string.offline) + resources.getString(R.string.offline_detail))
            .setNegativeButton(resources.getString(R.string.try_again)) { _, _ ->
                viewModel.initialize(movie)
            }
            .setPositiveButton(resources.getString(R.string.okey)) { _, _ ->
            }
            .show()
    }

    override fun movieOnClick(movie: Movie) {
        // If any movie is clicked, the detail page of that movie is opened.
        val destination = MovieDetailFragmentDirections.actionMovieDetailFragmentSelf(movie)
        Navigation.findNavController(requireView()).navigate(destination)
    }

    override fun actorOnClick(actor: Actor) {
        // If any actor is clicked, the detail page of that actor is opened.
        val destination = MovieDetailFragmentDirections.actionMovieDetailFragmentToActorDetailFragment(actor)
        Navigation.findNavController(requireView()).navigate(destination)
    }

    override fun videoOnClick(video: Video) {
        // If any video is clicked, YouTube player is opened.
        mainActivity.openYoutubeVideo(video.key)
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

        // The ViewModel function is called, this function processes whether the actor is a favorite or not.
        viewModel.changeFavorite(actor)
    }
}
