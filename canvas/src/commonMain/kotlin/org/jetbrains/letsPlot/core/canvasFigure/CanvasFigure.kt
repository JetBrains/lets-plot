/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvasFigure

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.canvas.CanvasControl

interface CanvasFigure : SomeFig {
    fun bounds(): ReadableProperty<Rectangle>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
