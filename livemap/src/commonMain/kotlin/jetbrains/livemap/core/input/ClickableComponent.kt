package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox

class ClickableComponent(private val myRect: RenderBox) : EcsComponent {
    val rect = DoubleRectangle(myRect.origin, myRect.dimension)
}
