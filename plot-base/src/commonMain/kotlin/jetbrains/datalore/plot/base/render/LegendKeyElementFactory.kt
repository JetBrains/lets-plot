/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.vis.svg.SvgGElement

interface LegendKeyElementFactory {
    fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement

    fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        val size = 2 * strokeWidth + 4
        return DoubleVector(size, size)
    }

}
