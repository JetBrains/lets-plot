package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.base.Strings.isNullOrEmpty
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.builder.ColorBarOptions
import jetbrains.datalore.visualization.plot.builder.GuideOptions
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

internal object PlotGuidesAssemblerUtil {
    fun mappedRenderedAesToCreateGuides(layerTiles: StitchedPlotLayers, guideOptionsMap: Map<Aes<*>, GuideOptions>): List<Aes<*>> {
        if (layerTiles.isLegendDisabled) {
            // ToDo: add support for
            // show_legend = True     : show all aesthetics in legend
            // show_legend = [.. list of aesthetics to show ..]     : show selected aesthetics in legend
            // see: https://ggplot2.tidyverse.org/reference/geom_point.html
            return emptyList()
        }

        val result = ArrayList<Aes<*>>()
        for (aes in layerTiles.renderedAes()) {
            if (Aes.noGuideNeeded(aes)) {
                continue
            }
            if (layerTiles.hasConstant(aes)) {
                // constants mask aes mappings
                continue
            }
            if (!layerTiles.hasBinding(aes)) {
                continue
            }
            if (guideOptionsMap.containsKey(aes)) {
                if (guideOptionsMap[aes] === GuideOptions.NONE) {
                    continue
                }
            }

            val binding = layerTiles.getBinding(aes)
            val scale = binding.scale
            val scaleName = scale!!.name
            if (isNullOrEmpty(scaleName)) {
                continue
            }

            result.add(aes)
        }

        return result
    }

    fun guideDataRangeByAes(stitchedLayers: StitchedPlotLayers, guideOptionsMap: Map<Aes<*>, GuideOptions>): Map<Aes<*>, ClosedRange<Double>> {
        val m = HashMap<Aes<*>, ClosedRange<Double>>()
        val aesSet = mappedRenderedAesToCreateGuides(stitchedLayers, guideOptionsMap)
        for (aes in aesSet) {
            val binding = stitchedLayers.getBinding(aes)
            if (stitchedLayers.isNumericData(binding.variable)) {
                val dataRange = stitchedLayers.getDataRange(binding.variable)!!
                m[aes] = dataRange
            }
        }

        return m
    }

    fun createColorBarAssembler(scaleName: String,
                                aes: Aes<*>, dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>,
                                scale: Scale<Color>,
                                options: ColorBarOptions?,
                                theme: LegendTheme): ColorBarAssembler {

        val domain = dataRangeByAes[aes]
        checkState(domain != null, "Data range is not defined for aes $aes")
        val result = ColorBarAssembler(scaleName, domain!!, scale, theme)
        result.setOptions(options)
        return result
    }

    fun fitsColorBar(binding: VarBinding): Boolean {
        return binding.aes.isColor && binding.scale!!.isContinuous
    }

    fun checkFitsColorBar(binding: VarBinding) {
        val aes = binding.aes
        checkState(aes.isColor, "Colorbar is not applicable to $aes aesthetic")
        checkState(binding.scale!!.isContinuous, "Colorbar is only applicable when both domain and color palette are continuous")
    }
}
