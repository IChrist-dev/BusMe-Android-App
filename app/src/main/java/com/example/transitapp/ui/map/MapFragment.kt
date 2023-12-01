package com.example.transitapp.ui.map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.transitapp.R
import com.example.transitapp.databinding.FragmentMapBinding
import com.google.transit.realtime.GtfsRealtime
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URL

class MapFragment: Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var mapView: MapView? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val routesFileName = "saved_routes"
    private var routesFile: File? = null

    private var feed: GtfsRealtime.FeedMessage? = null

    private lateinit var viewAnnotationManager : ViewAnnotationManager

    private val handler = Handler(Looper.getMainLooper())
    private val updateBusLocationsRunnable = object : Runnable {
        override fun run() {
            val internalStorageDir = requireContext().filesDir
            routesFile = File(internalStorageDir, routesFileName)
            val readFromFile = routesFile!!.readText()
            fetchBusPositions(readFromFile)
            handler.postDelayed(this, 20000)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Get phone location from bundle and set camera position
        arguments?.let {
            latitude = it.getDouble("latitude")
            longitude = it.getDouble("longitude")
        }

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set Mapbox object
        mapView = binding.mapView
        // Set camera position programmatically
        if (latitude != null && longitude != null) {
            val cameraPosition = CameraOptions.Builder()
                .zoom(13.0)
                .center(Point.fromLngLat(longitude!!, latitude!!))
                .build()
            Log.i("TESTING", "---Device Location (from Map Fragment)---" +
                    "\nLatitude: $latitude Longitude: $longitude")
            mapView!!.getMapboxMap().setCamera(cameraPosition)
        }

        viewAnnotationManager = mapView!!.viewAnnotationManager

        // Access Saved Routes file
        val internalStorageDir = requireContext().filesDir
        routesFile = File(internalStorageDir, routesFileName)
        // Check if file exists
        if (!routesFile!!.exists()) {
            try {
                routesFile!!.createNewFile()
                routesFile!!.appendText(",")
                Log.i("TESTING", "Saved-Routes file created")
            } catch (e: IOException) {
                Log.i("TESTING", "Error when creating file: ${e.message}")
            }
        }

        val readFromFile = routesFile!!.readText()

        // Populate bus position feed
        mapView?.getMapboxMap()?.apply {
            loadStyleUri(Style.MAPBOX_STREETS) {
                fetchBusPositions(readFromFile)
                handler.postDelayed(updateBusLocationsRunnable, 20000)
            }
        }
        return root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchBusPositions(readFromFile: String) {
        viewAnnotationManager.removeAllViewAnnotations()
        // Populate bus position feed after the map style is loaded
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Get GTFS data
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
                feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                if (feed != null) {
                    for (entity: GtfsRealtime.FeedEntity in feed!!.entityList) {
                        // Convert lat-long to Point for use in annotation
                        val point = Point.fromLngLat(
                            entity.vehicle.position.longitude.toDouble(),
                            entity.vehicle.position.latitude.toDouble()
                        )
                        val routeId = entity.vehicle.trip.routeId.toString()

                        // Jump out of co-routine to access mapView and add this entity's annotation
                        launch(Dispatchers.Main) {
                            val formattedRoute = ",$routeId,"
                            if (readFromFile.contains(formattedRoute)) {
                                addViewAnnotation(point, routeId, true)
                            } else {
                                addViewAnnotation(point, routeId, false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("TESTING", e.message.toString())
            }
        }
    }

    private fun addViewAnnotation(point: Point, routeId: String, isSaved: Boolean) {
        // Define view annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify layout resource id
            resId = R.layout.map_annotation,
            // Set view options
            options = viewAnnotationOptions {
                geometry(point)
            }
        )

        // Annotation default is set in xml
        if (isSaved) {
            viewAnnotation.setBackgroundResource(R.drawable.map_route_widget_saved)
        }

        // Get textView from annotation to update it with bus ID
        val annotationTextView: TextView = viewAnnotation.findViewById(R.id.annotation)
        annotationTextView.text = routeId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}