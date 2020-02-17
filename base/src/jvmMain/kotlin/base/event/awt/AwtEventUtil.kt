/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.event.awt

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import java.awt.event.MouseEvent as AwtMouseEvent

object AwtEventUtil {

    fun translate(e: AwtMouseEvent, offset: Vector = Vector.ZERO): MouseEvent {
        return MouseEvent(
            e.x - offset.x,
            e.y - offset.y,
            getButton(e),
            getModifiers(e))
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
