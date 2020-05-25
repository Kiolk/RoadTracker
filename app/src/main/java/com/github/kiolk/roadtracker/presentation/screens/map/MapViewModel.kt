package com.github.kiolk.roadtracker.presentation.screens.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.kiolk.roadtracker.data.repository.location.LocationRepository
import com.github.kiolk.roadtracker.presentation.base.viewmodel.BaseMapViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel(locationRepository: LocationRepository) : BaseMapViewModel(locationRepository) {

    private val _currentPosition: MutableLiveData<Location> = MutableLiveData()

    val currentPosition: LiveData<Location> = _currentPosition

    override fun onMapReady() {
//        viewModelScope.launch {
//            locationRepository.trackPosition().collect {
//                Timber.e("================== Coordinates ${it.longitude} - ${it.latitude}")
//                _currentPosition.value = it
//            }
//        }
    }
}