/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.builder.interact.ui.EventsManager

internal class MouseDragInteraction(
    private val eventsManager: EventsManager,
    private val geomBoundsList: List<DoubleRectangle>
) : Disposable {

    @Suppress("MemberVisibilityCanBePrivate")
    var started: Boolean = false
        private set
    var completed: Boolean = false
        private set
    var aborted: Boolean = false
        private set

    var geomBounds: DoubleRectangle = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        private set
        get():DoubleRectangle {
            require(started) { "Mouse drag target wasn't acquired." }
            return field
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
            eventsManager.onMouseEvent(MouseEventSpec.MOUSE_RELEASED) { _, e ->
                if (started && !(completed || aborted)) {
                    val absCoord = e.location.toDoubleVector()
                    completed = true
                    dragTo = absCoord
                    onCompleted(this)
                }
            }
        )

        reg.add(
            eventsManager.onMouseEvent(MouseEventSpec.MOUSE_DRAGGED) { _, e ->
                if (!(completed || aborted)) {
                    val absCoord = e.location.toDoubleVector()
                    if (!started) {
                        val target = geomBoundsList.find { it.contains(absCoord) }
                        if (target != null) {
                            started = true
                            geomBounds = target
                            dragFrom = absCoord
                            dragTo = absCoord
                            onStarted(this)
                        }
                    } else {
                        dragTo = absCoord
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
        started = false
        completed = false
        aborted = false
    }

    override fun dispose() {
        if (!disposed) {
            println("MouseDragInteraction dispose.")
            disposed = true
            reg.dispose()
        }
    }
}