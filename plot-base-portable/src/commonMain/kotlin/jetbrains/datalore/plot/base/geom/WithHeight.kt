/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics

interface WithHeight {
    fun heightSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan?
}