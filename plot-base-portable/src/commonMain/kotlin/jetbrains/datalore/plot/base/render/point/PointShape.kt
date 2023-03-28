/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point

import jetbrains.datalore.plot.base.DataPointAesthetics

interface PointShape {
    val code: Int

    fun size(dataPoint: DataPointAesthetics): Double

    fun strokeWidth(dataPoint: DataPointAesthetics): Double

    // ~ size + strokeWidth / 2, but considering shape
    fun totalSize(dataPoint: DataPointAesthetics): Double
//    fun create(location: DoubleVector, dataPoint: DataPointAesthetics): SvgSlimObject
}
