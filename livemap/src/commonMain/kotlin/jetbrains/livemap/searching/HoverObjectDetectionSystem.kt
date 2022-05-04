/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.toClientPoint
import jetbrains.livemap.ui.UiService


class HoverObjectComponent : EcsComponent {
    var searchResult: SearchResult? = null
    var zoom : Int? = null
    var cursotPosition : Vec<Client>? = null
}

class HoverObjectDetectionSystem(
    private val uiService: UiService,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    override fun initImpl(context: LiveMapContext) {
        super.initImpl(context)
        createEntity("hover_object")
            .add(HoverObjectComponent())
            .add(MouseInputComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingletonEntity<HoverObjectComponent>().let { hoverObject ->
            val mouseInputComponent = hoverObject.get<MouseInputComponent>()

            if (mouseInputComponent.moveEvent == null) {
                return
            }

            val mouseLocation: Vec<Client> = mouseInputComponent.moveEvent!!.location.toClientPoint()

            val hoverObjectComponent = hoverObject.get<HoverObjectComponent>()
            if (
                hoverObjectComponent.cursotPosition == mouseLocation &&
                hoverObjectComponent.zoom?.toDouble() == context.camera.zoom
            ) {
                // same mouse position - same result
                return
            }

            if (context.camera.isZoomFractionChanged && !context.camera.isZoomLevelChanged) {
                // on zoom do not search
                hoverObjectComponent.apply {
                    cursotPosition = null
                    zoom = null
                    searchResult = null
                }
                return
            }

            if (mouseInputComponent.dragState != null) {
                // On panning an object and a mouse are synchronized and there is no need to do a search again, only update cursor position
                hoverObjectComponent.cursotPosition = mouseLocation
                return
            }

            if (uiService.containsElementAtCoord(mouseLocation)) {
                hoverObjectComponent.apply {
                    cursotPosition = null
                    zoom = null
                    searchResult = null
                }
                return
            }

            hoverObjectComponent.apply {
                cursotPosition = mouseLocation
                zoom = context.camera.zoom.toInt()
                searchResult = getEntities(SEARCH_COMPONENTS)
                    .map { it.get<LocatorComponent>().locatorHelper.search(mouseLocation, it) }
                    .filterNotNull()
                    .sortedByDescending(SearchResult::layerIndex)
                    .firstOrNull()
            }
        }
    }
}
