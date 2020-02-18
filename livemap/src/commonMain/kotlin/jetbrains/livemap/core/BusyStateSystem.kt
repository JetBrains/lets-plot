/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.BusyStateSystem.EntitesState.BUSY
import jetbrains.livemap.core.BusyStateSystem.EntitesState.NOT_BUSY
import jetbrains.livemap.core.BusyStateSystem.MarkerState.NOT_SHOWING
import jetbrains.livemap.core.BusyStateSystem.MarkerState.SHOWING
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.rendering.primitives.Circle
import jetbrains.livemap.core.rendering.primitives.Frame
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.ui.UiService

/**
 * Entities with this component causes the map to show a spinner.
 * Have to be used to mark an important missing data - tiles, fragments, objects waiting for
 * geocoding and not rendering.
 */
class BusyStateComponent : EcsComponent {

}

class BusyMarkerComponent: EcsComponent {

}

class BusyStateSystem(
    componentManager: EcsComponentManager,
    private val uiService: UiService
) : AbstractSystem<EcsContext>(componentManager) {
    private lateinit var spinnerGraphics: RenderBox
    private var spinnerEntity: EcsEntity? = null

    override fun initImpl(context: EcsContext) {
        val pos = DoubleVector(18.0, 245.0)
        val size = 16.0
        val dim = DoubleVector(size, size)


        spinnerGraphics =
            Frame(pos, listOf(
                Circle().apply {
                    origin = DoubleVector.ZERO
                    dimension = dim
                    fillColor = Color.TRANSPARENT
                    strokeColor = Color.BLACK
                    strokeWidth = 2.0
                }
            ))
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        val entitiesState = BUSY.takeIf { componentManager.count<BusyStateComponent>() > 0 } ?: NOT_BUSY
        val markerState = SHOWING.takeIf { componentManager.count<BusyMarkerComponent>() > 0 } ?: NOT_SHOWING

        when (Pair(entitiesState, markerState)) {
            Pair(BUSY, SHOWING) -> {}
            Pair(NOT_BUSY, NOT_SHOWING) -> {}
            Pair(NOT_BUSY, SHOWING) -> {
                spinnerEntity!!.remove()
            }
            Pair(BUSY, NOT_SHOWING) -> {
                spinnerEntity = uiService.addRenderable(spinnerGraphics, "ui_busy_marker").addComponents { +BusyMarkerComponent() }
            }
        }
    }

    private enum class EntitesState {
        BUSY,
        NOT_BUSY
    }

    private enum class MarkerState {
        SHOWING,
        NOT_SHOWING
    }
}