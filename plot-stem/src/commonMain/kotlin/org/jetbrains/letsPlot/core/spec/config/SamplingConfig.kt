/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.builder.sampling.Sampling

internal object SamplingConfig {
    fun create(sampling: Any, geomKind: GeomKind): List<Sampling> {
        // sampling is specified per geom layer:
        // xxx_geom(..., sampling=sampling_random(100,seed=3)...)
        // or
        // xxx_geom(..., sampling=sampling_random(100,seed=3) + sampling_pick(10)...)
        if (sampling is MutableMap<*, *> && ConfigUtil.isFeatureList(sampling)) {
            @Suppress("UNCHECKED_CAST")
            val samplingList = ConfigUtil.featuresInFeatureList(sampling as MutableMap<String, Any>)
            val result = ArrayList<Sampling>()
            for (o in samplingList) {
                result.add(createOne(o, geomKind))
            }
            return result
        }

        return listOf(createOne(sampling, geomKind))
    }

    private fun createOne(sampling: Any, geomKind: GeomKind): Sampling {
        if (sampling is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return SamplingProto.createSampling(
                ConfigUtil.featureName(sampling), geomKind, sampling as Map<String, Any>
            )
        } else if (sampling is String) {
            return SamplingProto.createSampling(sampling, geomKind, emptyMap())
        }
        throw IllegalArgumentException("Incorrect sampling specification type: '${sampling::class.simpleName}'")
    }
}
