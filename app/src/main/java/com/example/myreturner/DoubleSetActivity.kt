package com.example.myreturner

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.myreturner.databinding.ActivityDoubleSetBinding

import com.google.android.material.button.MaterialButton

class DoubleSetActivity : AppCompatActivity() {

    private lateinit var enterLocation: MaterialButton
    private lateinit var enterOnMap: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDoubleSetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        enterLocation = binding.enterLocation
        enterOnMap = binding.enterOnMap

        enterLocation.setOnClickListener {
            openSetPointActivity()
            finish()
        }
        enterOnMap.setOnClickListener {
            openVKMapActivity()
            finish()
        }
    }

    private fun openVKMapActivity() {
        val runSettings = Intent(
            applicationContext,
            VKMapActivity::class.java
        )

        startActivity(runSettings)
    }

    private fun openSetPointActivity() {
        val runSettings = Intent(
            applicationContext,
            SetPointActivity::class.java
        )

        startActivity(runSettings)
    }
}