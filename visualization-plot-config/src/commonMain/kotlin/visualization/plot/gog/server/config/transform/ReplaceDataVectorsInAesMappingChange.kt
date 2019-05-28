package jetbrains.datalore.visualization.plot.gog.server.config.transform

import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.DATA
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.MAPPING
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChangeContext
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecSelector
import jetbrains.datalore.visualization.plot.gog.server.config.transform.ReplaceDataVectorsInAesMappingChangeUtil.AesMappingPreprocessor

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
