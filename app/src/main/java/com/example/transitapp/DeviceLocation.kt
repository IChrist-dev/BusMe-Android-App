package com.example.transitapp

import android.app.Application

class DeviceLocation : Application() {
    companion object {
        var latitude: Double = 0.0
        var longitude: Double = 0.0
    }
}