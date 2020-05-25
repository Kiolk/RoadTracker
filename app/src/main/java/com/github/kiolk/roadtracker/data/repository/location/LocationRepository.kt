package com.github.kiolk.roadtracker.data.repository.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentPosition(): Flow<Location>

    suspend fun trackPosition(): Flow<Location>
}