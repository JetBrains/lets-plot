/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.mouse

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionTarget

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

    init {
        ctx.checkSupported(
            listOf(
                MouseEventSpec.MOUSE_DOUBLE_CLICKED,
            )
        )
    }

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
            disposed = true
            _target = null
            reg.dispose()
        }
    }
}