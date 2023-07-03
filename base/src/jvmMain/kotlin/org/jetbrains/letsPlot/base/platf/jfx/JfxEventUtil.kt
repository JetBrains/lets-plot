/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.platf.jfx

import javafx.scene.input.MouseButton
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import kotlin.math.roundToInt
import javafx.scene.input.MouseEvent as JfxMouseEvent

object JfxEventUtil {
    fun translate(e: JfxMouseEvent, offset: Vector = Vector.ZERO): MouseEvent {
        return MouseEvent(
            e.x.roundToInt() - offset.x,
            e.y.roundToInt() - offset.y,
            getButton(e),
            getModifiers(e)
        )
    }

    private fun getModifiers(e: JfxMouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }

    private fun getButton(e: JfxMouseEvent): Button {
        return when (e.button) {
            MouseButton.PRIMARY -> Button.LEFT
            MouseButton.MIDDLE -> Button.MIDDLE
            MouseButton.SECONDARY -> Button.RIGHT
            else -> Button.NONE
        }
    }
}