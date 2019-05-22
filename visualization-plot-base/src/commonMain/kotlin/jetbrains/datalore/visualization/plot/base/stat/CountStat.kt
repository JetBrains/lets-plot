package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.StatContext
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.TransformVar
import jetbrains.datalore.visualization.plot.base.util.MutableDouble
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

/**
 * Counts the number of cases at each x position.
 * (or if the weight aesthetic is supplied, the sum of the weights)
 */
internal class CountStat : BaseStat(DEF_MAPPING) {

    override fun apply(data: DataFrame, statCtx: StatContext): DataFrame {
        if (data.hasNoOrEmpty(TransformVar.X)) {
            return DataFrame.Builder.emptyFrame()
        }

        val valuesX = data.getNumeric(TransformVar.X)
        val weight = StatUtil.weightVector(valuesX.size, data)

        val statX = ArrayList<Double>()
        val statCount = ArrayList<Double>()

        val countByX = countByX(valuesX, weight)
        for (x in countByX.keys) {
            statX.add(x)
            statCount.add(countByX[x]!!.get())
        }

        return DataFrame.Builder()
                .putNumeric(Stats.X, statX)
                .putNumeric(Stats.COUNT, statCount)
                .build()
    }

    override fun requires(): List<Aes<*>> {
        return listOf<Aes<*>>(Aes.X)
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
                Aes.X to Stats.X,
                Aes.Y to Stats.COUNT
        )

        private fun countByX(valuesX: List<Double?>, weight: List<Double?>): Map<Double, MutableDouble> {
            val result = LinkedHashMap<Double, MutableDouble>()
            for (i in valuesX.indices) {
                val x = valuesX[i]
                if (SeriesUtil.isFinite(x)) {
                    if (!result.containsKey(x!!)) {
                        result[x] = MutableDouble(0.0)
                    }
                    result[x]!!.getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}
