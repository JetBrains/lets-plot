/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.getEntities2
import jetbrains.livemap.core.ecs.onEachEntity2
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.toClientPoint
import jetbrains.livemap.ui.UiService


class HoverObjectDetectionSystem(
    private val uiService: UiService,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    override fun initImpl(context: LiveMapContext) {
        super.initImpl(context)
        createEntity("hover_object")
            .add(SearchResultComponent())
            .add(MouseInputComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity2<SearchResultComponent, MouseInputComponent> { _, hoverObjectComponent, mouseInputComponent ->
            if (mouseInputComponent.doubleClickEvent != null) {
                // Only fixes ghost result that appears for a split second when mouse starts to move AFTER zoom.
                // Tooltips update themselves only on a mouse move, we can't hide them from here if mouse is not moving.
                // To hide them on a zoom start we handle the DOUBLE_CLICK mouse event here:
                // jetbrains/datalore/plot/builder/interact/TooltipRenderer.kt:79
                hoverObjectComponent.clearResult()
                return
            }

            if (mouseInputComponent.dragState != null) {
                // On drag show no results
                hoverObjectComponent.clearResult()
                return
            }

            val mouseLocation = mouseInputComponent.moveEvent?.location?.toClientPoint() ?: return
            if (uiService.containsElementAtCoord(mouseLocation)) {
                hoverObjectComponent.clearResult()
                return
            }

            if (context.camera.isZoomFractionChanged && !context.camera.isZoomLevelChanged) {
                // mouse moves while zooming in/out - show no results
                hoverObjectComponent.clearResult()
                return
            }

            if (
                hoverObjectComponent.cursorPosition == mouseLocation &&
                hoverObjectComponent.zoom?.toDouble() == context.camera.zoom
            ) {
                // same mouse position - same result
                return
            }

            val objects = getEntities2<LocatorComponent, IndexComponent>()
                .mapNotNull { it.get<LocatorComponent>().locator.search(mouseLocation, it) }
                .groupBy(HoverObject::layerIndex)
                .values
                .filter(List<HoverObject>::isNotEmpty)
                .mapNotNull { hoverObjects -> hoverObjects.first().locator.reduce(hoverObjects) }

            hoverObjectComponent.apply {
                cursorPosition = mouseLocation
                zoom = context.camera.zoom.toInt()
                hoverObjects = objects
            }
        }
    }

    private fun SearchResultComponent.clearResult() {
        cursorPosition = null
        zoom = null
        hoverObjects = emptyList()
    }
}
