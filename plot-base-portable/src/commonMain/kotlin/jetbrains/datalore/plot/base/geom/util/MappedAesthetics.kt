/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics

class MappedAesthetics(
    private val myAesthetics: Aesthetics,
    private val myPointAestheticsMapper: (DataPointAesthetics) -> DataPointAesthetics
) : Aesthetics {

    override val isEmpty: Boolean
        get() = myAesthetics.isEmpty

    override fun dataPointAt(index: Int): DataPointAesthetics {
        return myPointAestheticsMapper(myAesthetics.dataPointAt(index))
    }

    override fun dataPointCount(): Int {
        return myAesthetics.dataPointCount()
    }

    override fun dataPoints(): Iterable<DataPointAesthetics> {
        val source = myAesthetics.dataPoints()
        return source.map { myPointAestheticsMapper(it) }
    }

    override fun range(aes: Aes<Double>): DoubleSpan {
        throw IllegalStateException("MappedAesthetics.range: not implemented $aes")
    }

    override fun resolution(aes: Aes<Double>, naValue: Double): Double {
        throw IllegalStateException("MappedAesthetics.resolution: not implemented $aes")
    }

    override fun numericValues(aes: Aes<Double>): Iterable<Double?> {
        throw IllegalStateException("MappedAesthetics.numericValues: not implemented $aes")
    }

    override fun groups(): Iterable<Int> {
        return myAesthetics.groups()
    }
}
