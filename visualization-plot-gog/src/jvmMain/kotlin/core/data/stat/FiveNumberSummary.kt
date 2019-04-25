package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.base.gcommon.collect.Ordering
import java.util.*

/**
 * For a set of data, the minimum, first quartile, median, third quartile, and maximum.
 * Note: A boxplot is a visual display of the five-number summary.
 */
internal class FiveNumberSummary {

    val min: Double
    val max: Double
    val median: Double
    val firstQuartile: Double
    // 25 %
    val thirdQuartile: Double    // 75 %

    private fun medianAtPointer(l: List<Double>, pointer: Double): Double {
        val rint = Math.rint(pointer)
        return if (pointer == rint) {
            l[pointer.toInt()]
        } else (l[Math.ceil(pointer).toInt()] + l[Math.floor(pointer).toInt()]) / 2.0
    }

    constructor(data: List<Double>) {
        val sorted = Ordering.natural<Double>().sortedCopy(data)
        if (sorted.isEmpty()) {
            thirdQuartile = java.lang.Double.NaN
            firstQuartile = thirdQuartile
            median = firstQuartile
            max = median
            min = max
        } else if (sorted.size == 1) {
            thirdQuartile = sorted.get(0)
            firstQuartile = thirdQuartile
            median = firstQuartile
            max = median
            min = max
        } else {
            val maxIndex = sorted.size - 1

            min = sorted.get(0)
            max = sorted.get(maxIndex)
            median = medianAtPointer(sorted, maxIndex * .5)
            firstQuartile = medianAtPointer(sorted, maxIndex * .25)
            thirdQuartile = medianAtPointer(sorted, maxIndex * .75)
        }
    }

    constructor(min: Double, max: Double, median: Double, firstQuartile: Double, thirdQuartile: Double) {
        this.min = min
        this.max = max
        this.median = median
        this.firstQuartile = firstQuartile
        this.thirdQuartile = thirdQuartile
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as FiveNumberSummary?
        return java.lang.Double.compare(that!!.min, min) == 0 &&
                java.lang.Double.compare(that.max, max) == 0 &&
                java.lang.Double.compare(that.median, median) == 0 &&
                java.lang.Double.compare(that.firstQuartile, firstQuartile) == 0 &&
                java.lang.Double.compare(that.thirdQuartile, thirdQuartile) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(min, max, median, firstQuartile, thirdQuartile)
    }
}
