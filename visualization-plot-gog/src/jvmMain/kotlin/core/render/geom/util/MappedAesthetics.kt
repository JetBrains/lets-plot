package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

import java.util.stream.StreamSupport

class MappedAesthetics(private val myAesthetics: Aesthetics, private val myPointAestheticsMapper: Function<DataPointAesthetics, DataPointAesthetics>) : Aesthetics {

    override val isEmpty: Boolean
        get() = myAesthetics.isEmpty

    override fun dataPointAt(index: Int): DataPointAesthetics {
        return myPointAestheticsMapper.apply(myAesthetics.dataPointAt(index))
    }

    override fun dataPointCount(): Int {
        return myAesthetics.dataPointCount()
    }

    override fun dataPoints(): Iterable<DataPointAesthetics> {
        val source = myAesthetics.dataPoints()
        val stream = StreamSupport.stream(source.spliterator(), false)
        return Iterable { stream.map { value -> myPointAestheticsMapper.apply(value) }.iterator() }
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
