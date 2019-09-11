package jetbrains.livemap.entities.scaling

import jetbrains.livemap.core.ecs.EcsComponent

class ScaleComponent : EcsComponent {
    var scale = 1.0
    var zoom: Int = 0
}