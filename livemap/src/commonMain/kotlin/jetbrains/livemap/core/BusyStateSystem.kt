/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.BusyStateSystem.EntitiesState.BUSY
import jetbrains.livemap.core.BusyStateSystem.EntitiesState.NOT_BUSY
import jetbrains.livemap.core.BusyStateSystem.MarkerState.NOT_SHOWING
import jetbrains.livemap.core.BusyStateSystem.MarkerState.SHOWING
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.rendering.primitives.Arc
import jetbrains.livemap.core.rendering.primitives.Circle
import jetbrains.livemap.core.rendering.primitives.Frame
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.ui.UiService
import kotlin.math.PI

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
    private var myStartAngle = 0.0
    private var mySpinnerArc = Arc()

    override fun initImpl(context: EcsContext) {
        val pos = DoubleVector(14.0, 169.0)
        val size = 26.0
        val dim = DoubleVector(size, size)


        spinnerGraphics =
            Frame(pos, listOf(
                Circle().apply {
                    origin = DoubleVector.ZERO
                    dimension = dim
                    fillColor = Color.WHITE
                    strokeColor = Color.LIGHT_GRAY
                    strokeWidth = 1.0
                },

                Circle().apply {
                    origin = DoubleVector(4.0, 4.0)
                    dimension = DoubleVector(18.0, 18.0)
                    fillColor = Color.TRANSPARENT
                    strokeColor = Color.LIGHT_GRAY
                    strokeWidth = 2.0
                },

                mySpinnerArc.apply {
                    origin = DoubleVector(4.0, 4.0)
                    dimension = DoubleVector(18.0, 18.0)
                    strokeColor = Color.parseHex("#70a7e3")
                    strokeWidth = 2.0
                    angle = PI / 4
                }
            ))
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        val entitiesState = BUSY.takeIf { componentManager.count<BusyStateComponent>() > 0 } ?: NOT_BUSY
        val markerState = SHOWING.takeIf { componentManager.count<BusyMarkerComponent>() > 0 } ?: NOT_SHOWING
        myStartAngle += (2 * PI * dt) / 1000

        when (Pair(entitiesState, markerState)) {
            Pair(BUSY, SHOWING) -> { mySpinnerArc.apply { startAngle = myStartAngle } }
            Pair(NOT_BUSY, NOT_SHOWING) -> {}
            Pair(NOT_BUSY, SHOWING) -> {
                spinnerEntity!!.remove()
            }
            Pair(BUSY, NOT_SHOWING) -> {
                spinnerEntity = uiService
                    .addRenderable(spinnerGraphics, "ui_busy_marker")
                    .add(BusyMarkerComponent())
            }
        }
    }

    private enum class EntitiesState {
        BUSY,
        NOT_BUSY
    }

    private enum class MarkerState {
        SHOWING,
        NOT_SHOWING
    }
}