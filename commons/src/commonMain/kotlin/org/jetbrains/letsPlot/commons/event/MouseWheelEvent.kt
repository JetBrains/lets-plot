/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

class MouseWheelEvent(
    x: Int,
    y: Int,
    button: Button,
    modifiers: KeyModifiers,
    val scrollAmount: Double,
) : MouseEvent(x, y, button, modifiers)