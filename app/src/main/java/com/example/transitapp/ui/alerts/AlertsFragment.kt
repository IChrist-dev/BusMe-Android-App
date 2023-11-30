package com.example.transitapp.ui.alerts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.transitapp.databinding.FragmentAlertsBinding
import com.google.transit.realtime.GtfsRealtime
import com.mapbox.geojson.Point
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var feed : GtfsRealtime.FeedMessage? = null
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup LinearLayout for use in network call
        val alertsLinearLayout = binding.alertsLinearLayout

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Get GTFS data
                val url = URL("https://gtfs.halifax.ca/realtime/Alert/Alerts.pb")
                feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                if (feed != null) {
                    for (entity: GtfsRealtime.FeedEntity in feed!!.entityList) {
                        val alert = entity.alert.descriptionText.translationList[0].text.toString()

                        // Jump out of co-routine to access mapView and add this entity's annotation
                        launch(Dispatchers.Main) {
                            // Load alert report into GUI TextView
                            val alertHeader = TextView(requireContext())
                            alertHeader.text = "Alert"
                            alertHeader.textSize = 18F
                            alertHeader.setTextColor(Color.RED)
                            val alertTextView = TextView(requireContext())
                            alertTextView.text = alert
                            alertTextView.textSize = 14F
                            alertTextView.setTextColor(Color.BLACK)
                            alertsLinearLayout.addView(alertHeader)
                            alertsLinearLayout.addView(alertTextView)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("TESTING", e.message.toString())
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}