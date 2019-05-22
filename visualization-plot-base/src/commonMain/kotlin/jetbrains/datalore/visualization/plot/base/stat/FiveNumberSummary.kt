package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.Ordering
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

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
        val rint = round(pointer)
        return if (pointer == rint) {
            l[pointer.toInt()]
        } else (l[ceil(pointer).toInt()] + l[floor(pointer).toInt()]) / 2.0
    }

    constructor(data: List<Double>) {
        val sorted = Ordering.natural<Double>().sortedCopy(data)
        if (sorted.isEmpty()) {
            thirdQuartile = Double.NaN
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as FiveNumberSummary?
        return that!!.min.compareTo(min) == 0 &&
                that.max.compareTo(max) == 0 &&
                that.median.compareTo(median) == 0 &&
                that.firstQuartile.compareTo(firstQuartile) == 0 &&
                that.thirdQuartile.compareTo(thirdQuartile) == 0
    }

    override fun hashCode(): Int {
        return arrayOf(min, max, median, firstQuartile, thirdQuartile).hashCode()
    }
}
