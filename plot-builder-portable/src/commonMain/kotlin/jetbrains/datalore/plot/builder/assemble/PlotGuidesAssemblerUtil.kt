/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.scale.transform.Transforms.ensureApplicableDomain
import jetbrains.datalore.plot.builder.theme.LegendTheme

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
    ): Map<Aes<*>, DoubleSpan> {
        val transformedDomainByAes = HashMap<Aes<*>, DoubleSpan>()
        val aesSet = mappedRenderedAesToCreateGuides(
            stitchedLayers,
            guideOptionsMap
        )

        for (aes in aesSet) {
            // Should be only 'tarnsform' variables in bindings at this point.
            val transformVariable = stitchedLayers.getBinding(aes).variable
            check(transformVariable.isTransform)

            val transformedDataRange = stitchedLayers.getDataRange(transformVariable)
            val scale = stitchedLayers.getScale(aes)
            if (scale.isContinuousDomain) {
                transformedDomainByAes[aes] = refineTransformedDataRangeForContinuousDomain(
                    transformedDataRange,
                    scale.transform as ContinuousTransform
                )
            } else if (transformedDataRange != null) {
                transformedDomainByAes[aes] = transformedDataRange
            }
        }

        return transformedDomainByAes
    }

    private fun refineTransformedDataRangeForContinuousDomain(
        transformedDataRange: DoubleSpan?,
        transform: ContinuousTransform
    ): DoubleSpan {
        val (dataLower, dataUpper) = when (transformedDataRange) {
            null -> Pair(Double.NaN, Double.NaN)
            else -> Pair(transformedDataRange.lowerEnd, transformedDataRange.upperEnd)
        }
        val (scaleLower, scaleUpper) = ScaleUtil.transformedDefinedLimits(transform)

        val lowerEnd = if (scaleLower.isFinite()) scaleLower else dataLower
        val upperEnd = if (scaleUpper.isFinite()) scaleUpper else dataUpper

        val newRange = when {
            lowerEnd.isFinite() && upperEnd.isFinite() -> DoubleSpan(lowerEnd, upperEnd)
            lowerEnd.isFinite() -> DoubleSpan(lowerEnd, lowerEnd)
            upperEnd.isFinite() -> DoubleSpan(upperEnd, upperEnd)
            else -> null
        }

        return ensureApplicableDomain(newRange, transform)
    }

    fun createColorBarAssembler(
        scaleName: String,
        transformedDomain: DoubleSpan,
        scale: Scale<Color>,
        scaleMapper: ScaleMapper<Color>,
        options: ColorBarOptions?,
        theme: LegendTheme
    ): ColorBarAssembler {

        val result = ColorBarAssembler(
            scaleName,
            transformedDomain,
            scale,
            scaleMapper,
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
