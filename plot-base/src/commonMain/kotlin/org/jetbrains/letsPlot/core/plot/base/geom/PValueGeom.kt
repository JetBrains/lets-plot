/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

class PValueGeom : TextGeom() {
    override fun pointLocationOrNull(point: DataPointAesthetics): DoubleVector? {
        val (xmin, xmax, y) = point.finiteOrNull(Aes.XMIN, Aes.XMAX, Aes.Y) ?: return null
        return DoubleVector((xmin + xmax) / 2.0, y)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}