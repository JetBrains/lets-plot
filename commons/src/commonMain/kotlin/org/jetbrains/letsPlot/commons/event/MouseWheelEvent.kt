/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.geometry.Vector

class MouseWheelEvent(
    x: Int,
    y: Int,
    button: Button,
    modifiers: KeyModifiers,
    val scrollAmount: Double, // Normalized scroll amount. +-1.0 for regular wheel. Touchpad can produce any values.
) : MouseEvent(x, y, button, modifiers) {
    override fun at(location: Vector): MouseWheelEvent {
        return at(location.x, location.y)
    }

    override fun at(x: Int, y: Int): MouseWheelEvent {
        if (this.x == x && this.y == y) {
            return this
        }

        return MouseWheelEvent(x, y, button, modifiers, scrollAmount)
    }
 }