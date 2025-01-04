package com.example.myreturner

data class MapLocation(
    val latitude: Double? = null, // широта [-90, 90]
    val longitude: Double? = null, // долгота [-180, 180]
    val speed: Float? = null, // скорость
    val bearing: Float? = null, // направление [0, 360]
    val accuracy: Float? = null, // точность
    val altitude: Double? = null // высота над уровнем моря в метрах
)
