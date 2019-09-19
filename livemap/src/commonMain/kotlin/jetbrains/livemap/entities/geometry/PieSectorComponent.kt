package jetbrains.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent

class PieSectorComponent : EcsComponent {
    var radius = 0.0
    var startAngle = 0.0
    var endAngle = 0.0
}