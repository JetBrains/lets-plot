package jetbrains.datalore.visualization.plot.base.geom.util

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics

class MappedAesthetics(
        private val myAesthetics: Aesthetics,
        private val myPointAestheticsMapper: (DataPointAesthetics) -> DataPointAesthetics) : Aesthetics {

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

    override fun range(aes: Aes<Double>): ClosedRange<Double> {
        throw IllegalStateException("MappedAesthetics.range: not implemented $aes")
    }

    override fun overallRange(aes: Aes<Double>): ClosedRange<Double> {
        throw IllegalStateException("MappedAesthetics.overallRange: not implemented $aes")
    }

    override fun resolution(aes: Aes<Double>, naValue: Double): Double {
        throw IllegalStateException("MappedAesthetics.resolution: not implemented $aes")
    }

    override fun numericValues(aes: Aes<Double>): Iterable<Double> {
        throw IllegalStateException("MappedAesthetics.numericValues: not implemented $aes")
    }

    override fun groups(): Iterable<Int> {
        return myAesthetics.groups()
    }
}
