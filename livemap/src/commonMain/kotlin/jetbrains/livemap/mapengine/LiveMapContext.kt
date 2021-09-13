/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.mapengine.camera.Camera

/**
 * opens are for tests
 */
open class LiveMapContext(
    open val mapProjection: MapProjection,
    mouseEventSource: MouseEventSource,
    open val mapRenderContext: MapRenderContext,
    private val errorHandler: (Throwable) -> Unit,
    open val camera: Camera
) : EcsContext(mouseEventSource) {
    fun raiseError(error: Throwable) = errorHandler(error)
}