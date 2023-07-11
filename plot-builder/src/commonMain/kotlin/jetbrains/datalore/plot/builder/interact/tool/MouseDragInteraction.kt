/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable

internal class MouseDragInteraction(
    private val ctx: InteractionContext
) : Disposable {

    private val started: Boolean
        get() = _target != null
    private var completed: Boolean = false
    private var aborted: Boolean = false

    private var _target: InteractionTarget? = null
    val target: InteractionTarget
        get():InteractionTarget {
            return _target ?: throw IllegalStateException("Mouse drag target wasn't acquired.")
        }

    // Coordinate relative to the entire plot.
    // Need to be translated to "geom" coordinate.
    var dragFrom: DoubleVector = DoubleVector.ZERO
        private set
        get():DoubleVector {
            require(started) { "Mouse drag target wasn't acquired." }
            return field
        }
    var dragTo: DoubleVector = DoubleVector.ZERO
        private set
        get():DoubleVector {
            require(started) { "Mouse drag target wasn't acquired." }
            return field
        }

    private var disposed = false
    private val reg: CompositeRegistration = CompositeRegistration()

    fun loop(
        onStarted: ((MouseDragInteraction) -> Unit),
        onDragged: ((MouseDragInteraction) -> Unit),
        onCompleted: ((MouseDragInteraction) -> Unit),
        onAborted: ((MouseDragInteraction) -> Unit)
    ) {
        check(!disposed) { "Disposed." }
        check(!started) { "Mouse drag has already started." }

        reg.add(
            ctx.eventsManager.onMouseEvent(MouseEventSpec.MOUSE_RELEASED) { _, e ->
                if (started && !(completed || aborted)) {
                    val absCoord = e.location.toDoubleVector()
                    completed = true
                    dragTo = absCoord
                    onCompleted(this)
                }
            }
        )

        reg.add(
            ctx.eventsManager.onMouseEvent(MouseEventSpec.MOUSE_DRAGGED) { _, e ->
                if (!(completed || aborted)) {
                    val plotCoord = e.location.toDoubleVector()
                    if (!started) {
                        ctx.findTarget(plotCoord)?.let {
                            _target = it
                            dragFrom = plotCoord
                            dragTo = plotCoord
                            onStarted(this)
                        }
                    } else {
                        dragTo = plotCoord
                        onDragged(this)
                    }
                }
            }
        )

        // ToDo: abort event?
    }

    fun reset() {
        check(!disposed) { "Disposed." }
        println("MouseDragInteraction reset.")
        _target = null
        completed = false
        aborted = false
    }

    override fun dispose() {
        if (!disposed) {
            println("MouseDragInteraction dispose.")
            disposed = true
            _target = null
            reg.dispose()
        }
    }
}