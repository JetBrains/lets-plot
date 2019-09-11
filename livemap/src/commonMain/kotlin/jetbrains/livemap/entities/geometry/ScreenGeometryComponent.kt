package jetbrains.datalore.maps.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.entities.geometry.ClientGeometry


class ScreenGeometryComponent : EcsComponent {
    lateinit var geometry: ClientGeometry
    var zoom: Int = 0
}
