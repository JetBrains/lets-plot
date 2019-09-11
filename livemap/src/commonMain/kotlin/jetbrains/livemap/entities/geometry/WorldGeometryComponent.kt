package jetbrains.datalore.maps.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.entities.geometry.WorldGeometry

class WorldGeometryComponent : EcsComponent {
    var geometry: WorldGeometry? = null
}
