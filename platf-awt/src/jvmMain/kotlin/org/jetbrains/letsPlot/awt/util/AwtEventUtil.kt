/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.util

import org.jetbrains.letsPlot.commons.event.Button
import org.jetbrains.letsPlot.commons.event.KeyModifiers
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.geometry.Vector
import java.awt.event.MouseEvent as AwtMouseEvent

object AwtEventUtil {

    fun translate(e: AwtMouseEvent, offset: Vector = Vector.ZERO): MouseEvent {
        return MouseEvent(
            e.x - offset.x,
            e.y - offset.y,
            getButton(e),
            getModifiers(e)
        )
    }

    private fun getButton(e: AwtMouseEvent): Button {
        return when (e.button) {
            AwtMouseEvent.BUTTON1 -> Button.LEFT
            AwtMouseEvent.BUTTON2 -> Button.MIDDLE
            AwtMouseEvent.BUTTON3 -> Button.RIGHT
            else -> Button.NONE
        }
    }

    private fun getModifiers(e: AwtMouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }
}
