/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.mouse

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.event.MouseWheelEvent
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionTarget

class MouseWheelInteraction(
    private val ctx: InteractionContext
) : Disposable {
    operator fun component1() = target
    operator fun component2() = zoomOrigin
    operator fun component3() = zoomDelta

    private var disposed = false
    private val reg: CompositeRegistration = CompositeRegistration()

    private var _target: InteractionTarget? = null
    val target: InteractionTarget
        get():InteractionTarget {
            return _target ?: throw IllegalStateException("Mouse wheel zoom target wasn't acquired.")
        }

    // Zoom origin in plot coordinates
    private var zoomOrigin: DoubleVector = DoubleVector.ZERO
        private set

    private var zoomDelta: Double = 0.0
        private set

    init {
        ctx.checkSupported(
            listOf(
                MouseEventSpec.MOUSE_WHEEL_ROTATED,
            )
        )
    }

    fun loop(
        onZoomed: ((MouseWheelInteraction) -> Unit)
    ) {
        check(!disposed) { "Disposed." }
        check(_target == null) { "Mouse wheel zoom has already started." }

        reg.add(
            ctx.eventsManager.onMouseEvent(MouseEventSpec.MOUSE_WHEEL_ROTATED) { _, e ->
                @Suppress("NAME_SHADOWING")
                val e = e as MouseWheelEvent
                e.preventDefault = true

                zoomOrigin = e.location.toDoubleVector()
                zoomDelta = e.scrollAmount
                _target = ctx.findTarget(e.location.toDoubleVector())
                onZoomed(this)
            }
        )
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            _target = null
            reg.dispose()
        }

    }
}