package com.example.myreturner


import android.Manifest
import android.annotation.SuppressLint

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myreturner.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


import kotlin.math.abs


private const val SETTINGS = "SETTINGS"
private const val LATITUDE = "LATITUDE"
private const val LONGITUDE = "LONGITUDE"
private const val IS_FIRST_START = "IS_FIRST_START"
private const val VERSION = "VERSION"
private const val VERS = "1.2"

class MainActivity : AppCompatActivity(), SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private var magneticFieldSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    private lateinit var magneticFieldValues: FloatArray
    private lateinit var accelerometerValues: FloatArray
    private lateinit var orientationMatrix: FloatArray

    private lateinit var tvDegree: TextView
    private lateinit var locDegree: TextView
    private lateinit var imDinamic: ImageView
    private lateinit var imStr: ImageView

    private var azimRotate: Int = 0

    private var strRotate: Int = 0


    private var currentDegree: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var targetLatitude: Double = 0.0// 59.833419371085725,30.165343816051177
    private var targetLongitude: Double = 0.0

    private lateinit var targetLatitudeStr: String
    private lateinit var targetLongitudeStr: String

    private var startTimer: Long = 0
    private var finishTimer: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getPreferences(MODE_PRIVATE).edit().putString(VERSION, VERS).apply()

        var counter = 0
        var counterStart: Long = 0
        var counterStop: Long
        tvDegree = binding.tvDegree
        locDegree = binding.locDegree
        imDinamic = binding.imDinamic
        imStr = binding.imStr

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        magneticFieldValues = FloatArray(3)
        accelerometerValues = FloatArray(3)
        orientationMatrix = FloatArray(9)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        imDinamic.setOnClickListener {
            counter++
            if (counter == 1) {
                counterStart = System.currentTimeMillis()
            }

            if (counter >= 10) {
                counterStop = System.currentTimeMillis()
                if ((counterStop - counterStart) < 3000) {
                    counterStop = 0
                    counterStart = 0
                    counter = 0
                    openAdminActivity()

                } else {
                    counterStop = 0
                    counterStart = 0
                    counter = 0
                }

            }
        }



        goToPoint().apply {
            if (targetLatitude == 0.0 || targetLongitude == 0.0) {
                notFinishPoint()

            }
        }


    }

    private fun goToPoint() {
        targetLatitudeStr =
            getSharedPreferences(SETTINGS, MODE_PRIVATE).getString(LATITUDE, "0.0")
                .toString()
        targetLongitudeStr =
            getSharedPreferences(SETTINGS, MODE_PRIVATE).getString(LONGITUDE, "0.0")
                .toString()

        if ((targetLatitudeStr != "0.0") && (targetLongitudeStr != "0.0")) {
            try {
                targetLatitude = targetLatitudeStr.toDouble()
                targetLongitude = targetLongitudeStr.toDouble()
            } catch (e: Exception) {
                targetLatitude = 0.0
                targetLongitude = 0.0
            }
        } else {
            targetLatitude = 0.0
            targetLongitude = 0.0
        }


    }

    override fun onStart() {
        super.onStart()


        val toast = getSharedPreferences(SETTINGS, MODE_PRIVATE).getBoolean(IS_FIRST_START, false)

        if (!toast) {
            openPasswordActivity()
        }


    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        goToPoint()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerValues = event.values
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                magneticFieldValues = event.values
            }
        }

        if (accelerometerValues.isNotEmpty() && magneticFieldValues.isNotEmpty()) {
            val success = SensorManager.getRotationMatrix(
                orientationMatrix,
                null,
                accelerometerValues,
                magneticFieldValues
            )
            if (success) {
                finishTimer = System.currentTimeMillis()
                if ((finishTimer - startTimer) > 500) {
                    process()
                    startTimer = System.currentTimeMillis()
                }
            }
        }

    }

    private fun process() {
        val orientation = FloatArray(3)
        SensorManager.getOrientation(orientationMatrix, orientation)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            getCurrentLocation()

        }
        var azimuth = Math.toDegrees(orientation[0].toDouble()).toInt()
        if (azimuth < 0) azimuth += 360
        if (abs(azimuth - azimRotate) > 3) {
            azimRotate = azimuth


            val rotationAnim = RotateAnimation(
                currentDegree.toFloat(), (-azimRotate.toFloat()),
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotationAnim.duration =
                abs((currentDegree).toFloat() - (-azimRotate).toFloat()).toLong() * 10
            rotationAnim.fillAfter = true
            rotationAnim.interpolator = LinearInterpolator()


            val rotationStr = RotateAnimation(
                (currentDegree + strRotate).toFloat(),
                ((-azimRotate + strRotate).toFloat()),
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotationStr.duration =
                abs((currentDegree + strRotate).toFloat() - (-azimRotate + strRotate).toFloat()).toLong() * 10
            rotationStr.fillAfter = true

            rotationStr.interpolator = LinearInterpolator()
            currentDegree = -azimRotate

            imDinamic.startAnimation(rotationAnim)


            imStr.startAnimation(rotationStr)


        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Здесь можно обработать изменения точности сенсоров, если необходимо
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        )
            .addOnSuccessListener { location: Location? ->

                if (targetLatitude == 0.0 || targetLongitude == 0.0) {
                    notFinishPoint()

                } else {

                    if (location != null) {

                        val currentLatitude = location.latitude
                        val currentLongitude = location.longitude

                        memoryOk(currentLatitude, currentLongitude)


                    } else {
                        notSignalGPS()

                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun memoryOk(currentLatitude: Double, currentLongitude: Double) {
        strRotate = calculateAzimuth(
            currentLatitude,
            currentLongitude,
            targetLatitude,
            targetLongitude
        ).toInt()

        //locDegree.text = "Азимут на точку: ${strRotate}°\nСтрелка указывает путь."
        locDegree.text =
            getResources().getString(R.string.azimuth_point) + strRotate + getResources().getString(
                R.string.stripe_to_way
            )

        tvDegree.text = calculateDistance(
            currentLatitude,
            currentLongitude,
            targetLatitude,
            targetLongitude
        ).toString() + getResources().getString(R.string.meter)
    }

    @SuppressLint("SetTextI18n")
    private fun notSignalGPS() {
        strRotate = 0
        tvDegree.text = getResources().getString(R.string.not_location)
        locDegree.text = getResources().getString(R.string.not_gps)
    }

    private fun notFinishPoint() {
        strRotate = 0
        tvDegree.text = ""
        locDegree.text = getResources().getString(R.string.not_end_point)

    }

    private fun openAdminActivity() {


        val runSettings = Intent(
            applicationContext,
            ControlLogActivity::class.java
        )

        startActivity(runSettings)

    }


    private fun openPasswordActivity() {


        val runSettings = Intent(
            applicationContext,
            LoginActivity::class.java
        )

        startActivity(runSettings)

    }

}




