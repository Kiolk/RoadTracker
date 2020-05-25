package com.github.kiolk.roadtracker.data.repository.location.google

import android.content.Context
import android.location.Location
import com.github.kiolk.roadtracker.data.repository.location.LocationDataSource
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import kotlinx.coroutines.flow.Flow


class MapBoxLocationDataSource(private val context: Context): LocationDataSource {

    override suspend fun getCurrentPosition(): Flow<Location> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun trackPosition(): Flow<Location> {

    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    private fun enableLocationComponent(loadedMapStyle: Style) { // Check if permissions are enabled and if not request
            val locationComponent: LocationComponent = Mapbox.get.getLocationComponent()
            // Set the LocationComponent activation options
            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(context, loadedMapStyle)
                    .useDefaultLocationEngine(false)
                    .build()
            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions)
            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true
            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING
            // Set the component's render mode
            locationComponent.renderMode = RenderMode.COMPASS
            initLocationEngine()
    }
}