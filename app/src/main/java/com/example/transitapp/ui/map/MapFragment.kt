package com.example.transitapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.transitapp.databinding.FragmentMapBinding
import com.google.transit.realtime.GtfsRealtime
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var mapView : MapView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set Mapbox object
        mapView = binding.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        getBusData()

        return root
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getBusData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Get GTFS data
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
                val feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                for (entity : GtfsRealtime.FeedEntity in feed.entityList) {
                    Log.i("TESTING", "Route Number: " + entity.vehicle.trip.routeId.toString() + "\n" +
                            "Latitude: " + entity.vehicle.position.latitude.toString() + "\n" +
                            "Longitude: " + entity.vehicle.position.longitude.toString())
                }
            } catch (e: Exception) {
                Log.i("TESTING", e.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}