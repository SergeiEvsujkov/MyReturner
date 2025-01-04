package com.example.myreturner

import ru.common.geo.mapssdk.LogoConfig
import ru.mail.maps.data.CompassLocationMode
import ru.mail.maps.data.LatLon
import ru.mail.maps.data.MapStyle

data class MapStartOptions(
    val center: LatLon, // точка начального расположения карты (учитываются только lat, lon)
    val zoomLevel: Float, // начальное значение уровня масштабирования (zoomLevel)
    val style: MapStyle, // стиль карты, может быть выбран из соответствующего enum или использован свой
    val compassLocationMode: CompassLocationMode, // настройка компаса, может быть выбран из соответствующего enum
    val logoConfig: LogoConfig // конфигурация логотипа
)
