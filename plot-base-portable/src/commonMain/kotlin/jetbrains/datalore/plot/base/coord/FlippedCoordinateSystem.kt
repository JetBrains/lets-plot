/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.CoordinateSystem

internal class FlippedCoordinateSystem(
    private val actual: DefaultCoordinateSystem
) : DefaultCoordinateSystem(
    actual.toClientOffsetX,
    actual.toClientOffsetY,
    actual.fromClientOffsetX,
    actual.fromClientOffsetY,
    clientLimitsX = actual.clientLimitsX,
    clientLimitsY = actual.clientLimitsY,
    projection = FlippedProjection(actual.projection)
) {
    override fun toClient(p: DoubleVector): DoubleVector {
        return super.toClient(p.flip())
    }

    override fun flip(): CoordinateSystem {
        return actual
    }

    private class FlippedProjection(private val orig: Projection) : Projection {
        override fun project(v: DoubleVector): DoubleVector? {
            return orig.project(v.flip())?.flip()
        }

        override fun invert(v: DoubleVector): DoubleVector? {
            return orig.invert(v.flip())?.flip()
        }

        override fun validRect(): DoubleRectangle = orig.validRect()

        override val cylindrical: Boolean = orig.cylindrical
    }
}