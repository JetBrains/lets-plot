/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.isIntegerZoom
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.projection.Client


class HoverObjectComponent : EcsComponent {
    var searchResult: SearchResult? = null
    var zoom : Int? = null
    var cursotPosition : Vec<Client>? = null
}

class HoverObjectDetectionSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {
    override fun initImpl(context: LiveMapContext) {
        super.initImpl(context)
        createEntity("hover_object")
            .add(HoverObjectComponent())
            .add(MouseInputComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingletonEntity<HoverObjectComponent>().let { hoverObject ->
            val mouseInputComponent = hoverObject.get<MouseInputComponent>()
            val mouseLocation: Vec<Client> = mouseInputComponent
                .location
                ?.let { explicitVec(it.x, it.y) }
                ?: return // no mouse movement in this frame

            val hoverObjectComponent = hoverObject.get<HoverObjectComponent>()

            if (context.camera.isZoomChanged && !context.camera.isIntegerZoom) {
                // on zoom do not search
                hoverObjectComponent.apply {
                    cursotPosition = null
                    zoom = null
                    searchResult = null
                }
                return
            }

            if (hoverObjectComponent.cursotPosition == mouseLocation && context.camera.zoom == hoverObjectComponent.zoom?.toDouble() ?: Double.NaN) {
                // same mouse position - same result
                return;
            }

            if (mouseInputComponent.dragDistance != null) {
                // On panning an object and a mouse are synchronized and there is no need to do a search again, only update cursor position
                hoverObjectComponent.cursotPosition = mouseLocation
                return
            }

            hoverObjectComponent.apply {
                cursotPosition = mouseLocation
                zoom = context.camera.zoom.toInt()
                searchResult = getEntities(SEARCH_COMPONENTS)
                    .filter { it.get<LocatorComponent>().locatorHelper.isCoordinateInTarget(mouseLocation, it) }
                    .sortedByDescending { it.get<IndexComponent>().layerIndex }
                    .firstOrNull()
                    ?.let {
                        SearchResult(
                            layerIndex = it.get<IndexComponent>().layerIndex,
                            index = it.get<IndexComponent>().index,
                            color = it.get<LocatorComponent>().locatorHelper.getColor(it)
                        )
                    }
            }
        }
    }
}
