/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil.flipAes

class YOrientationAesthetics(
    private val aesthetics: Aesthetics
) : MappedAesthetics(
    aesthetics = aesthetics,
    pointAestheticsMapper = ::flipDataPointOrientation
) {

    override fun range(aes: Aes<Double>): DoubleSpan? {
        return aesthetics.range(flipAes(aes))
    }

    override fun resolution(aes: Aes<Double>, naValue: Double): Double {
        return aesthetics.resolution(flipAes(aes), naValue)
    }

    override fun numericValues(aes: Aes<Double>): Iterable<Double?> {
        return aesthetics.numericValues(flipAes(aes))
    }

    companion object {
        private fun flipDataPointOrientation(p: DataPointAesthetics): DataPointAesthetics {
            return YOrientationDataPointAesthetics(p)
        }
    }

    private class YOrientationDataPointAesthetics(p: DataPointAesthetics) : DataPointAestheticsDelegate(p) {
        override fun <T> get(aes: Aes<T>): T? {
            return super.get(
                flipAes(aes)
            )
        }
    }
}