package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox

class ClickableComponent(rect: RenderBox) : EcsComponent {
    val rect = DoubleRectangle(rect.origin, rect.dimension)
}
