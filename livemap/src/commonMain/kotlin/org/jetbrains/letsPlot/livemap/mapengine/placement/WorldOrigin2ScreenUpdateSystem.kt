/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.placement

import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayerComponent
import org.jetbrains.letsPlot.livemap.core.layers.DirtyCanvasLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext

class WorldOrigin2ScreenUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val camera = context.camera

        if (camera.isMoved || camera.isZoomFractionChanged) {
            requestRepaint()
        }
        if (camera.isZoomFractionChanged || camera.isMoved || camera.panDistance != null) {
            if (camera.isZoomLevelChanged ||
                camera.isZoomFractionChanged ||
                camera.isMoved
            ) {
                requestRepaint()
            }

            if (camera.panFrameDistance?.let { it != org.jetbrains.letsPlot.livemap.Client.ZERO_VEC } == true) {
                requestRepaint()
            }
        }
    }

    private fun requestRepaint() {
        getEntities<CanvasLayerComponent>().forEach {
            it.tag(::DirtyCanvasLayerComponent)
        }
    }
}
