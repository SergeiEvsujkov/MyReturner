package com.example.myreturner

import android.Manifest
import android.annotation.SuppressLint

import android.content.DialogInterface


import android.content.pm.PackageManager


import android.os.Bundle
import android.os.Looper


import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.myreturner.databinding.ActivityVkmapBinding

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.button.MaterialButton


import ru.mail.maps.data.ImageAlignment
import ru.mail.maps.data.ImageSrcValue

import ru.mail.maps.data.LatLon
import ru.mail.maps.data.LocationSource

import ru.mail.maps.data.MapLocation
import ru.mail.maps.data.MapStyle
import ru.mail.maps.data.MarkerEntity

import ru.mail.maps.data.ResourceSrc
import ru.mail.maps.sdk.LogoConfig
import ru.mail.maps.sdk.MapGlobalConfig
import ru.mail.maps.sdk.MapStartOptions
import ru.mail.maps.sdk.models.MapViewConfig


import ru.mail.maps.sdk.views.MapView

private const val SETTINGS = "SETTINGS"
private const val LATITUDE = "LATITUDE"
private const val LONGITUDE = "LONGITUDE"
private const val LATITUDE_LAST = "LATITUDE_LAST"
private const val LONGITUDE_LAST = "LONGITUDE_LAST"


class VKMapActivity : AppCompatActivity() {


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var latitudeMrk: Double = 0.0
    private var longitudeMrk: Double = 0.0
    private var alertDialog: AlertDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapView: MapView
    private lateinit var locationSource: LocationSource
    private lateinit var imageSrcValue: ImageSrcValue
    private lateinit var setCoordBtn: MaterialButton

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        latitude = getSharedPreferences(SETTINGS, MODE_PRIVATE).getString(
            LATITUDE_LAST,
            "59.93899398130297"
        )
            ?.toDouble() ?: 0.0

        longitude = getSharedPreferences(SETTINGS, MODE_PRIVATE)
            .getString(LONGITUDE_LAST, "30.315812628406913")?.toDouble() ?: 0.0

        MapGlobalConfig.setMapGlobalConfig(
            MapViewConfig(
                apiKey = resources.getString(R.string.vk_maps_key)

            )
        )


        val binding = ActivityVkmapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MapGlobalConfig.setMapGlobalConfig(
            MapViewConfig(
                apiKey = resources.getString(R.string.vk_maps_key)

            )
        )
        MapGlobalConfig.setMapStartOptions(
            MapStartOptions(
                center = LatLon(latitude, longitude),
                style = MapStyle.Dark,
                logoConfig = LogoConfig(
                    LogoConfig.Alignment.BottomLeft,
                    LogoConfig.AdditionalPaddings()

                )

            )
        )

        var isMarker = false
        mapView = binding.mapView
        setCoordBtn = binding.saveBtn


        mapView.getMapAsync { map ->
            map.setLocationSource(locationSource)
        }




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {

                    latitude = location.latitude
                    longitude = location.longitude


                }

            }


        }


        // Запускаем обновление местоположения
        startLocationUpdates()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            latitude = fusedLocationClient.lastLocation.result.latitude
            longitude = fusedLocationClient.lastLocation.result.longitude
        } else {

        }.apply {
            mapView.getMapAsync { map ->
                map.flyTo(LatLon(latitude, longitude), true)
                map.setOnMapLongClickListener() { location, _ ->

                    imageSrcValue =
                        ResourceSrc(ru.mail.maps.sdk.R.drawable.ic_mail_electric_target)
                    val markerEntity =
                        MarkerEntity(
                            id = "newPoint",
                            coordinates = location,
                            alignment = ImageAlignment.Center(0, 0),
                            imageSrc = imageSrcValue
                        )
                    map.addMarker(markerEntity)
                    latitudeMrk = location.latitude!!
                    longitudeMrk = location.longitude!!

                    isMarker = true

                    map.setOnMarkerClickListener { id: String, loc: MapLocation ->
                        map.showPopUp(
                            id,
                            String.format("%.3f", loc.latitude) + " " + String.format(
                                "%.3f",
                                loc.longitude
                            )
                        )

                    }
                    //Toast.makeText(applicationContext, "Нажал", Toast.LENGTH_SHORT).show()
                }

            }

        }
        setCoordBtn.setOnClickListener {

            if (!isMarker) {
                createDialog(
                    resources.getString(R.string.not_point),
                    resources.getString(R.string.set_point_map)
                )
                alertDialog?.show()
            } else {
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LATITUDE, latitudeMrk.toString())
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LONGITUDE, longitudeMrk.toString())
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LATITUDE_LAST, latitudeMrk.toString())
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putString(LONGITUDE_LAST, longitudeMrk.toString())
                    .apply()

                createDialog(
                    getResources().getString(R.string.сongratulations),
                    getResources().getString(R.string.coordinates_recorded)
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



