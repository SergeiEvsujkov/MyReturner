package com.example.myreturner

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager

import android.os.Bundle
import android.os.Looper



import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.myreturner.databinding.ActivitySetPointBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest


import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.button.MaterialButton


private const val SETTINGS = "SETTINGS"
private const val LATITUDE = "LATITUDE"
private const val LONGITUDE = "LONGITUDE"
private const val LATITUDE_LAST = "LATITUDE_LAST"
private const val LONGITUDE_LAST = "LONGITUDE_LAST"

class SetPointActivity : AppCompatActivity() {

    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var deviation: TextView
    private lateinit var enterPointBtn: MaterialButton
    private lateinit var begin: TextView
    private lateinit var deviationText: TextView
    private var alertDialog: AlertDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySetPointBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        latitude = binding.latitude
        longitude = binding.longitude
        deviation = binding.deviation
        enterPointBtn = binding.enterPointBtn
        begin = binding.begin
        deviationText = binding.deviationText

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {
                begin.text = resources.getString(R.string.your_coordinates)
                deviationText.isVisible = true
                deviation.isVisible = true
                for (location in locationResult.locations) {

                    latitude.text = location.latitude.toString() + "°"
                    longitude.text = location.longitude.toString() + "°"
                    deviation.text = "± " + location.accuracy.toInt().toString() + " м"

                }
            }

            override fun onLocationAvailability(locationResult: LocationAvailability) {
                if (!locationResult.isLocationAvailable) {
                    begin.text = resources.getString(R.string.not_gps_set_point)
                    latitude.text = resources.getString(R.string.not_defined)
                    longitude.text = resources.getString(R.string.not_defined)
                    deviation.isVisible = false
                    binding.deviationText.isVisible = false

                }
            }

        }



        // Запускаем обновление местоположения
        startLocationUpdates()
        enterPointBtn.setOnClickListener {

            if (latitude.text != resources.getString(R.string.not_defined)) {
                val latitude = latitude.text.toString().substringBefore("°")
                val longitude = longitude.text.toString().substringBefore("°")
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit().putString(LATITUDE, latitude)
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit().putString(LONGITUDE, longitude)
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LATITUDE_LAST, latitude)
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LONGITUDE_LAST, longitude)
                    .apply()
                createDialog(
                    getResources().getString(R.string.сongratulations),
                    getResources().getString(R.string.coordinates_recorded)
                )
                alertDialog?.show()

            } else {
                createDialog(
                    getResources().getString(R.string.attention),
                    getResources().getString(R.string.coordinates_not_determined)
                )
                alertDialog?.show()
            }

        }


    }


    private fun createDialog(messageOk: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(messageOk)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setNegativeButton(resources.getString(R.string.closed)) { _: DialogInterface, _: Int ->
        }
        alertDialog = alertDialogBuilder.create()
    }

    private fun startLocationUpdates() {

        val interval: Long = 5000 // Интервал обновлений в миллисекундах
        val fastestInterval: Long = 2000 // Быстрый интервал
        val priority = Priority.PRIORITY_HIGH_ACCURACY

        val locationRequest = LocationRequest.Builder(priority, interval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(fastestInterval)
            .setMaxUpdateDelayMillis(6000)
            .build()



        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )


        }
    }



    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}