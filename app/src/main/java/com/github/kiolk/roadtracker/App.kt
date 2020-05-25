package com.github.kiolk.roadtracker

import android.app.Application
import com.github.kiolk.roadtracker.di.applicationModule
import com.github.kiolk.roadtracker.di.dataSourceModule
import com.github.kiolk.roadtracker.di.viewModelModule
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(applicationModule, viewModelModule, dataSourceModule))
        }

        Mapbox.getInstance(this, getString(R.string.map_box_access_token))
        Timber.plant(Timber.DebugTree())
    }
}