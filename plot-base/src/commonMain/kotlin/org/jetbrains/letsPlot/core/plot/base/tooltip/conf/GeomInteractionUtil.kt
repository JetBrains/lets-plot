/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.*
import org.jetbrains.letsPlot.core.plot.base.util.afterOrientation


object GeomInteractionUtil {
    fun createGeomInteraction(
        bindings: Map<Aes<*>, DataFrame.Variable>,
        scaleMap: Map<Aes<*>, Scale>,
        isLiveMap: Boolean,
        isPolarCoordSystem: Boolean,
        theme: Theme,
        geomKind: GeomKind,
        statKind: StatKind,
        contentSpecification: LinesContentSpecification,
        anchor: TooltipAnchor?,
        minWidth: Double?,
        disableSplitting: Boolean,
        tooltipGroup: String?,
        isCrosshairEnabled: Boolean,
        isYOrientation: Boolean,
        constantsMap: Map<Aes<*>, Any>,
        renderedAes: List<Aes<*>>,
        getOriginalVariableName: (Aes<*>) -> String?,
    ): GeomInteraction {
        val resolvedTooltipBehavior = TooltipBehaviorFactory.create(
            geomKind = geomKind,
            statKind = statKind,
            contentSpecification = contentSpecification,
            isPolarCoordSystem = isPolarCoordSystem,
            anchor = anchor,
            minWidth = minWidth,
            disableSplitting = disableSplitting,
            tooltipGroup = tooltipGroup,
            isCrosshairEnabled = isCrosshairEnabled
        )

        val axisWithoutTooltip = HashSet<Aes<*>>()
        if (isLiveMap || !theme.horizontalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.X)
        if (isLiveMap || !theme.verticalAxis(flipAxis = false).showTooltip()) axisWithoutTooltip.add(Aes.Y)

        // Also: don't show the axis tooltip if the axis tick labels are hidden.
        val axisWithNoLabels = HashSet<Aes<*>>()
        if (!theme.horizontalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.X)
        if (!theme.verticalAxis(flipAxis = false).showLabels()) axisWithNoLabels.add(Aes.Y)
        val axisAesFromFunctionKind = resolvedTooltipBehavior.axisAesFromFunctionKind
        val isAxisTooltipEnabled = resolvedTooltipBehavior.axisTooltipEnabled
        val hiddenAesList = createHiddenAesList(
            axisAesFromFunctionKind,
            geomKind,
            resolvedTooltipBehavior,
            renderedAes
        )
            .afterOrientation(isYOrientation) + axisWithoutTooltip
        val axisAes = createAxisAesList(isAxisTooltipEnabled, axisAesFromFunctionKind, geomKind,)
            .afterOrientation(isYOrientation) - hiddenAesList - axisWithNoLabels

        val tooltipAes: List<Aes<*>>
        val sideTooltipAes: List<Aes<*>>

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
        } else {
            tooltipAes = emptyList()
            sideTooltipAes = emptyList()
        }

        val tooltipBehaviorWithVisibleLines = if (theme.tooltips().show()) {
            resolvedTooltipBehavior
        } else {
            // Need to keep specified formats to use for non-hidden tooltips:
            resolvedTooltipBehavior.copy(
                tooltipLinePatterns = null,
                anchor = null,
                minWidth = null,
                tooltipTitle = null,
                disableSplitting = false,
                tooltipGroup = resolvedTooltipBehavior.tooltipGroup,
            )
        }

        return GeomInteraction.create(
            tooltipBehavior = tooltipBehaviorWithVisibleLines,
            tooltipAes = tooltipAes,
            tooltipAxisAes = axisAes,
            sideTooltipAes = sideTooltipAes,
            tooltipConstants = createConstantAesList(geomKind, constantsMap)
        )
    }

    private fun createHiddenAesList(
        axisAes: List<Aes<*>>,
        geomKind: GeomKind,
        tooltipBehavior: TooltipBehavior,
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
                if (tooltipBehavior.tooltipLinePatterns.isNullOrEmpty()) {
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
                        // (ex TooltipModelFactory::removeDiscreteDuplicatedMappings method)
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


    fun createTooltipLines(
        tooltipBehavior: TooltipBehavior,
        tooltipAes: List<Aes<*>>,
        tooltipAxisAes: List<Aes<*>>,
        sideTooltipAes: List<Aes<*>>,
        tooltipConstantAes: Map<Aes<*>, Any>?,
    ): List<LinePattern> {

        return when {
            tooltipBehavior.useDefaultTooltips() -> {
                // No user line patterns => use default tooltips with the given formatted valueSources
                // and move content of side tooltips to the general tooltip in 'disable_splitting' mode
                defaultValueSourceTooltipLines(
                    aesListForTooltip = if (tooltipBehavior.disableSplitting) {
                        (sideTooltipAes + tooltipAes).distinct()
                    } else {
                        tooltipAes
                    },
                    tooltipAxisAes,
                    sideTooltipAes = if (tooltipBehavior.disableSplitting) {
                        emptyList()
                    } else {
                        sideTooltipAes
                    },
                    tooltipBehavior.valueSources,
                    tooltipConstantAes
                )
            }

            tooltipBehavior.hideTooltips() -> {
                // User list is empty => not show tooltips
                emptyList()
            }

            else -> {
                // Form value sources: user list + axis + side tooltips

                val geomSideTooltips = if (tooltipBehavior.disableSplitting) {
                    // Hide side tooltips in 'disable_splitting' mode with specified lines
                    emptyList()
                } else {
                    sideTooltipAes
                }.toMutableList()

                // Remove side tooltip if the mappedAes is used in the general tooltip
                tooltipBehavior.tooltipLinePatterns!!.forEach { line ->
                    val userDataAesList = line.fields.filterIsInstance<MappingField>().map(MappingField::aes)
                    geomSideTooltips.removeAll(userDataAesList)
                }
                val axisValueSources = tooltipAxisAes.map { aes ->
                    getMappingValueSource(aes, isSide = true, isAxis = true, tooltipBehavior.valueSources)
                }
                val geomSideValueSources = geomSideTooltips.map { aes ->
                    getMappingValueSource(aes, isSide = true, isAxis = false, tooltipBehavior.valueSources)
                }

                tooltipBehavior.tooltipLinePatterns +
                        (axisValueSources + geomSideValueSources)
                            .map(LinePattern.Companion::defaultLineForValueSource)
            }
        }
    }


    internal fun getMappingValueSource(
        aes: Aes<*>,
        isSide: Boolean,
        isAxis: Boolean,
        userDefinedValueSources: List<ValueSource>?,
        label: String? = null
    ): ValueSource {
        val userDefined = userDefinedValueSources?.filterIsInstance<MappingField>()?.find { it.aes == aes }
        return userDefined?.withFlags(isSide, isAxis, label) ?: MappingField(
            aes,
            isSide = isSide,
            isAxis = isAxis,
            label = label
        )
    }

    internal fun defaultValueSourceTooltipLines(
        aesListForTooltip: List<Aes<*>>,
        axisAes: List<Aes<*>>,
        sideTooltipAes: List<Aes<*>>,
        userDefinedValueSources: List<ValueSource>? = null,
        constantsMap: Map<Aes<*>, Any>? = null
    ): List<LinePattern> {
        val axisValueSources = axisAes.map { aes ->
            getMappingValueSource(aes, isSide = true, isAxis = true, userDefinedValueSources)
        }
        val sideValueSources = sideTooltipAes.map { aes ->
            getMappingValueSource(aes, isSide = true, isAxis = false, userDefinedValueSources)
        }

        // will use empty label in one-line tooltip for positional aes and constants
        val aesForGeneralTooltip = aesListForTooltip - sideTooltipAes + (constantsMap?.keys ?: emptyList())
        val isOneLineTooltip = aesForGeneralTooltip.size == 1

        val aesValueSources = aesListForTooltip.map { aes ->
            val label = if (isOneLineTooltip && aes in listOf(
                    Aes.X,
                    Aes.Y
                )
            ) "" else null
            getMappingValueSource(aes, isSide = false, isAxis = false, userDefinedValueSources, label)
        }
        val constantFields = constantsMap?.map { (aes, value) ->
            val label = if (isOneLineTooltip) "" else null
            val userDefined = userDefinedValueSources?.filterIsInstance<ConstantField>()?.find { it.aes == aes }
            userDefined?.withLabel(label) ?: ConstantField(aes, value, format = null, label = label)
        } ?: emptyList()

        return (aesValueSources + axisValueSources + sideValueSources + constantFields).map(LinePattern.Companion::defaultLineForValueSource)
    }


}
