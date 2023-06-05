/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.placement

import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.core.layers.DirtyCanvasLayerComponent
import jetbrains.livemap.mapengine.LiveMapContext

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

            if (camera.panFrameDistance?.let { it != Client.ZERO_VEC } == true) {
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
