/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.Option.Plot.DATA
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.MAPPING
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector
import jetbrains.datalore.plot.server.config.transform.ReplaceDataVectorsInAesMappingChangeUtil.AesMappingPreprocessor

internal class ReplaceDataVectorsInAesMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val preprocessors = ArrayList<AesMappingPreprocessor>()
        preprocessors.add(AesMappingPreprocessor(spec, DATA, MAPPING))

        val layerSpecs = ctx.getSpecsAbsolute(LAYERS)
        for (layerSpec in layerSpecs) {
            preprocessors.add(AesMappingPreprocessor(layerSpec as MutableMap<String, Any>, DATA, MAPPING))
        }

        val varNameCollector = HashSet<String>()
        for (preprocessor in preprocessors) {
            varNameCollector.addAll(preprocessor.dataColNames)
        }

        for (preprocessor in preprocessors) {
            preprocessor.replaceDataVectorsInAesMapping(varNameCollector)
        }
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.root()
        }
    }
}
