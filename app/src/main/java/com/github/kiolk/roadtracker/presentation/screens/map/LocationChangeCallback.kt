package com.github.kiolk.roadtracker.presentation.screens.map

import android.widget.Toast
import com.github.kiolk.roadtracker.presentation.base.viewmodel.BaseMapViewModel
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import timber.log.Timber
import java.lang.ref.WeakReference

class LocationChangeCallback<VM : BaseMapViewModel> internal constructor(targetView: BaseMapFragment<VM>) :
        LocationEngineCallback<LocationEngineResult?> {
    private val activityWeakReference = WeakReference(targetView)

    override fun onSuccess(result: LocationEngineResult?) {
        val targetView: BaseMapFragment<VM> = activityWeakReference.get() ?: return
        if (result?.lastLocation != null) {
            targetView.mapBoxMap.locationComponent.forceLocationUpdate(result.lastLocation)
            targetView.setNextLocation(result.lastLocation!!)
        }
    }

    override fun onFailure(exception: Exception) {
        val targetView: BaseMapFragment<VM> = activityWeakReference.get() ?: return
        Toast.makeText(targetView.requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        Timber.e(exception)
    }
}