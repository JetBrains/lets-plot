/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.formatting.string.wrap
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.FeatureSwitch
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.ScaleBreaksUtil
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendAssemblerUtil.mapToAesthetics
import org.jetbrains.letsPlot.core.plot.builder.guide.*
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Legend
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class LegendAssembler(
    private val legendTitle: String,
    private val guideOptionsMap: Map<GuideKey, GuideOptionsList>,
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val legendTheme: LegendTheme,
    private val panelTheme: PanelTheme
) {

    private val legendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        aesList: List<Aes<*>>,
        overrideAesValues: Map<Aes<*>, Any>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>,
        isMarginal: Boolean,
        ctx: PlotContext,
    ) {
        legendLayers.add(
            LegendLayer.createDefaultLegendLayer(
                keyFactory,
                aesList,
                overrideAesValues,
                constantByAes,
                aestheticsDefaults,
                scaleMappers,
                colorByAes,
                fillByAes,
                isMarginal,
                ctx
            )
        )
    }

    fun addCustomLayer(
        customLegendOptions: CustomLegendOptions,
        keyFactory: LegendKeyElementFactory,
        overrideAesValues: Map<Aes<*>, Any>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>,
        isMarginal: Boolean
    ) {
        legendLayers.add(
            LegendLayer.createCustomLegendLayer(
                customLegendOptions,
                keyFactory,
                overrideAesValues,
                constantByAes,
                aestheticsDefaults,
                colorByAes,
                fillByAes,
                isMarginal
            )
        )
    }

    fun createLegend(): LegendBoxInfo {
        val includeMarginalLayers = legendLayers.all { it.isMarginal } // Yes, if there are no 'core' layers.
        val legendLayers = legendLayers
            .filter { includeMarginalLayers || !it.isMarginal }
            .sortedWith(compareBy(nullsLast(), LegendLayer::index))

        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in legendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics.dataPoints().iterator()
            for (label in legendLayer.labels) {
                legendBreaksByLabel.getOrPut(label) {
                    LegendBreak(wrap(label, Legend.LINES_MAX_LENGTH, Legend.LINES_MAX_COUNT))
                }.addLayer(dataPoints.next(), keyElementFactory)
            }
        }

        val legendBreaks = legendBreaksByLabel.values.filterNot { it.isEmpty }
        if (legendBreaks.isEmpty()) {
            return LegendBoxInfo.EMPTY
        }

        // legend options
        val legendOptionsList = legendLayers
            .flatMap(LegendLayer::guideKeys)
            .mapNotNull(guideOptionsMap::get)
            .mapNotNull(GuideOptionsList::getLegendOptions)
        val combinedLegendOptions = LegendOptions.combine(legendOptionsList)

        val spec = createLegendSpec(legendTitle, legendBreaks, legendTheme, combinedLegendOptions)

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = LegendComponent(spec, panelTheme)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }


    private class LegendLayer(
        val keyElementFactory: LegendKeyElementFactory,
        val keyAesthetics: Aesthetics,
        val labels: List<String>,
        val guideKeys: List<GuideKey>,
        val index: Int? = null,
        val isMarginal: Boolean
    ) {
        companion object {
            fun createDefaultLegendLayer(
                keyElementFactory: LegendKeyElementFactory,
                aesList: List<Aes<*>>,
                overrideAesValues: Map<Aes<*>, Any>,
                constantByAes: Map<Aes<*>, Any>,
                aestheticsDefaults: AestheticsDefaults,
                scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
                colorByAes: Aes<Color>,
                fillByAes: Aes<Color>,
                isMarginal: Boolean,
                ctx: PlotContext
            ): LegendLayer {

                val labelsValuesByAes: MutableMap<Aes<*>, Pair<List<String>, List<Any?>>> = mutableMapOf()

                for (aes in aesList) {
                    var scale = ctx.getScale(aes)
                    if (!scale.hasBreaks()) {
                        scale = ScaleBreaksUtil.withBreaks(scale, ctx.overallTransformedDomain(aes), 5)
                    }
                    check(scale.hasBreaks()) { "No breaks were defined for scale $aes" }

                    val scaleBreaks = scale.getShortenedScaleBreaks()

                    // processOverrideAesValues() has a quadratic complexity - limit the number of labels.
                    // And big legends are not very useful anyway.
                    if (scaleBreaks.labels.size > MAX_LEGEND_LABELS) {
                        continue
                    }

                    val aesValues = scaleBreaks.transformedValues.map {
                        scaleMappers.getValue(aes)(it) as Any // Don't expect nulls.
                    }

                    val labels = scaleBreaks.labels
                    labelsValuesByAes[aes] = labels to aesValues
                }

                val labelValues = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

                val keyAesthetics = mapToAesthetics(
                    labelValues.second,
                    constantByAes.filterKeys {
                        // Derive some aesthetics from constants
                        it in listOf(
                            Aes.SHAPE,
                            Aes.COLOR,
                            Aes.FILL,
                            Aes.PAINT_A, Aes.PAINT_B, Aes.PAINT_C
                        )
                    },
                    aestheticsDefaults,
                    colorByAes,
                    fillByAes
                )

                return LegendLayer(
                    keyElementFactory,
                    keyAesthetics,
                    labels = labelValues.first,
                    guideKeys = aesList.map(GuideKey::fromAes),
                    isMarginal = isMarginal
                )
            }

            fun createCustomLegendLayer(
                customLegendOptions: CustomLegendOptions,
                keyElementFactory: LegendKeyElementFactory,
                overrideAesValues: Map<Aes<*>, Any>,
                constantByAes: Map<Aes<*>, Any>,
                aestheticsDefaults: AestheticsDefaults,
                colorByAes: Aes<Color>,
                fillByAes: Aes<Color>,
                isMarginal: Boolean
            ): LegendLayer {
                val keyAesthetics = mapToAesthetics(
                    listOf(overrideAesValues + customLegendOptions.aesValues),
                    constantByAes,
                    aestheticsDefaults,
                    colorByAes,
                    fillByAes
                )
                return LegendLayer(
                    keyElementFactory,
                    keyAesthetics,
                    labels = listOf(customLegendOptions.label),
                    guideKeys = listOf(GuideKey.fromName(customLegendOptions.group)),
                    customLegendOptions.index,
                    isMarginal
                )
            }
        }
    }

    companion object {
        private const val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING
        private const val MAX_LEGEND_LABELS = 200

        fun createLegendSpec(
            title: String,
            breaks: List<LegendBreak>,
            theme: LegendTheme,
            options: LegendOptions = LegendOptions()
        ): LegendComponentSpec {

            val legendDirection = LegendAssemblerUtil.legendDirection(theme)

            // key size
            fun pretty(v: DoubleVector): DoubleVector {
                val margin = 1.0
                return DoubleVector(
                    floor(v.x / 2) * 2 + 1.0 + margin,
                    floor(v.y / 2) * 2 + 1.0 + margin
                )
            }

            val themeKeySize = theme.keySize()
            val keySizes = breaks
                .map { br -> themeKeySize.max(pretty(br.minimumKeySize)) }
                .let { sizes ->
                    // Use max height for horizontal and max width for vertical legend for better (central) alignment
                    if (legendDirection == LegendDirection.HORIZONTAL) {
                        val maxKeyHeight = sizes.maxOf(DoubleVector::y)
                        sizes.map { DoubleVector(it.x, maxKeyHeight) }
                    } else {
                        val maxKeyWidth = sizes.maxOf(DoubleVector::x)
                        sizes.map { DoubleVector(maxKeyWidth, it.y) }
                    }
                }

            // row, col count
            val breakCount = breaks.size
            val colCount: Int
            val rowCount: Int
            if (options.byRow) {
                colCount = when {
                    options.hasColCount() -> min(options.colCount!!, breakCount)
                    options.hasRowCount() -> ceil(breakCount / options.rowCount!!.toDouble()).toInt()
                    legendDirection === LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                rowCount = ceil(breakCount / colCount.toDouble()).toInt()
            } else {
                // by column
                rowCount = when {
                    options.hasRowCount() -> min(options.rowCount!!, breakCount)
                    options.hasColCount() -> ceil(breakCount / options.colCount!!.toDouble()).toInt()
                    legendDirection !== LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                colCount = ceil(breakCount / rowCount.toDouble()).toInt()
            }

            val layout: LegendComponentLayout
            @Suppress("LiftReturnOrAssignment")
            if (legendDirection === LegendDirection.HORIZONTAL) {
                if (options.hasRowCount() || options.hasColCount() && options.colCount!! < breakCount) {
                    layout = LegendComponentLayout.horizontalMultiRow(
                        title,
                        breaks,
                        keySizes,
                        theme
                    )
                } else {
                    layout = LegendComponentLayout.horizontal(title, breaks, keySizes, theme)
                }
            } else {
                layout = LegendComponentLayout.vertical(title, breaks, keySizes, theme)
            }

            layout.colCount = colCount
            layout.rowCount = rowCount
            layout.isFillByRow = options.byRow

            return LegendComponentSpec(
                title,
                breaks,
                theme,
                layout,
                reverse = false
            )
        }
    }
}

internal fun processOverrideAesValues(
    labelsValuesByAes: MutableMap<Aes<*>, Pair<List<String>, List<Any?>>>,
    overrideAesValues: Map<Aes<*>, Any>
): Pair<List<String>, List<Map<Aes<*>, Any>>> {
    val maxLabelsSize = labelsValuesByAes.values.map { it.first.size }.maxOrNull() ?: 0
    val overrideAesValueLists = createOverrideAesValueLists(overrideAesValues, maxLabelsSize)
    return applyOverrideAesLists(overrideAesValueLists, labelsValuesByAes)
}

internal fun applyOverrideAesLists(
    overrideAesValueLists: Map<Aes<*>, List<Any?>>,
    labelsValuesByAes: MutableMap<Aes<*>, Pair<List<String>, List<Any?>>>
): Pair<List<String>, List<Map<Aes<*>, Any>>> {
    val labelsLists = labelsValuesByAes.values.map{ it.first }

    labelsLists.forEach { labels ->
        overrideAesValueLists.forEach { (aesToOverride, valueList) ->
            val currentValues = labelsValuesByAes.getOrPut(aesToOverride) { labels to valueList }.second
            val updatedValues = currentValues
                .zip(valueList)
                .map { (oldValue, newValue) -> newValue ?: oldValue }

            labelsValuesByAes[aesToOverride] = labels to updatedValues
        }
    }

    val keyLabels = labelsLists.flatten().distinct()

    val mapsByLabel = keyLabels.map { label ->
        labelsValuesByAes.mapNotNull { (aes, pair) ->
            pair.first.zip(pair.second)
                .lastOrNull { it.first == label }
                ?.second
                ?.let { aes to it }
        }.toMap()
    }

    return keyLabels to mapsByLabel
}

internal fun createOverrideAesValueLists(
    overrideAesValues: Map<Aes<*>, Any>,
    maxLabelsSize: Int
): Map<Aes<*>, List<Any?>> {
    val overrideAesValueLists = overrideAesValues.mapValues { (_, value) ->
        val valueList = when (value) {
            is List<*> -> value.ifEmpty { listOf(null) }
            else -> listOf(value)
        }
        if (maxLabelsSize <= valueList.size) {
            valueList
        } else {
            valueList + List(maxLabelsSize - valueList.size) { valueList.last() }
        }
    }
    return overrideAesValueLists
}

