/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem

class TransformedCoordinateSystem(
    private val coordSystem: CoordinateSystem,
    private val translate: DoubleVector,
    private val scale: DoubleVector
) : CoordinateSystem {
    override val isLinear: Boolean
        get() = coordSystem.isLinear

    override val isPolar: Boolean
        get() = coordSystem.isPolar

    override fun toClient(p: DoubleVector): DoubleVector? {
        val client = coordSystem.toClient(p) ?: return null

        return DoubleVector(
            (client.x  + translate.x) * scale.x,
            (client.y + translate.y) * scale.y
        )
    }


    override fun fromClient(p: DoubleVector): DoubleVector? {
        val client = DoubleVector(
            (p.x - translate.x) / scale.x,
            (p.y - translate.y) / scale.y
        )
        return coordSystem.fromClient(client)
    }

    override fun unitSize(p: DoubleVector): DoubleVector {
        val unitSize = coordSystem.unitSize(p)
        return DoubleVector(
            unitSize.x * scale.x,
            unitSize.y * scale.y
        )
    }

    override fun flip(): CoordinateSystem {
        return TransformedCoordinateSystem(
            coordSystem.flip(),
            translate,
            scale
        )
    }
}