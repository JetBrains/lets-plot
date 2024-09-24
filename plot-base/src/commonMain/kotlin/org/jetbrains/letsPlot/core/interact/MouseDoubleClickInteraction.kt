/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable

internal class MouseDoubleClickInteraction(
    private val ctx: InteractionContext
) : Disposable {

    private var _target: InteractionTarget? = null
    val target: InteractionTarget
        get():InteractionTarget {
            return _target ?: throw IllegalStateException("Mouse double-click target wasn't found.")
        }

    private var disposed = false
    private val reg: CompositeRegistration = CompositeRegistration()

    fun loop(
        onAction: ((MouseDoubleClickInteraction) -> Unit)
    ) {
        check(!disposed) { "Disposed." }

        reg.add(
            ctx.eventsManager.onMouseEvent(MouseEventSpec.MOUSE_DOUBLE_CLICKED) { _, e ->
                // Coordinate relative to the entire plot.
                val plotCoord = e.location.toDoubleVector()
                ctx.findTarget(plotCoord)?.let {
                    _target = it
                    onAction(this)
                }
            }
        )
    }

    override fun dispose() {
        if (!disposed) {
            println("MouseDoubleClickInteraction dispose.")
            disposed = true
            _target = null
            reg.dispose()
        }
    }
}