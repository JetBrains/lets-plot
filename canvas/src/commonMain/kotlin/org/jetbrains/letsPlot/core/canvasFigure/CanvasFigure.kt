/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvasFigure

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d

interface CanvasFigure : SomeFig {
    val size: Vector

    fun draw(context2d: Context2d)
    fun onRepaintRequest(handler: () -> Unit): Registration
    fun mapToCanvas(canvasPeer: CanvasPeer): Registration
}
