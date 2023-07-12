/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

interface WithHeight {
    fun heightSpan(p: DataPointAesthetics, coordAes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan?
}