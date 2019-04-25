package jetbrains.datalore.visualization.plot.gog.common.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.IntStream
import java.util.stream.Stream

class PolylineSimplifier private constructor(private val myPoints: List<DoubleVector>, strategy: RankingStrategy) {
    private val myWeights: List<Double>
    private var myWeightLimit = java.lang.Double.NaN
    private var myCountLimit = -1

    val points: List<DoubleVector>
        get() =
            indices.stream().map<DoubleVector> { myPoints[it] }.collect(toList())

    val indices: List<Int>
        get() {
            val sorted = IntStream.range(0, myPoints.size)
                    .mapToObj { i -> Pair(i, myWeights[i]) }
                    .filter { p -> !java.lang.Double.isNaN(getWeight(p)) }
                    .sorted(Comparator.comparing<Pair<Int, Double>, Double> { this.getWeight(it) }.reversed())


            val filtered: Stream<Pair<Int, Double>>
            if (isWeightLimitSet) {
                filtered = sorted.filter { p -> getWeight(p) > myWeightLimit }
            } else {
                filtered = sorted.limit(myCountLimit.toLong())
            }

            return filtered
                    .map<Int> { this.getIndex(it) }
                    .sorted()
                    .collect(toList())
        }

    private val isWeightLimitSet: Boolean
        get() = !java.lang.Double.isNaN(myWeightLimit)

    init {
        myWeights = strategy.getWeights(myPoints)
    }

    fun setWeightLimit(weightLimit: Double): PolylineSimplifier {
        myWeightLimit = weightLimit
        myCountLimit = -1
        return this
    }

    fun setCountLimit(countLimit: Int): PolylineSimplifier {
        myWeightLimit = java.lang.Double.NaN
        myCountLimit = countLimit
        return this
    }

    private fun getWeight(p: Pair<Int, Double>): Double {
        return p.second!!
    }

    private fun getIndex(p: Pair<Int, Double>): Int {
        return p.first!!
    }

    interface RankingStrategy {
        fun getWeights(points: List<DoubleVector>): List<Double>
    }

    companion object {

        fun visvalingamWhyatt(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(points, VisvalingamWhyattSimplification())
        }

        fun douglasPeucker(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(points, DouglasPeuckerSimplification())
        }
    }
}
