package com.github.kiolk.roadtracker.presentation.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.kiolk.roadtracker.R
import com.github.kiolk.roadtracker.presentation.base.getLatLng
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class Map : BaseMapFragment<MapViewModel>() {

    override val viewModel: MapViewModel by viewModel()
    private var previewLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(checkPermissions()) {
            mapView = view.findViewById(R.id.map_view)
            mapView?.onCreate(savedInstanceState)
            mapView?.getMapAsync(this)
        }
    }

    private fun initMapView() {
        mapView = view?.findViewById(R.id.map_view) ?: return
        mapView?.onCreate(null)
        mapView?.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val result = grantResults.find { it == permissions.indexOf("android.permission.ACCESS_FINE_LOCATION") }
        if (requestCode == REQUEST_CODE_LOCATION && result != null) {
            Timber.e("Permissions granted")
            initMapView()
        } else {
            Timber.e("Permissions not granted")
        }
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
            false
        } else {
            true
        }
    }

    override fun setNextLocation(location: Location) {
        if (previewLocation == null) {
            previewLocation = location
            return
        }

        val markerView = MarkerView(previewLocation!!.getLatLng(), View(requireContext()).apply {
            background = requireContext().resources.getDrawable(R.drawable.bg_dot, null)
            layoutParams = ViewGroup.LayoutParams(10, 10)
        })
        markerViewManager.addMarker(markerView)

        previewLocation = location
    }

    companion object {
        private const val REQUEST_CODE_LOCATION = 501
        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1500L
    }
}