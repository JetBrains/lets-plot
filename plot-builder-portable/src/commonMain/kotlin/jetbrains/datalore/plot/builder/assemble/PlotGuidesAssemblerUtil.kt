/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange

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

            result.add(aes)
        }

        return result
    }

    fun guideTransformedDomainByAes(
        stitchedLayers: StitchedPlotLayers,
        guideOptionsMap: Map<Aes<*>, GuideOptions>
    ): Map<Aes<*>, ClosedRange<Double>> {
        val transformedDomainByAes = HashMap<Aes<*>, ClosedRange<Double>>()
        val aesSet = mappedRenderedAesToCreateGuides(
            stitchedLayers,
            guideOptionsMap
        )

        for (aes in aesSet) {
            // Should be only 'tarnsform' variables in bindings at this point.
            val transformVariable = stitchedLayers.getBinding(aes).variable
            check(transformVariable.isTransform)

            val transformedDataRange = stitchedLayers.getDataRange(transformVariable)
            if (transformedDataRange != null) {
                val scale = stitchedLayers.getScale(aes)

                val transformedDomain =
                    if (scale.isContinuousDomain && scale.hasDomainLimits()) {
                        val (scaleLower, scaleUpper) = ScaleUtil.transformedDefinedLimits(scale)
                        val lowerEnd = if (scaleLower.isFinite()) scaleLower else transformedDataRange.lowerEnd
                        val upperEnd = if (scaleUpper.isFinite()) scaleUpper else transformedDataRange.upperEnd
                        ClosedRange<Double>(lowerEnd, upperEnd)
                    } else {
                        transformedDataRange
                    }


                transformedDomainByAes[aes] = transformedDomain
            }
        }

        return transformedDomainByAes
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
        check(aes.isColor) { "Color-bar is not applicable to $aes aesthetic" }
        check(scale.isContinuous) { "Color-bar is only applicable when both domain and color palette are continuous" }
    }
}
