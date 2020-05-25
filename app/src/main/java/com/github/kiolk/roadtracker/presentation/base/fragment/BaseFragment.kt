package com.github.kiolk.roadtracker.presentation.base.fragment

import androidx.navigation.fragment.NavHostFragment
import com.github.kiolk.roadtracker.presentation.base.viewmodel.BaseViewModel

abstract class BaseFragment<T: BaseViewModel>: NavHostFragment() {

    abstract val viewModel: T
}