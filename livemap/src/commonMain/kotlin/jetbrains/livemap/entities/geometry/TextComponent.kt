package jetbrains.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.obj2entity.TextSpec

class TextComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}