package com.example.transitapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.transitapp.databinding.ActivityMainBinding
import java.net.URL
import com.google.transit.realtime.GtfsRealtime.FeedEntity
import com.google.transit.realtime.GtfsRealtime.FeedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get location from Start Activity Intent
        val intent = intent
        val latitude: Double = intent.getDoubleExtra("latitude", 0.0)
        val longitude: Double = intent.getDoubleExtra("longitude", 0.0)

        Log.i("TESTING", "Latitude: $latitude\nLongitude: $longitude")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Get GTFS data
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
                val feed = FeedMessage.parseFrom(url.openStream())
                for (entity : FeedEntity in feed.entityList) {
                    Log.i("TESTING", "Route Number: " + entity.vehicle.trip.routeId.toString() + "\n" +
                    "Latitude: " + entity.vehicle.position.latitude.toString() + "\n" +
                    "Longitude: " + entity.vehicle.position.longitude.toString())
                }
            } catch (e: Exception) {
                Log.i("TESTING", e.message.toString())
            }
        }

        // Bottom Navigation View setup
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_map, R.id.navigation_routes, R.id.navigation_alerts))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}