package com.github.kiolk.roadtracker.presentation.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.kiolk.roadtracker.R
import com.github.kiolk.roadtracker.presentation.base.fragment.BaseFragment
import com.github.kiolk.roadtracker.presentation.base.getLatLng
import com.mapbox.android.core.location.*
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.ref.WeakReference


class Map : BaseFragment<MapViewModel>(), OnMapReadyCallback {

    override val viewModel: MapViewModel by viewModel()
    private lateinit var mapView: MapView
    lateinit var mapboxMap: MapboxMap
    private lateinit var locationEngine: LocationEngine
    private val callback: LocationChangeListeningActivityLocationCallback =
        LocationChangeListeningActivityLocationCallback(this)
    private lateinit var markerViewManager: MarkerViewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"))
        mapboxMap.setStyle(
            Style.TRAFFIC_DAY
        ) { style -> setLocation(style) }
        markerViewManager = MarkerViewManager(mapView, mapboxMap)
    }

    private fun setLocation(style: Style) {
        val locationComponent = mapboxMap.getLocationComponent();

        // Set the LocationComponent activation options
        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(requireContext(), style)
                .useDefaultLocationEngine(false)
                .build();

        // Activate with the LocationComponentActivationOptions object
        locationComponent.activateLocationComponent(locationComponentActivationOptions);

        // Enable to make component visible
        locationComponent.isLocationComponentEnabled = true;

        // Set the component's camera mode
        locationComponent.cameraMode = CameraMode.TRACKING;

        // Set the component's render mode
        locationComponent.renderMode = RenderMode.COMPASS;

        initLocationEngine();
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine.requestLocationUpdates(request, callback, getMainLooper())
        locationEngine.getLastLocation(callback)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        viewModel.onMapReady()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val result =
            grantResults.find { it == permissions.indexOf("android.permission.ACCESS_FINE_LOCATION") }

        if (requestCode == REQUEST_CODE_LOCATION && result != null) {
            Timber.e("Permissions granted")
            viewModel.onHandleCurrentPosition()
        } else {
            Timber.e("Permissions not granted")
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        } else {
            viewModel.onHandleCurrentPosition()
        }
    }

    fun setDot(lastLocation: Location) {
        val markerView = MarkerView(lastLocation.getLatLng(), View(requireContext()).apply {
            background = requireContext().resources.getDrawable(R.drawable.dot)
            layoutParams = ViewGroup.LayoutParams(10, 10)
        })
        markerViewManager.addMarker(markerView)
    }

    companion object {
        private const val REQUEST_CODE_LOCATION = 501
        val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        private val DEFAULT_MAX_WAIT_TIME: Long = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    }
}

private class LocationChangeListeningActivityLocationCallback internal constructor(activity: Map?) :
    LocationEngineCallback<LocationEngineResult?> {
    private val activityWeakReference: WeakReference<Map?>
    /**
     * The LocationEngineCallback interface's method which fires when the device's location has changed.
     *
     * @param result the LocationEngineResult object which has the last known location within it.
     */
    override fun onSuccess(result: LocationEngineResult?) {
        val activity: Map? = activityWeakReference.get()
        if (activity != null) {
            val location = result!!.lastLocation ?: return
            // Create a Toast which displays the new location's coordinates
            Toast.makeText(
                activity.requireContext(),
                java.lang.String.format(
                    "New location",
                    result.lastLocation!!.latitude.toString(),
                    result.lastLocation!!.longitude.toString()
                ),
                Toast.LENGTH_SHORT
            ).show()
            // Pass the new location to the Maps SDK's LocationComponent
            if (activity.mapboxMap != null && result.lastLocation != null) {
                activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.lastLocation)
                activity.setDot(result.lastLocation!!)
            }
        }
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can't be captured
     *
     * @param exception the exception message
     */
    override fun onFailure(exception: Exception) {
        val activity: Map? = activityWeakReference.get()
        if (activity != null) {
            Toast.makeText(
                activity.requireContext(), exception.localizedMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    init {
        activityWeakReference = WeakReference(activity)
    }
}