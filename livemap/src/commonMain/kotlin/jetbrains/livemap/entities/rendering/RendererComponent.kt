package jetbrains.livemap.entities.rendering

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class RendererComponent(var renderer: Renderer) : EcsComponent {

    companion object {
        internal fun getRenderer(entity: EcsEntity): Renderer {
            return get(entity).renderer
        }

        operator fun get(entity: EcsEntity): RendererComponent {
            return entity.getComponent()
        }

        fun setRenderer(entity: EcsEntity, renderer: Renderer) {
            get(entity).renderer = renderer
        }
    }
}