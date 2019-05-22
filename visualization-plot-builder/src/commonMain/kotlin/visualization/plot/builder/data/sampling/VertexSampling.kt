package jetbrains.datalore.visualization.plot.builder.data.sampling

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.util.MutableInteger
import jetbrains.datalore.visualization.plot.builder.data.PointSampling
import jetbrains.datalore.visualization.plot.builder.data.sampling.SamplingUtil.calculateRingLimits
import jetbrains.datalore.visualization.plot.builder.data.sampling.SamplingUtil.getRingIndex
import jetbrains.datalore.visualization.plot.builder.data.sampling.SamplingUtil.getRingLimit
import jetbrains.datalore.visualization.plot.builder.data.sampling.SamplingUtil.splitRings
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.common.geometry.PolylineSimplifier
import jetbrains.datalore.visualization.plot.common.geometry.Utils.isClosed

internal abstract class VertexSampling(sampleSize: Int) : SamplingBase(sampleSize), PointSampling {

    private fun simplify(points: List<DoubleVector>, limit: Int): List<Int> {
        return if (limit == 0) {
            emptyList()
        } else {
            simplifyInternal(points, limit)
        }
    }

    internal abstract fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int>

    override fun apply(population: DataFrame): DataFrame {
        checkArgument(isApplicable(population))

        val rings = splitRings(population)
        val limits = if (rings.size == 1 && !isClosed(rings[0]))
            listOf(sampleSize)
        else
            calculateRingLimits(rings, sampleSize)

        val indices = ArrayList<Int>()
        val ringBase = MutableInteger(0)

        (0 until limits.size)
                .map { Pair(it, limits[it]) }
                .forEach { p ->
                    simplify(rings[getRingIndex(p)], getRingLimit(p))
                            .forEach { index -> indices.add(ringBase.get() + index) }
                    ringBase.getAndAdd(rings[getRingIndex(p)].size)
                }

        return population.selectIndices(indices)
    }

    internal class VertexVwSampling(sampleSize: Int) : VertexSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int> {
            return PolylineSimplifier.visvalingamWhyatt(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "vertex_vw"
        }
    }

    internal class VertexDpSampling(sampleSize: Int) : VertexSampling(sampleSize) {

        override val expressionText: String
            get() = "sampling_" + ALIAS + "(" +
                    "n=" + sampleSize + ")"

        override fun simplifyInternal(points: List<DoubleVector>, limit: Int): List<Int> {
            return PolylineSimplifier.douglasPeucker(points).setCountLimit(limit).indices
        }

        companion object {
            const val ALIAS = "vertex_dp"
        }
    }

    internal class DoubleVectorComponentsList(private val myXValues: List<Any>, private val myYValues: List<Any>) : AbstractList<DoubleVector>() {
        override val size: Int
            get() = myXValues.size

        override fun get(index: Int): DoubleVector {
            return createPoint(myXValues[index], myYValues[index])
        }
    }

    companion object {
        private fun createPoint(x: Any, y: Any): DoubleVector {
            if (x is String || y is String) {
                throw IllegalArgumentException("String coords are not supported yet")
            }

            if (!SeriesUtil.allFinite(x as Double, y as Double)) {
                throw IllegalArgumentException("Invalid coord")
            }

            return DoubleVector(x, y)
        }
    }
}
