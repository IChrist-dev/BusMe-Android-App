package com.example.transitapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.transitapp.databinding.ActivityMainBinding
import com.example.transitapp.ui.alerts.AlertsFragment
import com.example.transitapp.ui.map.MapFragment
import com.example.transitapp.ui.routes.RoutesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mapFragment : MapFragment? = null
    private var alertsFragment : AlertsFragment? = null
    private var routesFragment : RoutesFragment? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get location from Start Activity Intent
        // Commented out for now. Undecided about method for sharing device info
        val intent = intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        Log.i("TESTING", "---Device Location---\nLatitude: $latitude\nLongitude: $longitude")

        // Bottom Navigation View setup
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_map, R.id.navigation_routes, R.id.navigation_alerts))

        val bundle = Bundle().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }

        mapFragment = MapFragment().apply {
            arguments = bundle
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}