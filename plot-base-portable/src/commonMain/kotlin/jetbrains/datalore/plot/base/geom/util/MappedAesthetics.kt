/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics

open class MappedAesthetics(
    private val aesthetics: Aesthetics,
    private val pointAestheticsMapper: (DataPointAesthetics) -> DataPointAesthetics
) : Aesthetics {

    override val isEmpty: Boolean
        get() = aesthetics.isEmpty

    override fun dataPointAt(index: Int): DataPointAesthetics {
        return pointAestheticsMapper(aesthetics.dataPointAt(index))
    }

    override fun dataPointCount(): Int {
        return aesthetics.dataPointCount()
    }

    override fun dataPoints(): Iterable<DataPointAesthetics> {
        val source = aesthetics.dataPoints()
        return source.map { pointAestheticsMapper(it) }
    }

    override fun range(aes: Aes<Double>): DoubleSpan? {
        throw IllegalStateException("MappedAesthetics.range: not implemented $aes")
    }

    override fun resolution(aes: Aes<Double>, naValue: Double): Double {
        throw IllegalStateException("MappedAesthetics.resolution: not implemented $aes")
    }

    override fun numericValues(aes: Aes<Double>): Iterable<Double?> {
        throw IllegalStateException("MappedAesthetics.numericValues: not implemented $aes")
    }

    override fun groups(): Iterable<Int> {
        return aesthetics.groups()
    }
}
