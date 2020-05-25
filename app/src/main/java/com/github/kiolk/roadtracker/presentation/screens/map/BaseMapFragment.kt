package com.github.kiolk.roadtracker.presentation.screens.map

import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import com.github.kiolk.roadtracker.R
import com.github.kiolk.roadtracker.presentation.base.fragment.BaseFragment
import com.github.kiolk.roadtracker.presentation.base.viewmodel.BaseMapViewModel
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager

abstract class BaseMapFragment<VM : BaseMapViewModel> : BaseFragment<VM>(), OnMapReadyCallback {

    abstract override val viewModel: VM

    protected var mapView: MapView? = null
    lateinit var mapBoxMap: MapboxMap
    protected var callback = LocationChangeCallback(this)
    protected lateinit var locationEngine: LocationEngine
    protected lateinit var markerViewManager: MarkerViewManager
    protected lateinit var locationComponent: LocationComponent

    abstract fun setNextLocation(location: Location)

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapBoxMap = mapboxMap
        val style = Style.Builder().fromUri(STYLE_URI)
        mapboxMap.setStyle(style)
        mapboxMap.setStyle(Style.TRAFFIC_DAY) { setStyle -> setLocation(setStyle) }
        markerViewManager = MarkerViewManager(mapView, mapboxMap)
        mapboxMap.setMinZoomPreference(DEFAULT_ZOOM)
    }

    private fun setLocation(style: Style) {
            mapBoxMap.getStyle {
                locationComponent = mapBoxMap.locationComponent;
                val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(), style)
                        .useDefaultLocationEngine(false)
                        .build();

                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                locationComponent.isLocationComponentEnabled = true;
                locationComponent.cameraMode = CameraMode.TRACKING;
                locationComponent.renderMode = RenderMode.COMPASS;

                locationComponent.onDestroy()
                initLocationEngine();
            }
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest.Builder(Map.DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine.getLastLocation(callback)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        viewModel.onMapReady()
    }

    override fun onPause() {
        super.onPause()
        mapView?.let{
            it.onPause()
            locationComponent.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.let {
            it.onDestroy()
            locationComponent.onDestroy()
            locationEngine.removeLocationUpdates(callback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    companion object {
        private const val DEFAULT_ZOOM = 15.0
        private const val STYLE_URI = "mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"
        private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1500L
        private const val DEFAULT_MAX_WAIT_TIME: Long = DEFAULT_INTERVAL_IN_MILLISECONDS

    }
}