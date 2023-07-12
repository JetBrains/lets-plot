/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point

import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

interface PointShape {
    val code: Int

    fun size(dataPoint: DataPointAesthetics, fatten: Double = 1.0): Double

    fun strokeWidth(dataPoint: DataPointAesthetics): Double
//    fun create(location: DoubleVector, dataPoint: DataPointAesthetics): SvgSlimObject
}
