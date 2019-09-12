package jetbrains.livemap

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.rendering.SpriteSheet
import jetbrains.livemap.projections.MapProjection

open class LiveMapContext(
    open val mapProjection: MapProjection,
    eventSource: MouseEventSource,
    open val mapRenderContext: MapRenderContext,
    val spriteSheet: SpriteSheet?
) : EcsContext(eventSource)