package com.github.kiolk.roadtracker.presentation.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kiolk.roadtracker.R
import com.github.kiolk.roadtracker.presentation.base.fragment.BaseFragment
import com.github.kiolk.roadtracker.presentation.screens.map.MapViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class Home : BaseFragment<MapViewModel>() {

    override val viewModel: MapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
