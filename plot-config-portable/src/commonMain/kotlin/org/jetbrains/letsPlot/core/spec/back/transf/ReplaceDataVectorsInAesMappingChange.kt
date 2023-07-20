/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf

import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.transf.SpecChange
import org.jetbrains.letsPlot.core.spec.transf.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.transf.SpecSelector
import org.jetbrains.letsPlot.core.spec.back.transf.ReplaceDataVectorsInAesMappingChangeUtil.AesMappingPreprocessor

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
