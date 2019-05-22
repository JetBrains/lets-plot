package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.data.Sampling
import jetbrains.datalore.visualization.plot.builder.assemble.geom.DefaultSampling.SAFETY_SAMPLING
import jetbrains.datalore.visualization.plot.builder.data.GroupAwareSampling
import jetbrains.datalore.visualization.plot.builder.data.PointSampling
import jetbrains.datalore.visualization.plot.builder.data.sampling.Samplings

internal object PlotSampling {

    fun apply(data: DataFrame, samplings: List<Sampling>,
              groupMapper: (Int) -> Int,
              samplingExpressionConsumer: Consumer<String>): DataFrame {

        @Suppress("NAME_SHADOWING")
        var data = data

        val applied = ArrayList<Sampling>()
        for (sampling in samplings) {
            //DataFrame data1 = applyOne(sampling, data, layerConfig);
            val data1 = applyOne(sampling, data, groupMapper)
            if (data1 != data) {
                applied.add(sampling)
            }
            data = data1
        }

        val isNone = samplings.size == 1 && samplings[0] === Samplings.NONE
        if (!isNone && SAFETY_SAMPLING.isApplicable(data)) {
            // apply SAFETY_SAMPLING unless used has already specified some kind of point sampling
            val havePointSampling = samplings.any { sampling -> sampling is PointSampling }
            if (!havePointSampling) {
                data = SAFETY_SAMPLING.apply(data)
                applied.add(SAFETY_SAMPLING)
            }
        }

        if (!applied.isEmpty()) {
            val expressionText = applied.map { it.expressionText }.joinToString("+")
            samplingExpressionConsumer(expressionText)
        }

        return data
    }

    private fun applyOne(sampling: Sampling, data: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        if (sampling is PointSampling) {
            if (sampling.isApplicable(data)) {
                return sampling.apply(data)
            }
        } else {
            val gs = sampling as GroupAwareSampling
            if (gs.isApplicable(data, groupMapper)) {
                return gs.apply(data, groupMapper)
            }
        }

        return data
    }
}
