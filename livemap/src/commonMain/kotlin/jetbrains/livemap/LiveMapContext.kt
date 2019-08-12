package jetbrains.livemap

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.rendering.SpriteSheet
import jetbrains.livemap.projections.MapProjection

class LiveMapContext(
    val mapProjection: MapProjection,
    eventSource: MouseEventSource,
    val mapRenderContext: MapRenderContext,
    val spriteSheet: SpriteSheet?
) : EcsContext(eventSource)