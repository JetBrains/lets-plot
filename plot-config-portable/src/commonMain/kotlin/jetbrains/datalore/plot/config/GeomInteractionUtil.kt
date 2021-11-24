/*
 * Copyright (c) 2020. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.interact.GeomInteraction
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.theme.Theme

object GeomInteractionUtil {
    internal fun configGeomTargets(
        layerConfig: LayerConfig,
        scaleMap: TypedScaleMap,
        multilayer: Boolean,
        isLiveMap: Boolean,
        theme: Theme
    ): GeomInteraction {
        return createGeomInteractionBuilder(layerConfig, scaleMap, multilayer, isLiveMap, theme).build()
    }

    internal fun createGeomInteractionBuilder(
        layerConfig: LayerConfig,
        scaleMap: TypedScaleMap,
        multilayer: Boolean,
        isLiveMap: Boolean,
        theme: Theme
    ): GeomInteractionBuilder {
        val axisWithoutTooltip = HashSet<Aes<*>>()
        if (isLiveMap || !theme.axisX().showTooltip()) axisWithoutTooltip.add(Aes.X)
        if (isLiveMap || !theme.axisY().showTooltip()) axisWithoutTooltip.add(Aes.Y)

        val isCrosshairEnabled = isCrosshairEnabled(layerConfig)
        val builder = createGeomInteractionBuilder(
            layerConfig.geomProto.renders(),
            layerConfig.geomProto.geomKind,
            layerConfig.statKind,
            multilayer,
            isCrosshairEnabled
        )
        val hiddenAesList = createHiddenAesList(layerConfig, builder.getAxisFromFunctionKind) + axisWithoutTooltip
        val axisAes = createAxisAesList(builder, layerConfig.geomProto.geomKind, theme) - hiddenAesList
        val aesList = createTooltipAesList(layerConfig, scaleMap, builder.getAxisFromFunctionKind) - hiddenAesList
        val outlierAesList = createOutlierAesList(layerConfig.geomProto.geomKind)

        return builder.axisAes(axisAes)
            .tooltipAes(aesList)
            .tooltipOutliers(outlierAesList)
            .tooltipLinesSpec(layerConfig.tooltips)
            .tooltipConstants(createConstantAesList(layerConfig))
            .showAxisTooltip(!isLiveMap)
            .setIsCrosshairEnabled(isCrosshairEnabled)
    }

    private fun createGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind,
        multilayer: Boolean,
        isCrosshairEnabled: Boolean
    ): GeomInteractionBuilder {

        val builder = initGeomInteractionBuilder(renders, geomKind, statKind, isCrosshairEnabled)

        if (multilayer && !isCrosshairEnabled) {
            // Only these kinds of geoms should be switched to NEAREST XY strategy on a multilayer plot.
            // Rect, histogram and other column alike geoms should not switch searching strategy, otherwise
            // tooltips behaviour becomes unexpected(histogram shows tooltip when cursor is close enough,
            // but not above a column).
            if (listOf(GeomKind.LINE, GeomKind.DENSITY, GeomKind.AREA, GeomKind.FREQPOLY).contains(geomKind)) {
                builder.multilayerLookupStrategy()
            } else if (statKind === StatKind.SMOOTH) {
                when (geomKind) {
                    GeomKind.POINT, GeomKind.CONTOUR -> builder.multilayerLookupStrategy()
                    else -> {
                    }
                }
            }
        }

        return builder
    }

    private fun createHiddenAesList(layerConfig: LayerConfig, axisAes: List<Aes<*>>): List<Aes<*>> {
        return when (layerConfig.geomProto.geomKind) {
            GeomKind.BOX_PLOT -> listOf(Aes.Y)
            GeomKind.RECT -> listOf(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
            GeomKind.SEGMENT -> listOf(Aes.X, Aes.Y, Aes.XEND, Aes.YEND)
            GeomKind.TEXT -> {
                // by default geom_text doesn't show tooltips,
                // but user can enable them via tooltips config in which case the axis tooltips should also be displayed
                if (layerConfig.tooltips.tooltipLinePatterns.isNullOrEmpty()) {
                    GeomMeta.renders(GeomKind.TEXT)
                } else {
                    GeomMeta.renders(GeomKind.TEXT) - axisAes
                }
            }
            else -> emptyList()
        }
    }

    private fun createAxisAesList(geomBuilder: GeomInteractionBuilder, geomKind: GeomKind, theme: Theme): List<Aes<*>> {
        return when {
            !geomBuilder.isAxisTooltipEnabled -> emptyList()
            geomKind == GeomKind.SMOOTH -> listOf(Aes.X)
            else -> geomBuilder.getAxisFromFunctionKind
        }.let {
            // Not show the axis tooltip if the axis tick labels are hidden
            val axisAesList = it.toMutableList()
            if (!theme.axisX().showLabels()) axisAesList.remove(Aes.X)
            if (!theme.axisY().showLabels()) axisAesList.remove(Aes.Y)
            axisAesList
        }
    }

    private fun createTooltipAesList(
        layerConfig: LayerConfig,
        scaleMap: TypedScaleMap,
        axisAes: List<Aes<*>>
    ): List<Aes<*>> {
        // remove axis mapping: if aes and axis are bound to the same data
        val aesListForTooltip = ArrayList(layerConfig.geomProto.renders() - axisAes)
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
        aesListForTooltip
            .forEach { aes ->
                val variable = layerConfig.getVariableForAes(aes)!!
                val mappingToShow = mappingsToShow[variable]
                when {
                    mappingToShow == null ->  {
                        mappingsToShow[variable] = aes
                    }
                    !isVariableContinuous(scaleMap, mappingToShow) && isVariableContinuous(scaleMap, aes) -> {
                        // If the same variable is mapped twice as continuous and discrete - use the continuous value
                        // (ex TooltipSpecFactory::removeDiscreteDuplicatedMappings method)
                        mappingsToShow[variable] = aes
                    }
                    scaleMap[aes].name != variable.label -> {
                        // Use variable which is shown by the scale with its name
                        mappingsToShow[variable] = aes
                    }
                }
            }
        return mappingsToShow.values.toList()
    }

    private fun createOutlierAesList(geomKind: GeomKind) = when (geomKind) {
        GeomKind.CROSS_BAR,
        GeomKind.ERROR_BAR,
        GeomKind.LINE_RANGE,
        GeomKind.POINT_RANGE,
        GeomKind.RIBBON -> listOf(Aes.YMAX, Aes.YMIN)
        GeomKind.BOX_PLOT -> listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN)
        GeomKind.SMOOTH -> listOf(Aes.YMAX, Aes.YMIN, Aes.Y)
        else -> emptyList()
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
    private fun isTooltipForAesEnabled(aes: Aes<*>, scaleMap: TypedScaleMap): Boolean {
        if (isVariableContinuous(scaleMap, aes)) {
            return true
        }
        val factors = scaleMap.safeGet(aes)?.getScaleBreaks()?.domainValues ?: return false
        return factors.size >= MIN_FACTORS_TO_SHOW_TOOLTIPS
    }

    private fun initGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind,
        isCrosshairEnabled: Boolean
    ): GeomInteractionBuilder {
        val builder = GeomInteractionBuilder(renders)
        if (statKind === StatKind.SMOOTH) {
            when (geomKind) {
                GeomKind.POINT,
                GeomKind.CONTOUR -> return builder.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                else -> {}
            }
        } else if (statKind == StatKind.CORR) {
            when (geomKind) {
                GeomKind.POINT -> return builder
                    .bivariateFunction(GeomInteractionBuilder.NON_AREA_GEOM)
                    .ignoreInvisibleTargets(true)
                GeomKind.TILE -> return builder
                    .bivariateFunction(GeomInteractionBuilder.AREA_GEOM)
                    .showAxisTooltip(true)
                    .ignoreInvisibleTargets(true)
                else -> {}
            }
        }

        when (geomKind) {
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.HISTOGRAM,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.BAR,
            GeomKind.ERROR_BAR,
            GeomKind.CROSS_BAR,
            GeomKind.POINT_RANGE,
            GeomKind.LINE_RANGE,
            GeomKind.SEGMENT,
            GeomKind.V_LINE -> return builder.univariateFunction(GeomTargetLocator.LookupStrategy.HOVER)
                .showAxisTooltip(true)
            GeomKind.RIBBON -> return builder.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            GeomKind.SMOOTH -> return if (isCrosshairEnabled) {
                builder.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            } else {
                builder.bivariateFunction(GeomInteractionBuilder.NON_AREA_GEOM)
            }
            GeomKind.BOX_PLOT,
            GeomKind.BIN_2D,
            GeomKind.TILE -> return builder.bivariateFunction(GeomInteractionBuilder.AREA_GEOM).showAxisTooltip(true)
            GeomKind.TEXT,
            GeomKind.POINT,
            GeomKind.JITTER,
            GeomKind.CONTOUR,
            GeomKind.DENSITY2D -> return builder.bivariateFunction(GeomInteractionBuilder.NON_AREA_GEOM)
            GeomKind.PATH -> {
                when (statKind) {
                    StatKind.CONTOUR, StatKind.CONTOURF, StatKind.DENSITY2D -> return builder.bivariateFunction(
                        GeomInteractionBuilder.NON_AREA_GEOM
                    )
                    else -> {
                    }
                }
                return builder.bivariateFunction(GeomInteractionBuilder.AREA_GEOM)
            }
            GeomKind.H_LINE,
            GeomKind.DENSITY2DF,
            GeomKind.CONTOURF,
            GeomKind.POLYGON,
            GeomKind.MAP,
            GeomKind.RECT -> return builder.bivariateFunction(GeomInteractionBuilder.AREA_GEOM)

            GeomKind.LIVE_MAP -> return builder.bivariateFunction(GeomInteractionBuilder.NON_AREA_GEOM)

            else -> return builder.none()
        }
    }
}

private fun <T> TypedScaleMap.safeGet(aes: Aes<T>) = if (containsKey(aes)) get(aes) else null

private fun isVariableContinuous(scaleMap: TypedScaleMap, aes: Aes<*>) =
    scaleMap.safeGet(aes)?.isContinuousDomain ?: false

