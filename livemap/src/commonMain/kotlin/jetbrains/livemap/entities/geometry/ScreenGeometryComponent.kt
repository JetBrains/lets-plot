package jetbrains.datalore.maps.livemap.entities.geometry

import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.core.ecs.EcsComponent


class ScreenGeometryComponent : EcsComponent {
    lateinit var geometry: Geometry
    var zoom: Int = 0
}
