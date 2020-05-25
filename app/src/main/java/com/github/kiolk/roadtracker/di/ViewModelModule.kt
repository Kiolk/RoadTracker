package com.github.kiolk.roadtracker.di

import com.github.kiolk.roadtracker.presentation.screens.map.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { MapViewModel(get()) }
}