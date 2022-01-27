/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
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
) {
    override fun toClient(p: DoubleVector): DoubleVector {
        return actual.toClient(p.flip())
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return actual.fromClient(p).flip()
    }

    override fun flip(): CoordinateSystem {
        throw IllegalStateException("'flip()' is not applicable to FlippedCoordinateSystem")
    }
}