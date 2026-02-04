/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipSpecification
import org.jetbrains.letsPlot.core.plot.base.util.afterOrientation


object GeomInteractionUtil {
    fun createGeomInteractionBuilder(
        bindings: Map<Aes<*>, DataFrame.Variable>,
        scaleMap: Map<Aes<*>, Scale>,
        multilayerWithTooltips: Boolean,
        isLiveMap: Boolean,
        isPolarCoordSystem: Boolean,
        theme: Theme,
        geomKind: GeomKind,
        statKind: StatKind,
        tooltipSpecification1: TooltipSpecification,
        isYOrientation: Boolean,
        constantsMap: Map<Aes<*>, Any>,
        renderedAes: List<Aes<*>>,
        getOriginalVariableName: (Aes<*>) -> String?,
    ): GeomInteractionBuilder {
        val tooltipSetup = createGeomTooltipSetup(
            geomKind = geomKind,
            statKind = statKind,
            isCrosshairEnabled = isCrosshairEnabled(geomKind, tooltipSpecification1),
            isPolarCoordSystem = isPolarCoordSystem,
            multilayerWithTooltips = multilayerWithTooltips
        )

        val axisWithoutTooltip = HashSet<Aes<*>>()
        if (isLiveMap || !theme.horizontalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.X)
        if (isLiveMap || !theme.verticalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.Y)

        // Also: don't show the axis tooltip if the axis tick labels are hidden.
        val axisWithNoLabels = HashSet<Aes<*>>()
        if (!theme.horizontalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.X)
        if (!theme.verticalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.Y)
        val axisAesFromFunctionKind = tooltipSetup.axisAesFromFunctionKind
        val isAxisTooltipEnabled = tooltipSetup.axisTooltipEnabled
        val hiddenAesList = createHiddenAesList(
            axisAesFromFunctionKind,
            geomKind,
            tooltipSpecification1,
            renderedAes
        )
            .afterOrientation(isYOrientation) + axisWithoutTooltip
        val axisAes = createAxisAesList(isAxisTooltipEnabled, axisAesFromFunctionKind, geomKind,)
            .afterOrientation(isYOrientation) - hiddenAesList - axisWithNoLabels

        val tooltipAes: List<Aes<*>>
        val sideTooltipAes: List<Aes<*>>
        val tooltipSpecification: TooltipSpecification

        if (theme.tooltips().show()) {
            val axisAesFromFunctionTypeAfterOrientation = axisAesFromFunctionKind.afterOrientation(isYOrientation)
            val layerRendersAesAfterOrientation = renderedAes.afterOrientation(isYOrientation)
            tooltipAes = createTooltipAesList(
                bindings,
                scaleMap,
                layerRendersAesAfterOrientation,
                axisAesFromFunctionTypeAfterOrientation,
                hiddenAesList,
                getOriginalVariableName
            )
            sideTooltipAes = createSideTooltipAesList(geomKind, isYOrientation)
            tooltipSpecification = tooltipSpecification1
        } else {
            tooltipAes = emptyList()
            sideTooltipAes = emptyList()
            // Need to keep specified formats to use for non-hidden tooltips:
            tooltipSpecification = TooltipSpecification(
                valueSources = tooltipSpecification1.valueSources,
                tooltipLinePatterns = null,
                tooltipProperties = TooltipSpecification.TooltipProperties.NONE,
                tooltipTitle = null,
                disableSplitting = false
            )
        }

        val builder = GeomInteractionBuilder(
            locatorLookupSpace = tooltipSetup.locatorLookupSpace,
            locatorLookupStrategy = tooltipSetup.locatorLookupStrategy,
            tooltipAes = tooltipAes,
            tooltipAxisAes = axisAes,
            sideTooltipAes = sideTooltipAes
        )
        return builder
            .tooltipLinesSpec(tooltipSpecification)
            .tooltipConstants(createConstantAesList(geomKind, constantsMap))
            .enableCrosshair(isCrosshairEnabled(geomKind, tooltipSpecification1))
    }

    private fun createGeomTooltipSetup(
        geomKind: GeomKind,
        statKind: StatKind,
        isCrosshairEnabled: Boolean,
        isPolarCoordSystem: Boolean,
        multilayerWithTooltips: Boolean
    ): GeomTooltipSetup {
        val tooltipSetup = createGeomTooltipSetup(geomKind, statKind, isCrosshairEnabled, isPolarCoordSystem).let {
            var multilayerLookup = false
            if (multilayerWithTooltips && !isCrosshairEnabled) {
                // Only these kinds of geoms should be switched to NEAREST XY strategy on a multilayer plot,
                // and tooltips should not be disabled in other layers.
                // Rect, histogram and other column alike geoms should not switch searching strategy, otherwise
                // tooltips behaviour becomes unexpected(histogram shows tooltip when cursor is close enough,
                // but not above a column).
                if (geomKind in setOf(GeomKind.LINE, GeomKind.DENSITY, GeomKind.AREA, GeomKind.FREQPOLY, GeomKind.RIBBON)) {
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
        isPolarCoordSystem: Boolean
    ): GeomTooltipSetup {
        if (isPolarCoordSystem) {
            // Always show axis tooltips for polar coordinate system as all geoms are area-like
            return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.AREA_GEOM, axisTooltipVisibilityFromConfig = true)
        }

        if (statKind === StatKind.SMOOTH) {
            when (geomKind) {
                GeomKind.POINT,
                GeomKind.CONTOUR -> return GeomTooltipSetup.xUnivariateFunction(
                    GeomTargetLocator.LookupStrategy.NEAREST
                )

                else -> {}
            }
        }

        when (geomKind) {
            GeomKind.RIBBON -> return GeomTooltipSetup.xUnivariateFunction(
                GeomTargetLocator.LookupStrategy.NEAREST,
                axisTooltipVisibilityFromConfig = true
            )
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.HISTOGRAM,
            GeomKind.DOT_PLOT,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.BAR,
            GeomKind.SEGMENT,
            GeomKind.STEP,
            GeomKind.POINT_RANGE,
            GeomKind.LINE_RANGE,
            GeomKind.ERROR_BAR -> return GeomTooltipSetup.xUnivariateFunction(
                GeomTargetLocator.LookupStrategy.HOVER,
                axisTooltipVisibilityFromConfig = true
            )

            GeomKind.SMOOTH -> return if (isCrosshairEnabled) {
                GeomTooltipSetup.xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            } else {
                GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)
            }

            GeomKind.PIE,
            GeomKind.BOX_PLOT,
            GeomKind.CROSS_BAR,
            GeomKind.Y_DOT_PLOT,
            GeomKind.HEX,
            GeomKind.BIN_2D,
            GeomKind.TILE -> return GeomTooltipSetup.bivariateFunction(
                GeomTooltipSetup.AREA_GEOM,
                axisTooltipVisibilityFromConfig = true
            )

            GeomKind.TEXT,
            GeomKind.LABEL,
            GeomKind.TEXT_REPEL,
            GeomKind.LABEL_REPEL,
            GeomKind.POINT,
            GeomKind.JITTER,
            GeomKind.Q_Q,
            GeomKind.Q_Q_2,
            GeomKind.CONTOUR,
            GeomKind.DENSITY2D,
            GeomKind.POINT_DENSITY,
            GeomKind.AREA_RIDGES,
            GeomKind.VIOLIN,
            GeomKind.SINA,
            GeomKind.LOLLIPOP,
            GeomKind.SPOKE,
            GeomKind.CURVE -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)

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
            GeomKind.V_LINE,
            GeomKind.BAND,
            GeomKind.DENSITY2DF,
            GeomKind.CONTOURF,
            GeomKind.POLYGON,
            GeomKind.MAP,
            GeomKind.RECT -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.AREA_GEOM)

            GeomKind.LIVE_MAP -> return GeomTooltipSetup.bivariateFunction(GeomTooltipSetup.NON_AREA_GEOM)

            else -> return GeomTooltipSetup.none()
        }
    }

    private fun createHiddenAesList(
        axisAes: List<Aes<*>>,
        geomKind: GeomKind,
        tooltipSpecification: TooltipSpecification,
        renderedAes: List<Aes<*>>
    ): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.DOT_PLOT -> listOf(Aes.BINWIDTH)
            GeomKind.Y_DOT_PLOT -> listOf(Aes.BINWIDTH)
            GeomKind.HEX -> listOf(Aes.WIDTH, Aes.HEIGHT)
            GeomKind.AREA -> listOf(Aes.QUANTILE)
            GeomKind.DENSITY -> listOf(Aes.QUANTILE)
            GeomKind.VIOLIN -> listOf(Aes.QUANTILE)
            GeomKind.SINA -> listOf(Aes.VIOLINWIDTH, Aes.QUANTILE)
            GeomKind.AREA_RIDGES -> listOf(Aes.QUANTILE)
            GeomKind.CROSS_BAR -> listOf(Aes.Y)
            GeomKind.BOX_PLOT -> listOf(Aes.Y)
            GeomKind.RECT -> listOf(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
            GeomKind.SEGMENT, GeomKind.CURVE -> listOf(Aes.X, Aes.Y, Aes.XEND, Aes.YEND)
            GeomKind.SPOKE -> listOf(Aes.X, Aes.Y, Aes.ANGLE, Aes.RADIUS)

            GeomKind.Q_Q, GeomKind.Q_Q_LINE -> listOf(Aes.SAMPLE)
            GeomKind.TEXT, GeomKind.LABEL, GeomKind.TEXT_REPEL, GeomKind.LABEL_REPEL -> {
                // by default geom_text doesn't show tooltips,
                // but user can enable them via tooltips config in which case the axis tooltips should also be displayed
                if (tooltipSpecification.tooltipLinePatterns.isNullOrEmpty()) {
                    renderedAes
                } else {
                    renderedAes - axisAes
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
        bindings: Map<Aes<*>, DataFrame.Variable>,
        scaleMap: Map<Aes<*>, Scale>,
        layerRendersAes: List<Aes<*>>,
        axisAes: List<Aes<*>>,
        hiddenAesList: List<Aes<*>>,
        getOriginalVariableName: (Aes<*>) -> String?
    ): List<Aes<*>> {

        // remove axis mapping: if aes and axis are bound to the same data
        val aesListForTooltip = ArrayList(layerRendersAes - axisAes)
        for (aes in axisAes) {
            val axisVariable = bindings[aes]
            aesListForTooltip.removeAll { bindings[it] == axisVariable }
        }

        aesListForTooltip.retainAll { aes -> scaleMap.containsKey(aes) && bindings[aes] != null }

        // remove auto generated mappings
        val autoGenerated = listOf<String>()
        aesListForTooltip.removeAll { scaleMap[it]?.name in autoGenerated }

        // retain continuous mappings or discrete with checking of number of factors
        aesListForTooltip.retainAll { isTooltipForAesEnabled(it, scaleMap) }

        // remove hidden aes
        aesListForTooltip.removeAll { it in hiddenAesList }

        // remove duplicated mappings
        val mappingsToShow = LinkedHashMap<DataFrame.Variable, Aes<*>>()
        aesListForTooltip
            .forEach { aes ->
                val variable = bindings[aes]!!
                val originalVariable = getOriginalVariableName(aes)
                val mappingToShow = mappingsToShow[variable]
                when {
                    scaleMap.getValue(aes).name != originalVariable -> {
                        // Use variable which is shown by the scale with its name
                        mappingsToShow[variable] = aes
                    }

                    // do nothing - mapping for the same original variable is already added
                    mappingsToShow.any { (_, aes) -> getOriginalVariableName(aes) == originalVariable } -> { }

                    mappingToShow == null -> mappingsToShow[variable] = aes

                    !isVariableContinuous(scaleMap, mappingToShow) && isVariableContinuous(scaleMap, aes) -> {
                        // If the same variable is mapped twice as continuous and discrete - use the continuous value
                        // (ex TooltipSpecFactory::removeDiscreteDuplicatedMappings method)
                        mappingsToShow[variable] = aes
                    }

                }
            }
        return mappingsToShow.values.toList()
    }

    private fun createSideTooltipAesList(geomKind: GeomKind, yOrientation: Boolean): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.CROSS_BAR,
            GeomKind.SMOOTH -> when (yOrientation) {
                true -> listOf(Aes.XMAX, Aes.X, Aes.XMIN)
                false -> listOf(Aes.YMAX, Aes.Y, Aes.YMIN)
            }
            GeomKind.POINT_RANGE,
            GeomKind.LINE_RANGE,
            GeomKind.ERROR_BAR,
            GeomKind.RIBBON,
            GeomKind.BAND -> listOf(Aes.YMAX, Aes.YMIN, Aes.XMAX, Aes.XMIN)
            GeomKind.BOX_PLOT -> when (yOrientation) {
                true -> listOf(Aes.XMAX, Aes.XUPPER, Aes.XMIDDLE, Aes.XLOWER, Aes.XMIN)
                false -> listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN)
            }
            else -> emptyList()
        }
    }

    private fun createConstantAesList(geomKind: GeomKind, constants: Map<Aes<*>, Any>): Map<Aes<*>, Any> {
        return when (geomKind) {
            GeomKind.H_LINE,
            GeomKind.V_LINE -> constants.filter { (aes, _) -> Aes.isPositional(aes) }

            else -> emptyMap()
        }
    }

    private fun isCrosshairEnabled(geomKind: GeomKind, tooltipSpecification: TooltipSpecification): Boolean {
        // Crosshair is enabled if the general tooltip is moved to the specified position
        if (tooltipSpecification.tooltipProperties.anchor == null) {
            return false
        }

        return when (geomKind) {
            GeomKind.POINT,
            GeomKind.JITTER,
            GeomKind.SINA,
            GeomKind.Q_Q,
            GeomKind.Q_Q_2,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.TILE,
            GeomKind.CONTOUR,
            GeomKind.CONTOURF,
            GeomKind.BIN_2D,
            GeomKind.HEX,
            GeomKind.DENSITY,
            GeomKind.DENSITY2D,
            GeomKind.DENSITY2DF,
            GeomKind.POINT_DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.PATH,
            GeomKind.SEGMENT,
            GeomKind.CURVE,
            GeomKind.SPOKE,
            GeomKind.RIBBON,
            GeomKind.SMOOTH,
            GeomKind.STEP -> true

            else -> false
        }
    }

    // Add a discrete variable to the tooltip only if the number of factor levels > 4
    private const val MIN_FACTORS_TO_SHOW_TOOLTIPS = 5
    private fun isTooltipForAesEnabled(aes: Aes<*>, scaleMap: Map<Aes<*>, Scale>): Boolean {
        if (isVariableContinuous(scaleMap, aes)) {
            return true
        }
        val factors = scaleMap[aes]?.getScaleBreaks()?.domainValues ?: return false
        return factors.size >= MIN_FACTORS_TO_SHOW_TOOLTIPS
    }

    private fun isVariableContinuous(scaleMap: Map<Aes<*>, Scale>, aes: Aes<*>) =
        scaleMap[aes]?.isContinuousDomain ?: false
}

