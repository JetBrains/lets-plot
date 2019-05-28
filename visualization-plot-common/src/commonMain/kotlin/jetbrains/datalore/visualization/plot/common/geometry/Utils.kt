package jetbrains.datalore.visualization.plot.common.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.abs

object Utils {
    fun calculateArea(ring: List<DoubleVector>): Double {
        var area = 0.0

        var j = ring.size - 1

        for (i in ring.indices) {
            val p1 = ring[i]
            val p2 = ring[j]

            area += (p2.x + p1.x) * (p2.y - p1.y)
            j = i
        }

        return abs(area / 2)
    }

    fun isClosed(list: List<DoubleVector>): Boolean {
        if (list.size < 2) {
            return true
        }

        val endIndex = list.size - 1
        return list[0] == list[endIndex]
    }
}
