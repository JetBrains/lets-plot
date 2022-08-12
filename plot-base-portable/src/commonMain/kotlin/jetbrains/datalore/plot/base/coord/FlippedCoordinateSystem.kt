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
    coordMapper = actual.coordMapper
) {
    override fun toClient(p: DoubleVector): DoubleVector {
        return super.toClient(p.flip())
    }

    override fun flip(): CoordinateSystem {
        return actual
    }
}