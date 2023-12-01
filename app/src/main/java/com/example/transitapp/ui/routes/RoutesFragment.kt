package com.example.transitapp.ui.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.transitapp.R
import com.example.transitapp.databinding.FragmentRoutesBinding
import java.io.File
import java.io.IOException

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val routesFileName = "saved_routes"
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
        val internalStorageDir = requireContext().filesDir
        val routesFile = File(internalStorageDir, routesFileName)

        // Check if file exists
        if (!routesFile.exists()) {
            try {
                routesFile.createNewFile()
                routesFile.appendText(",")
                Log.i("TESTING", "Saved-Routes file created")
            } catch (e: IOException) {
                Log.i("TESTING", "Error when creating file: ${e.message}")
            }
        }

        // Setup scroll view for saved routes
        val savedRoutesLL = binding.savedRoutesLinearLayout

        // Get contents
        var readFromFile = routesFile.readText()
        // Transfer contents of file to separate text views
        var fileSplit = readFromFile.split(",").toMutableList()
        for (route in fileSplit) {
            val routeTextView = TextView(requireContext())
            routeTextView.text = route
            routeTextView.textSize = 16F
            savedRoutesLL.addView(routeTextView)
        }

        // Logic to add routes to file
        val addRouteButton = binding.addRouteButton
        // Define button behaviour
        addRouteButton.setOnClickListener {
            val routeToSave = autoTextView.text.toString()

            if (routeToSave.isNotEmpty()) {
                if (!readFromFile.contains(",$routeToSave,")) {
                    // Add to file
                    routesFile.appendText("$routeToSave,")

                    // Add to GUI (temporary until page is reloaded)
                    val tempRouteTextView = TextView(requireContext())
                    tempRouteTextView.text = routeToSave.trim()
                    tempRouteTextView.textSize = 16F
                    savedRoutesLL.addView(tempRouteTextView)
                    // Clear the search bar
                    autoTextView.text.clear()
                }

                // Get updated contents of routes file
                readFromFile = routesFile.readText()
            }
        }

        // Logic to delete routes from file
        val deleteRouteButton = binding.deleteRouteButton
        deleteRouteButton.setOnClickListener {
            // Define button behaviour
            val routeToDelete = autoTextView.text.toString()

            // Update array
            fileSplit = readFromFile.split(",").toMutableList()

            if (routeToDelete.isNotEmpty()) {
                if (fileSplit.contains(routeToDelete)) {
                    // Delete one route from file and reset contents
                    fileSplit.remove(routeToDelete)
                    readFromFile = fileSplit.joinToString(",")

                    try {
                        routesFile.writeText(readFromFile)

                        // Get updated contents of routes file
                        readFromFile = routesFile.readText()
                        Log.i("TESTING", "Content in file:$readFromFile")
                    } catch (e: IOException) {
                        Log.i("TESTING", "IO Exception on deleting route from file: ${e.message}")
                    }

                    // Remove deleted route from GUI (temporary until page reload)
                    val savedRoutesCount = savedRoutesLL.childCount
                    var viewToDelete: View? = null

                    // Find the text view to remove
                    for (i in 0 until savedRoutesCount) {
                        val tempView = savedRoutesLL.getChildAt(i) as? TextView
                        if (tempView?.text.toString() == routeToDelete) {
                            viewToDelete = tempView
                            break
                        }
                    }

                    if (viewToDelete != null) {
                        savedRoutesLL.removeView(viewToDelete)
                    }

                    // Clear the search bar
                    autoTextView.text.clear()
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}