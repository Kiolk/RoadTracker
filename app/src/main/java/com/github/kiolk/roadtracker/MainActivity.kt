package com.github.kiolk.roadtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBar = findViewById(R.id.bottom_navigation)
        setupNavigation()
    }

    private fun setupNavigation() {
        val controller = Navigation.findNavController(this, R.id.navigation_fragment)
        bottomBar.setupWithNavController(controller)
    }
}
