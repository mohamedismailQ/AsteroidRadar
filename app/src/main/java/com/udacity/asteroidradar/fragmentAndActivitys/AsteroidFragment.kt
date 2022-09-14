package com.udacity.asteroidradar.fragmentAndActivitys

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.AsteroidFilter
import com.udacity.asteroidradar.adapters.AsteroidAdapter
import com.udacity.asteroidradar.viewModel.MainViewModel

class AsteroidFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        // ViewModelProvider(this).get(MainViewModel::class.java)
        ViewModelProvider(this,
            MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)

    }

    val adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
        viewModel.displayAsteroidDetails(it)
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController()
                    .navigate(com.udacity.asteroidradar.fragmentAndActivitys.AsteroidFragmentDirections.actionShowDetail(
                        it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayAsteroidDetailsComplete()
            }
        })
        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.show_saved_menu -> {
                observeAsteroids(viewModel.savedAsteroids, AsteroidFilter.SHOW_SAVED_ASTEROIDS)
            }
            R.id.show_today_menu -> {
                observeAsteroids(viewModel.asteroids, AsteroidFilter.SHOW_TODAY_ASTEROIDS)
            }
            else -> observeAsteroids(viewModel.weeklyAsteroids, AsteroidFilter.SHOW_WEEK_ASTEROIDS)
        }

        return true
    }

    private fun observeAsteroids(
        asteroidListLiveData: LiveData<List<Asteroid>>, filter: AsteroidFilter,
    ) {
        viewModel.updateFilter(filter)
        asteroidListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}
