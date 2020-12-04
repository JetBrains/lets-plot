/*
 * Copyright (c) 2020. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
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

    private fun isVariableContinuous(scaleMap: TypedScaleMap, aes: Aes<*>): Boolean {
        return scaleMap.containsKey(aes) && (scaleMap[aes].isContinuous || scaleMap[aes].isContinuousDomain)
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

        val builder = createGeomInteractionBuilder(
            layerConfig.geomProto.renders(),
            layerConfig.geomProto.geomKind,
            layerConfig.statKind,
            multilayer,
            isVariableContinuous(scaleMap, Aes.X)
        )
        val hiddenAesList = createHiddenAesList(layerConfig.geomProto.geomKind) + axisWithoutTooltip
        val axisAes = createAxisAesList(builder, layerConfig.geomProto.geomKind) - hiddenAesList
        val aesList = createTooltipAesList(layerConfig, scaleMap, builder.getAxisFromFunctionKind) - hiddenAesList
        val outlierAesList = createOutlierAesList(layerConfig.geomProto.geomKind)

        return builder.axisAes(axisAes)
            .tooltipAes(aesList)
            .tooltipOutliers(outlierAesList)
            .tooltipLinesSpec(layerConfig.tooltips)
            .tooltipConstants(layerConfig.constantsMap.filter { (aes, _) -> Aes.isPositional(aes) })
            .showAxisTooltip(!isLiveMap)
    }

    private fun createGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind,
        multilayer: Boolean,
        isContinuousX: Boolean

    ): GeomInteractionBuilder {

        val builder = initGeomInteractionBuilder(renders, geomKind, statKind, isContinuousX)

        if (multilayer) {
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

    private fun createHiddenAesList(geomKind: GeomKind): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.BOX_PLOT -> listOf(Aes.Y)
            GeomKind.RECT -> listOf(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
            // by default geom_text doesn't show tooltips, but user can enable them via tooltips config
            GeomKind.TEXT -> GeomMeta.renders(GeomKind.TEXT)
            else -> emptyList()
        }
    }

    private fun createAxisAesList(geomBuilder: GeomInteractionBuilder, geomKind: GeomKind): List<Aes<*>> {
        if (!geomBuilder.isAxisTooltipEnabled) return emptyList()

        val axisAesFromConfig = if (geomKind === GeomKind.SMOOTH)
            listOf(Aes.X)
        else
            emptyList()
        return if (axisAesFromConfig.isNotEmpty())
            axisAesFromConfig
        else
            geomBuilder.getAxisFromFunctionKind
    }

    private fun createTooltipAesList(
        layerConfig: LayerConfig,
        scaleMap: TypedScaleMap,
        axisAes: List<Aes<*>>
    ): List<Aes<*>> {
        // remove axis mapping: if aes and axis are bound to the same data
        val aesListForTooltip = ArrayList(layerConfig.geomProto.renders() - axisAes)
        for (aes in axisAes) {
            if (isVariableContinuous(scaleMap, aes)) {
                val axisVariable = layerConfig.getVariableForAes(aes)
                aesListForTooltip.removeAll { layerConfig.getVariableForAes(it) == axisVariable }
            }
        }

        // remove auto generated mappings
        val autoGenerated = listOf<String>()
        aesListForTooltip.removeAll { scaleMap.containsKey(it) && scaleMap[it].name in autoGenerated }

        // remove discrete mappings
        aesListForTooltip.removeAll { !isVariableContinuous(scaleMap, it) }

        // remove duplicated mappings
        // (this step was in TooltipSpecFactory::removeDiscreteDuplicatedMappings method)
        val mappingsToShow = HashMap<String, Aes<*>>()
        aesListForTooltip
            .filter { aes -> scaleMap.containsKey(aes) }
            .forEach { aes ->
                val label = scaleMap[aes].name
                val mappingToShow = mappingsToShow[label]
                if (mappingToShow == null) {
                    mappingsToShow[label] = aes
                } else if (!isVariableContinuous(scaleMap, mappingToShow) && isVariableContinuous(scaleMap, aes)) {
                    mappingsToShow[label] = aes
                }
            }
        return mappingsToShow.values.toList()
    }

    private fun createOutlierAesList(geomKind: GeomKind) = when (geomKind) {
        GeomKind.CROSS_BAR,
        GeomKind.ERROR_BAR,
        GeomKind.LINE_RANGE,
        GeomKind.POINT_RANGE -> listOf(Aes.YMAX, Aes.YMIN)
        GeomKind.BOX_PLOT -> listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN)
        GeomKind.SMOOTH -> listOf(Aes.YMAX, Aes.YMIN, Aes.Y)
        else -> emptyList()
    }

    private fun initGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind,
        isContinuousX: Boolean
    ): GeomInteractionBuilder {
        val builder = GeomInteractionBuilder(renders)
        if (statKind === StatKind.SMOOTH) {
            when (geomKind) {
                GeomKind.POINT,
                GeomKind.CONTOUR -> return builder.univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
                else -> {
                }
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
            GeomKind.BOX_PLOT,
            GeomKind.V_LINE -> return builder.univariateFunction(GeomTargetLocator.LookupStrategy.HOVER)
                .showAxisTooltip(isContinuousX)
            GeomKind.TILE -> return builder.bivariateFunction(GeomInteractionBuilder.AREA_GEOM).showAxisTooltip(true)
            GeomKind.TEXT,
            GeomKind.SMOOTH,
            GeomKind.POINT,
            GeomKind.CONTOUR,
            GeomKind.RIBBON,
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
            GeomKind.BIN_2D,
            GeomKind.MAP,
            GeomKind.RECT -> return builder.bivariateFunction(GeomInteractionBuilder.AREA_GEOM)

            GeomKind.LIVE_MAP -> return builder.bivariateFunction(GeomInteractionBuilder.NON_AREA_GEOM)

            else -> return builder.none()
        }
    }
}
