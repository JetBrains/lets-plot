package jetbrains.datalore.visualization.plot.gog.server.core.data.stat.regression

import org.apache.commons.math3.stat.descriptive.rank.Percentile
import java.util.*

internal object RegressionUtil {

    // sample m data randomly
    fun <T> sampling(data: List<T>, m: Int): ArrayList<T> {
        val index = sampleInt(data.size, m)
        val result = ArrayList<T>()
        for (i in index) {
            result.add(data[i])
        }
        return result
    }

    // sample m int from 0..n-1
    private fun sampleInt(n: Int, m: Int): IntArray {
        if (n < m || m < 0) {
            throw IllegalArgumentException("Sample $m data from $n data is impossible!")
        }
        val perm = IntArray(n)
        for (i in 0 until n) {
            perm[i] = i
        }

        val result = IntArray(m)
        for (j in 0 until m) {
            val r = j + (Math.random() * (n - j)).toInt()
            result[j] = perm[r]
            perm[r] = perm[j]
        }
        return result
    }

    fun percentile(data: List<Double>, p: Double): Double {
        val per = Percentile()
        return per.evaluate(data.toDoubleArray(), p * 100)
    }
}
