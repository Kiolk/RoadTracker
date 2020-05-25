package com.github.kiolk.roadtracker.presentation.base

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng

fun Location.getLatLng(): LatLng = LatLng(this.latitude, this.longitude)
