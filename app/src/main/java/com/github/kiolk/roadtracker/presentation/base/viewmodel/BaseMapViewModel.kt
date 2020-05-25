package com.github.kiolk.roadtracker.presentation.base.viewmodel

import com.github.kiolk.roadtracker.data.repository.location.LocationRepository

abstract class BaseMapViewModel(protected val locationRepository: LocationRepository): BaseViewModel() {

    abstract fun onMapReady()
}