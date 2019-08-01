package jetbrains.livemap.core.rendering

import jetbrains.livemap.core.rendering.primitives.RenderObject

interface SpriteSheet {
    fun add(renderObject: RenderObject)
    fun add(obj: Any)

    fun getSprite(obj: Any): Sprite

    operator fun contains(renderObject: RenderObject): Boolean
    operator fun contains(obj: Any): Boolean
}