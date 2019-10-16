package jetbrains.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent


class ScreenGeometryComponent : EcsComponent {
    lateinit var geometry: ClientGeometry
    var zoom: Int = 0
}
