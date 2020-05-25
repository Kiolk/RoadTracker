package com.github.kiolk.roadtracker.di

import com.github.kiolk.roadtracker.App
import com.github.kiolk.roadtracker.presentation.navigation.Navigation
import com.github.kiolk.roadtracker.presentation.navigation.NavigationImpl
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module {

    single<Navigation> { NavigationImpl(androidApplication() as App)}
    single {
        LocationServices.getSettingsClient(androidContext())
    }

    single {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }
}