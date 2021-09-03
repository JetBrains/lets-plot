/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

class FlippedCoordinateSystem(val actual: CoordinateSystem) : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector {
        return actual.toClient(p.flip())
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return actual.fromClient(p).flip()
    }

    override fun isPointInLimits(p: DoubleVector, isClient: Boolean): Boolean {
        return actual.isPointInLimits(p, isClient)
    }

    override fun isRectInLimits(rect: DoubleRectangle, isClient: Boolean): Boolean {
        return actual.isRectInLimits(rect, isClient)
    }

    override fun isPathInLimits(path: List<DoubleVector>): Boolean {
        return actual.isPathInLimits(path)
    }

    override fun isPolygonInLimits(polygon: List<DoubleVector>): Boolean {
        return actual.isPolygonInLimits(polygon)
    }

    override val xClientLimit: ClosedRange<Double>?
        get() = actual.yClientLimit

    override val yClientLimit: ClosedRange<Double>?
        get() = actual.xClientLimit
}