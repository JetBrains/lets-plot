/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvasFigure.CanvasFigure

interface LiveMapProvider {
    fun createLiveMap(dimension: DoubleVector): CanvasFigure
}
