package com.github.kiolk.roadtracker.di

import com.github.kiolk.roadtracker.data.repository.location.DefaultLocationRepository
import com.github.kiolk.roadtracker.data.repository.location.LocationDataSource
import com.github.kiolk.roadtracker.data.repository.location.LocationRepository
import com.github.kiolk.roadtracker.data.repository.location.google.GoogleLocationDataSource
import org.koin.dsl.module

val dataSourceModule = module {

    //location
    single<LocationDataSource> { GoogleLocationDataSource(get(), get()) }
    single<LocationRepository> { DefaultLocationRepository(get()) }
}
