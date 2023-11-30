package com.example.transitapp.ui.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.transitapp.R
import com.example.transitapp.databinding.FragmentRoutesBinding
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val routesFileName = "Saved_Routes_File.txt"
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Setup AutoComplete Text View
        val autoTextView = root.findViewById<AutoCompleteTextView>(R.id.route_Autocomplete_TextView)
        val routesList = resources.getStringArray(R.array.bus_routes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, routesList)
        autoTextView.setAdapter(adapter)

        // Access routes-file to present and add buses
        val interalStorageDir = requireContext().filesDir
        val routesFile = File(interalStorageDir, routesFileName)

        routesFile.delete()
        // Check if file exists
        if (!routesFile.exists()) {
            try {
                routesFile.createNewFile()
                Log.i("TESTING", "Saved-Routes file created")
            } catch (e: IOException) {
                Log.i("TESTING", "Error when creating file: ${e.message}")
            }
        }

        // Setup scroll view for saved routes
        val savedRoutesLL = binding.savedRoutesLinearLayout

        // Get contents
        var readFromFile = routesFile.readText()
        val fileSplit = readFromFile.split(",")

        for (route in fileSplit) {
            val routeTextView = TextView(requireContext());
            val formattedRoute = route + "\n"
            routeTextView.text = formattedRoute
            savedRoutesLL.addView(routeTextView)
        }

        val addRouteButton = binding.addRouteButton
        // Define button behaviour
        addRouteButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val routeToSave = autoTextView.text

                if (routeToSave.isNotEmpty()) {
                    if (!readFromFile.contains(routeToSave)) {
                        routesFile.appendText("$routeToSave,")
                    }

                    // Get updated contents of routes file
                    readFromFile = routesFile.readText()
                    Log.i("TESTING", "Content in file: $readFromFile")
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}