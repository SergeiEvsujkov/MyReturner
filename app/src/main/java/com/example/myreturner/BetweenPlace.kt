package com.example.myreturner

import kotlin.math.*


private const val AVERAGE_RADIUS_OF_EARTH = 6371.0


fun calculateDistance(
    userLat: Double,
    userLng: Double,
    venueLat: Double,
    venueLng: Double
): Int {
    val latDistance = Math.toRadians(userLat - venueLat)
    val lngDistance = Math.toRadians(userLng - venueLng)
    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(userLat)) *
            cos(Math.toRadians(venueLat)) *
            sin(lngDistance / 2) *
            sin(lngDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return ((AVERAGE_RADIUS_OF_EARTH * 1000 * c).roundToInt())
}


fun calculateAzimuth(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    // Переводим градусы в радианы
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    // Разница долгот
    val deltaLon = lon2Rad - lon1Rad

    // Вычисление азимута
    val x = sin(deltaLon) * cos(lat2Rad)
    val y = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)

    // Вычисление угла в радианах
    var azimuth = atan2(x, y)

    // Преобразуем в градусы
    azimuth = Math.toDegrees(azimuth)

    // Корректируем азимут на диапазон 0 - 360 градусов
    if (azimuth < 0) {
        azimuth += 360
    }

    return azimuth
}