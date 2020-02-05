/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvasFigure

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl

interface CanvasFigure {
    fun bounds(): ReadableProperty<DoubleRectangle>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
