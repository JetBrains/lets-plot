/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface CoordinateSystem {
    fun toClient(p: DoubleVector): DoubleVector

    fun fromClient(p: DoubleVector): DoubleVector

    fun isPointInLimits(p: DoubleVector, isClient: Boolean = true): Boolean

    fun isRectInLimits(rect: DoubleRectangle, isClient: Boolean = true): Boolean

    fun isPathInLimits(path: List<DoubleVector>, isClient: Boolean = true): Boolean

    fun isPolygonInLimits(polygon: List<DoubleVector>, isClient: Boolean = true): Boolean

    val xClientLimit: ClosedRange<Double>?

    val yClientLimit: ClosedRange<Double>?
}
