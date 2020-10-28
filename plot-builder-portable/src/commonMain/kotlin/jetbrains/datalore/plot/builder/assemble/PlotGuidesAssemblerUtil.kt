/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.base.Strings.isNullOrEmpty
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange
import kotlin.math.max

internal object PlotGuidesAssemblerUtil {
    fun mappedRenderedAesToCreateGuides(
        layerTiles: StitchedPlotLayers,
        guideOptionsMap: Map<Aes<*>, GuideOptions>
    ): List<Aes<*>> {
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

            val scale = layerTiles.getScale(aes)
            val scaleName = scale.name
            if (isNullOrEmpty(scaleName)) {
                continue
            }

            result.add(aes)
        }

        return result
    }

    fun guideDataRangeByAes(
        stitchedLayers: StitchedPlotLayers,
        guideOptionsMap: Map<Aes<*>, GuideOptions>
    ): Map<Aes<*>, ClosedRange<Double>> {
        val guideDomainByAes = HashMap<Aes<*>, ClosedRange<Double>>()
        val aesSet =
            mappedRenderedAesToCreateGuides(
                stitchedLayers,
                guideOptionsMap
            )
        for (aes in aesSet) {
            val binding = stitchedLayers.getBinding(aes)
            if (stitchedLayers.isNumericData(binding.variable)) {
                val dataRange = stitchedLayers.getDataRange(binding.variable)
                if (dataRange != null) {
                    val scale = stitchedLayers.getScale(aes)

                    val guideDomain =
                        if (scale.isContinuousDomain && scale.hasDomainLimits()) {
                            val limits = scale.domainLimits!!
                            val lowerEnd = if (limits.lowerEnd.isFinite()) limits.lowerEnd else dataRange.lowerEnd
                            val upperEnd = if (limits.upperEnd.isFinite()) limits.upperEnd else dataRange.upperEnd
                            ClosedRange<Double>(lowerEnd, max(lowerEnd, upperEnd))
                        } else {
                            dataRange
                        }


                    guideDomainByAes[aes] = guideDomain
                }
            }
        }

        return guideDomainByAes
    }

    fun createColorBarAssembler(
        scaleName: String,
        aes: Aes<*>, dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>,
        scale: Scale<Color>,
        options: ColorBarOptions?,
        theme: LegendTheme
    ): ColorBarAssembler {

        val domain = dataRangeByAes[aes]
        val result = ColorBarAssembler(
            scaleName,
            ensureApplicableRange(domain),
            scale,
            theme
        )
        result.setOptions(options)
        return result
    }

    fun fitsColorBar(aes: Aes<*>, scale: Scale<*>): Boolean {
        return aes.isColor && scale.isContinuous
    }

    fun checkFitsColorBar(aes: Aes<*>, scale: Scale<*>) {
        checkState(aes.isColor, "Color-bar is not applicable to $aes aesthetic")
        checkState(
            scale.isContinuous,
            "Color-bar is only applicable when both domain and color palette are continuous"
        )
    }
}
