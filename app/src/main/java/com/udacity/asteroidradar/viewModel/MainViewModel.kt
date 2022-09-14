package com.udacity.asteroidradar.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.util.Util
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.AsteroidFilter
import com.udacity.asteroidradar.network.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedAsteroid: MutableLiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()

    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    var selectedFilter: String = AsteroidFilter.SHOW_TODAY_ASTEROIDS.value

    init {
        // This is done if the workmanager fails to insert data
        viewModelScope.launch {
            if (Util.isNetworkAvailable(application.applicationContext)) {
                _status.value = AsteroidApiStatus.LOADING
                asteroidRepository.insertAsteroids()
                _status.value = AsteroidApiStatus.DONE
            }

        }
        viewModelScope.launch {
            try {
                _pictureOfTheDay.value = asteroidRepository.getPictureOfTheDay()
            } catch (e: Exception) {
                _pictureOfTheDay.value = PictureOfDay("", "", "")
                println("Error fetching data" + e.message)
            }
        }

    }

    val asteroids = asteroidRepository.asteroids

    val weeklyAsteroids = asteroidRepository.weeklyAsteroids

    val savedAsteroids = asteroidRepository.savedAsteroids


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }


    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun updateFilter(filter: AsteroidFilter) {
        selectedFilter = filter.value
    }

    fun getAsteroidList(): LiveData<List<Asteroid>> {
        if (selectedFilter.equals("today")) {
            return asteroids
        } else if (selectedFilter.equals("week")) {
            return weeklyAsteroids
        } else {
            return savedAsteroids
        }
    }

}