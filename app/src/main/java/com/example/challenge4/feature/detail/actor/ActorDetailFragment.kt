package com.example.challenge4.feature.detail.actor

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
import com.example.challenge4.common.Constant
import com.example.challenge4.databinding.FragmentActorDetailBinding
import com.example.challenge4.extension.hide
import com.example.challenge4.extension.show
import com.example.challenge4.feature.MainActivity
import com.example.challenge4.model.Actor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActorDetailFragment : Fragment() {

    private val viewModel: ActorDetailViewModel by viewModels()
    private lateinit var binding: FragmentActorDetailBinding
    lateinit var mainActivity: MainActivity
    lateinit var actor: Actor
    private lateinit var starringMovieAdapter: StarringMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActorDetailBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        val navArgs by navArgs<ActorDetailFragmentArgs>()
        actor = navArgs.actor

        setToolBar()
        observeInternetConnection()
        setRV()
        setActorDetails()
        observeFavoriteStatus()

        viewModel.initialize(actor)
        return binding.root
    }

    private fun setToolBar() {
        // Toolbar has been made invisible.
        mainActivity.toolBar.hide()

        // The appropriate icon is set according to whether the isFavorite field of the actor is true or false.
        val menuItem = binding.flexibleExampleToolbar.menu.findItem(R.id.add_favorite)
        menuItem.run {
            if (actor.isFavorite) {
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
                    if (!actor.isFavorite) {
                        menuItem.setIcon(R.drawable.ic_baseline_favorite_red_24)
                    } else {
                        menuItem.setIcon(R.drawable.ic_baseline_favorite_24)
                    }
                    changeFavorite(actor)
                    true
                }
                // When the Share menu item is clicked, the sharing intent is open.
                R.id.share_button -> {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        actor.name.plus(" ") + resources.getString(R.string.share_button_content)
                    )
                    startActivity(Intent.createChooser(shareIntent, actor.name))
                    true
                }
                else -> false
            }
        }
    }

    private fun setRV() {
        binding.movieRV.run {
            // Adapter is set for starring movies RV.
            starringMovieAdapter = StarringMovieAdapter()
            adapter = starringMovieAdapter

            // LiveData observed to update starring movies RV.
            viewModel.castsLiveData.observe(viewLifecycleOwner) {
                starringMovieAdapter.setMovieList(it)
                binding.movieSimilarMoviesShimmer.hide()
                show()
            }
        }
    }

    private fun setActorDetails() {
        binding.actorName.text = actor.name
        binding.collapsing.title = actor.name

        binding.department.text = actor.knownForDepartment
        binding.popularity.text = actor.popularity.toString()

        binding.gender.run {
            // The appropriate icon has been set according to the gender of the actor.
            when (actor.gender) {
                1 -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_female_24, 0, 0, 0)
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.female))
                }
                2 -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_male_24, 0, 0, 0)
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.male))
                }
                else -> {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_transgender_24, 0, 0, 0)
                }
            }
        }

        // LiveData observed to update actor details.
        viewModel.personLiveData.observe(viewLifecycleOwner) {
            if (it.biography.isNotEmpty()) {
                binding.actorBiography.text = it.biography.trim()
            }
            if (it.birthday != null) {
                binding.actorBirthday.text = it.birthday?.trim()
            }
            if (it.placeOfBirth != null) {
                binding.actorPlaceOfBirth.text = it.placeOfBirth?.trim()
            }

            binding.actorBirthdayShimmer.hide()
            binding.actorBiographyShimmer.hide()
            binding.actorPlaceOfBirthShimmer.hide()

            binding.actorBirthday.show()
            binding.actorBiography.show()
            binding.actorPlaceOfBirth.show()
        }

        // A photo of the actor is added to the toolbar background.
        Glide.with(requireContext())
            .load(Constant.IMAGE_PATH_PREFIX + actor.profilePath)
            .placeholder(R.drawable.placeholder_person)
            .into(binding.imageView)
    }

    private fun observeFavoriteStatus() {
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            // The appropriate icon is set according to whether the isFavorite field of the actor is true or false.
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
                viewModel.initialize(actor)
            }
            .setPositiveButton(resources.getString(R.string.okey)) { _, _ ->
            }
            .show()
    }

    fun changeFavorite(actor: Actor) {
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
