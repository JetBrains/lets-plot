package jetbrains.datalore.maps.livemap.entities.geometry

import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.core.ecs.EcsComponent

class WorldGeometryComponent : EcsComponent {
    var geometry: Geometry? = null
}
