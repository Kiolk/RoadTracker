package com.github.kiolk.roadtracker.data.repository.location.google

import android.location.Location
import android.os.Looper
import com.github.kiolk.roadtracker.data.repository.location.LocationDataSource
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

@ExperimentalCoroutinesApi
class GoogleLocationDataSource(
    private val locationClient: SettingsClient,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationDataSource {

    private var subscribers: MutableList<SendChannel<Location>> = mutableListOf()
    private var subscribersFlow: MutableList<SendChannel<Location>> = mutableListOf()
    private var scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var locationCallback: LocationCallback

    @ExperimentalCoroutinesApi
    override suspend fun getCurrentPosition(): Flow<Location> {
        return channelFlow {
            subscribers.add(this)
            getPosition()
            awaitClose()
        }
    }

    private fun getPosition() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            for (channel in subscribers) {
                scope.launch {
                    if(it != null) {
                        channel.send(it)
                    }
                }
            }
        }

        fusedLocationClient.lastLocation.addOnFailureListener {
            for (channel in subscribers) {
                scope.launch {
                    channel.close(it)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun trackPosition(): Flow<Location> {
        return channelFlow {
            subscribersFlow.add(this)
            subscribeOnUpdate()
            awaitClose()
        }
    }

    private fun subscribeOnUpdate() {
//        if (subscribersFlow.size > 1) {
//            return
//        }

        val locationRequest = LocationRequest.create().apply {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val task: Task<LocationSettingsResponse> =
            locationClient.checkLocationSettings(builder.build())

        setupCallback()

        task.addOnSuccessListener {
            Timber.e("Location is on")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener {
            Timber.e("Location is off")
            scope.launch {
                for (channel in subscribersFlow) {
                    channel.close(it)
                }
            }
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for (location in locationResult.locations) {
                    Timber.e("Coordinates is ${location.latitude} and ${location.longitude}")
                    scope.launch {
                        var isActive = false

                        subscribersFlow.forEach { channel ->
                            if (!channel.isClosedForSend) {
                                channel.send(location)
                                isActive = true
                            }
                        }

                        if (!isActive) {
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                            subscribersFlow = mutableListOf()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val INTERVAL = 4000L
        private const val FASTEST_INTERVAL = 4000L
    }
}
