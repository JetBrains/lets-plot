package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.builder.sampling.Sampling
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings
import jetbrains.datalore.visualization.plot.gog.config.Option.Sampling.NONE

internal object SamplingConfig {
    fun create(sampling: Any): List<Sampling> {
        // sampling is specified per geom layer:
        // xxx_geom(..., sampling=sampling_random(100,seed=3)...)
        // or
        // xxx_geom(..., sampling=sampling_random(100,seed=3) + sampling_pick(10)...)
        if (sampling is MutableMap<*, *> && ConfigUtil.isFeatureList(sampling)) {
            val samplingList = ConfigUtil.featuresInFeatureList(sampling)
            val result = ArrayList<Sampling>()
            for (o in samplingList) {
                result.add(createOne(o))
            }
            return result
        }

        return listOf(createOne(sampling))
    }

    private fun createOne(sampling: Any): Sampling {
        if (sampling is Map<*, *>) {
            return SamplingProto.createSampling(ConfigUtil.featureName(sampling), sampling)
        } else if (NONE == sampling) {
            return Samplings.NONE
        }
        throw IllegalArgumentException("Incorrect sampling specification")
    }
}
