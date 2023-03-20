/*
 * Copyright (c) 2020. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.util.afterOrientation
import jetbrains.datalore.plot.builder.interact.GeomInteraction
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.interact.GeomTooltipSetup
import jetbrains.datalore.plot.builder.theme.Theme

object GeomInteractionUtil {
    internal fun configGeomTargets(
        layerConfig: LayerConfig,
        scaleMap: Map<Aes<*>, Scale>,
        multilayerWithTooltips: Boolean,
        isLiveMap: Boolean,
        theme: Theme
    ): GeomInteraction {
        return createGeomInteractionBuilder(layerConfig, scaleMap, multilayerWithTooltips, isLiveMap, theme).build()
    }

    internal fun createGeomInteractionBuilder(
        layerConfig: LayerConfig,
        scaleMap: Map<Aes<*>, Scale>,
        multilayerWithTooltips: Boolean,
        isLiveMap: Boolean,
        theme: Theme
    ): GeomInteractionBuilder {
        val tooltipSetup = createGeomTooltipSetup(
            geomKind = layerConfig.geomProto.geomKind,
            statKind = layerConfig.statKind,
            isCrosshairEnabled = isCrosshairEnabled(layerConfig),
            multilayerWithTooltips = multilayerWithTooltips
        )
        return createGeomInteractionBuilder(layerConfig, scaleMap, tooltipSetup, isLiveMap, theme)
    }

    private fun createGeomInteractionBuilder(
        layerConfig: LayerConfig,
        scaleMap: Map<Aes<*>, Scale>,
        tooltipSetup: GeomTooltipSetup,
        isLiveMap: Boolean,
        theme: Theme
    ): GeomInteractionBuilder {

        val axisWithoutTooltip = HashSet<Aes<*>>()
        if (isLiveMap || !theme.horizontalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.X)
        if (isLiveMap || !theme.verticalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.Y)

        // Also: don't show the axis tooltip if the axis tick labels are hidden.
        val axisWithNoLabels = HashSet<Aes<*>>()
        if (!theme.horizontalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.X)
        if (!theme.verticalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.Y)

        val yOrientation = layerConfig.isYOrientation
        val axisAesFromFunctionKind = tooltipSetup.axisAesFromFunctionKind
        val isAxisTooltipEnabled = tooltipSetup.axisTooltipEnabled

        val hiddenAesList = createHiddenAesList(
            layerConfig,
            axisAesFromFunctionKind
        ).afterOrientation(yOrientation) +
                axisWithoutTooltip

        val axisAes = createAxisAesList(
            isAxisTooltipEnabled,
            axisAesFromFunctionKind,
            layerConfig.geomProto.geomKind,
        ).afterOrientation(yOrientation) -
                hiddenAesList -
                axisWithNoLabels

        val outlierAesList = createOutlierAesList(
            layerConfig.geomProto.geomKind
        ).afterOrientation(yOrientation)

        val axisAesFromFunctionTypeAfterOrientation = axisAesFromFunctionKind.afterOrientation(yOrientation)
        val layerRendersAesAfterOrientation = layerConfig.renderedAes.afterOrientation(yOrientation)
        val tooltipAes = createTooltipAesList(
            layerConfig,
            scaleMap,
            layerRendersAesAfterOrientation,
            axisAesFromFunctionTypeAfterOrientation,
            hiddenAesList
        )

        val builder = GeomInteractionBuilder(
            locatorLookupSpace = tooltipSetup.locatorLookupSpace,
            locatorLookupStrategy = tooltipSetup.locatorLookupStrategy,
            tooltipAes = tooltipAes,
            tooltipAxisAes = axisAes,
            tooltipOutlierAes = outlierAesList
        )

        return builder
            .tooltipLinesSpec(layerConfig.tooltips)
            .tooltipConstants(createConstantAesList(layerConfig))
            .enableCrosshair(isCrosshairEnabled(layerConfig))
    }

    private fun createGeomTooltipSetup(
        geomKind: GeomKind,
        statKind: StatKind,
        isCrosshairEnabled: Boolean,
        multilayerWithTooltips: Boolean,
    ): GeomTooltipSetup {
        val tooltipSetup = createGeomTooltipSetup(
            geomKind,
            statKind,
            isCrosshairEnabled,
        ).let {
            var multilayerLookup: Boolean = false
            if (multilayerWithTooltips && !isCrosshairEnabled) {
                // Only these kinds of geoms should be switched to NEAREST XY strategy on a multilayer plot,
                // and tooltips should not be disabled in other layers.
                // Rect, histogram and other column alike geoms should not switch searching strategy, otherwise
                // tooltips behaviour becomes unexpected(histogram shows tooltip when cursor is close enough,
                // but not above a column).
                if (listOf(GeomKind.LINE, GeomKind.DENSITY, GeomKind.AREA, GeomKind.FREQPOLY).contains(geomKind)) {
                    multilayerLookup = true
                } else if (statKind === StatKind.SMOOTH) {
                    multilayerLookup = geomKind in listOf(GeomKind.POINT, GeomKind.CONTOUR)
                }
            }

            if (multilayerLookup) {
                it.toMultilayerLookupStrategy()
            } else {
                it
            }
        }

        return tooltipSetup
    }

    private fun createGeomTooltipSetup(
        geomKind: GeomKind,
        statKind: StatKind,
        isCrosshairEnabled: Boolean,
    ): GeomTooltipSetup {
        if (statKind === StatKind.SMOOTH) {
            when (geomKind) {
                GeomKind.POINT,
                GeomKind.CONTOUR -> return GeomTooltipSetup.univariateFunction(
                    GeomTargetLocator.LookupStrategy.NEAREST
                )

                else -> {}
            }
        }

        when (geomKind) {
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.HISTOGRAM,
            GeomKind.DOT_PLOT,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.BAR,
            GeomKind.ERROR_BAR,
            GeomKind.CROSS_BAR,
            GeomKind.POINT_RANGE,
            GeomKind.LINE_RANGE,
            GeomKind.SEGMENT,
            GeomKind.V_LINE -> return GeomTooltipSetup.univariateFunction(
                GeomTargetLocator.LookupStrategy.HOVER,
                axisTooltipVisibilityFromConfig = true
            )

            GeomKind.RIBBON -> return GeomTooltipSetup.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            GeomKind.SMOOTH -> return if (isCrosshairEnabled) {
                GeomTooltipSetup.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            } else {
                GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)
            }

            GeomKind.PIE,
            GeomKind.BOX_PLOT,
            GeomKind.Y_DOT_PLOT,
            GeomKind.BIN_2D,
            GeomKind.TILE -> return GeomTooltipSetup.bivariateFunction(
                GeomTooltipSetup.AREA_GEOM,
                axisTooltipVisibilityFromConfig = true
            )

            GeomKind.TEXT,
            GeomKind.LABEL,
            GeomKind.POINT,
            GeomKind.JITTER,
            GeomKind.Q_Q,
            GeomKind.Q_Q_2,
            GeomKind.CONTOUR,
            GeomKind.DENSITY2D,
            GeomKind.AREA_RIDGES,
            GeomKind.VIOLIN -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)

            GeomKind.Q_Q_LINE,
            GeomKind.Q_Q_2_LINE,
            GeomKind.PATH -> {
                return when (statKind) {
                    StatKind.CONTOUR, StatKind.CONTOURF, StatKind.DENSITY2D -> GeomTooltipSetup.bivariateFunction(
                        GeomTooltipSetup.NON_AREA_GEOM
                    )

                    else -> {
                        GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.AREA_GEOM)
                    }
                }
            }

            GeomKind.H_LINE,
            GeomKind.DENSITY2DF,
            GeomKind.CONTOURF,
            GeomKind.POLYGON,
            GeomKind.MAP,
            GeomKind.RECT -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.AREA_GEOM)

            GeomKind.LIVE_MAP -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)

            else -> return GeomTooltipSetup.none()
        }
    }

    private fun createHiddenAesList(layerConfig: LayerConfig, axisAes: List<Aes<*>>): List<Aes<*>> {
        return when (layerConfig.geomProto.geomKind) {
            GeomKind.DOT_PLOT -> listOf(Aes.BINWIDTH)
            GeomKind.Y_DOT_PLOT -> listOf(Aes.BINWIDTH)
            GeomKind.AREA -> listOf(Aes.QUANTILE)
            GeomKind.DENSITY -> listOf(Aes.QUANTILE)
            GeomKind.VIOLIN -> listOf(Aes.QUANTILE)
            GeomKind.AREA_RIDGES -> listOf(Aes.QUANTILE)
            GeomKind.BOX_PLOT -> listOf(Aes.Y)
            GeomKind.RECT -> listOf(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
            GeomKind.SEGMENT -> listOf(Aes.X, Aes.Y, Aes.XEND, Aes.YEND)
            GeomKind.TEXT, GeomKind.LABEL -> {
                // by default geom_text doesn't show tooltips,
                // but user can enable them via tooltips config in which case the axis tooltips should also be displayed
                if (layerConfig.tooltips.tooltipLinePatterns.isNullOrEmpty()) {
                    layerConfig.renderedAes
                } else {
                    layerConfig.renderedAes - axisAes
                }
            }

            GeomKind.PIE -> listOf(Aes.EXPLODE)
            else -> emptyList()
        }
    }

    private fun createAxisAesList(
        isAxisTooltipEnabled: Boolean,
        axisAesFromFunctionKind: List<Aes<*>>,
        geomKind: GeomKind,
    ): List<Aes<*>> {
        return when {
            !isAxisTooltipEnabled -> emptyList()
            geomKind == GeomKind.AREA_RIDGES ||
                    geomKind == GeomKind.SMOOTH -> listOf(Aes.X)

            else -> axisAesFromFunctionKind
        }
    }

    private fun createTooltipAesList(
        layerConfig: LayerConfig,
        scaleMap: Map<Aes<*>, Scale>,
        layerRendersAes: List<Aes<*>>,
        axisAes: List<Aes<*>>,
        hiddenAesList: List<Aes<*>>
    ): List<Aes<*>> {

        // remove axis mapping: if aes and axis are bound to the same data
        val aesListForTooltip = ArrayList(layerRendersAes - axisAes)
        for (aes in axisAes) {
            val axisVariable = layerConfig.getVariableForAes(aes)
            aesListForTooltip.removeAll { layerConfig.getVariableForAes(it) == axisVariable }
        }

        aesListForTooltip.retainAll { aes -> scaleMap.containsKey(aes) && layerConfig.getVariableForAes(aes) != null }

        // remove auto generated mappings
        val autoGenerated = listOf<String>()
        aesListForTooltip.removeAll { scaleMap.safeGet(it)?.name in autoGenerated }

        // retain continuous mappings or discrete with checking of number of factors
        aesListForTooltip.retainAll { isTooltipForAesEnabled(it, scaleMap) }

        // remove duplicated mappings
        val mappingsToShow = HashMap<DataFrame.Variable, Aes<*>>()
        for (aes in aesListForTooltip) {
            if (aes in hiddenAesList) continue

            val variable = layerConfig.getVariableForAes(aes)!!
            val mappingToShow = mappingsToShow[variable]
            when {
                mappingToShow == null -> {
                    mappingsToShow[variable] = aes
                }

                !isVariableContinuous(scaleMap, mappingToShow) && isVariableContinuous(scaleMap, aes) -> {
                    // If the same variable is mapped twice as continuous and discrete - use the continuous value
                    // (ex TooltipSpecFactory::removeDiscreteDuplicatedMappings method)
                    mappingsToShow[variable] = aes
                }

                scaleMap.getValue(aes).name != variable.label -> {
                    // Use variable which is shown by the scale with its name
                    mappingsToShow[variable] = aes
                }
            }
        }
        return mappingsToShow.values.toList()
    }

    private fun createOutlierAesList(geomKind: GeomKind): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.CROSS_BAR,
            GeomKind.ERROR_BAR,
            GeomKind.LINE_RANGE,
            GeomKind.POINT_RANGE,
            GeomKind.RIBBON -> listOf(Aes.YMAX, Aes.YMIN)

            GeomKind.BOX_PLOT -> listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN)
            GeomKind.SMOOTH -> listOf(Aes.YMAX, Aes.YMIN, Aes.Y)
            else -> emptyList()
        }
    }

    private fun createConstantAesList(layerConfig: LayerConfig): Map<Aes<*>, Any> {
        return when (layerConfig.geomProto.geomKind) {
            GeomKind.H_LINE,
            GeomKind.V_LINE -> layerConfig.constantsMap.filter { (aes, _) -> Aes.isPositional(aes) }

            else -> emptyMap()
        }
    }

    private fun isCrosshairEnabled(layerConfig: LayerConfig): Boolean {
        // Crosshair is enabled if the general tooltip is moved to the specified position
        if (layerConfig.tooltips.tooltipProperties.anchor == null) {
            return false
        }

        return when (layerConfig.geomProto.geomKind) {
            GeomKind.POINT,
            GeomKind.JITTER,
            GeomKind.Q_Q,
            GeomKind.Q_Q_2,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.TILE,
            GeomKind.CONTOUR,
            GeomKind.CONTOURF,
            GeomKind.BIN_2D,
            GeomKind.DENSITY,
            GeomKind.DENSITY2D,
            GeomKind.DENSITY2DF,
            GeomKind.FREQPOLY,
            GeomKind.PATH,
            GeomKind.SEGMENT,
            GeomKind.RIBBON,
            GeomKind.SMOOTH -> true

            else -> false
        }
    }

    // the number of factors starting from which tooltips can be displayed
    private const val MIN_FACTORS_TO_SHOW_TOOLTIPS = 5
    private fun isTooltipForAesEnabled(aes: Aes<*>, scaleMap: Map<Aes<*>, Scale>): Boolean {
        if (isVariableContinuous(scaleMap, aes)) {
            return true
        }
        val factors = scaleMap.safeGet(aes)?.getScaleBreaks()?.domainValues ?: return false
        return factors.size >= MIN_FACTORS_TO_SHOW_TOOLTIPS
    }
}

private fun <T> Map<Aes<*>, Scale>.safeGet(aes: Aes<T>) = if (containsKey(aes)) get(aes) else null

private fun isVariableContinuous(scaleMap: Map<Aes<*>, Scale>, aes: Aes<*>) =
    scaleMap.safeGet(aes)?.isContinuousDomain ?: false

