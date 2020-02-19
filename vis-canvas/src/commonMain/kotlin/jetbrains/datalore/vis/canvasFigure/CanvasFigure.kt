/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvasFigure

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.vis.canvas.CanvasControl

interface CanvasFigure : SomeFig {
    fun dimension(): ReadableProperty<DoubleVector>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
