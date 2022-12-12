/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics

interface WithWidth {
    fun widthSpan(p: DataPointAesthetics, coordAes:Aes<Double>, resolution: Double, discrete: Boolean): DoubleSpan?
}