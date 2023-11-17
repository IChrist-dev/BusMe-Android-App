package com.example.transitapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.transitapp.DeviceLocation
import com.example.transitapp.databinding.FragmentMapBinding
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

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

        Log.i("TESTING", "Device Location: ${DeviceLocation.latitude}, ${DeviceLocation.longitude}")

        // Set Mapbox object
        mapView = binding.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}