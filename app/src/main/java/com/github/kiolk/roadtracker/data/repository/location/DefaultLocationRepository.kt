package com.github.kiolk.roadtracker.data.repository.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

class DefaultLocationRepository(private val googleLocation: LocationDataSource) :
        LocationRepository {

    override suspend fun getCurrentPosition(): Flow<Location> = googleLocation.getCurrentPosition()

    override suspend fun trackPosition(): Flow<Location> = googleLocation.trackPosition()
}