package jetbrains.livemap.core.ecs

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.livemap.core.rendering.primitives.RenderBox

class ClickableComponent(private val myRect: RenderBox) : EcsComponent {
    val rect = DoubleRectangle(myRect.origin, myRect.dimension)
}
