package jetbrains.datalore.visualization.plot.gog.common.color

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.gcommon.collect.Ordering.Companion.natural

import java.util.ArrayList
import java.util.Arrays

class RGBChannelGen(private val myBaseValues: List<Int>) {
    private var myRange: ClosedRange<Int>? = null

    init {
        val min = natural<Int>().min(myBaseValues)
        val max = natural<Int>().max(myBaseValues)
        for (r in RANGES) {
            if (r.contains(min) || r.contains(max)) {
                if (myRange == null) {
                    myRange = r
                } else {
                    myRange = myRange!!.span(r)
                }
            }
        }
    }

    /**
     * @param maxCount - count of new values required
     * @return List of generated value, size may be different than maxCount
     */
    fun generate(maxCount: Int): List<Int> {
        var genPerBaseValue = Math.ceil(maxCount.toDouble() / myBaseValues.size).toInt()
        genPerBaseValue = Math.min(maxValueCount() - 1, genPerBaseValue)

        var inc = Math.floor(maxValueCount().toDouble() / (genPerBaseValue + 1)).toInt()
        // make increment a bit less regular
        inc = Math.max(1, (inc * 1.33).toInt())

        val values = ArrayList<Int>(maxCount)
        var baseValues = myBaseValues
        while (values.size < maxCount) {
            val nextBaseValues = ArrayList<Int>()
            for (baseValue in baseValues) {
                var genValue = baseValue + inc
                if (!myRange!!.contains(genValue)) {
                    genValue = myRange!!.lowerEndpoint() + (genValue - myRange!!.upperEndpoint())
                }
                values.add(genValue)
                nextBaseValues.add(genValue)
                if (values.size == maxCount) {
                    break
                }
            }

            baseValues = nextBaseValues
        }

        return values
    }

    private fun maxValueCount(): Int {
        return myRange!!.upperEndpoint() - myRange!!.lowerEndpoint() + 1
    }

    companion object {
        private val RANGES = Arrays.asList(
                ClosedRange.closed(0, 37),
                ClosedRange.closed(38, 97),
                ClosedRange.closed(98, 157),
                ClosedRange.closed(158, 217),
                ClosedRange.closed(218, 255)
        )
    }
}
