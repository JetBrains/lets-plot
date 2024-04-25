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
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendAssemblerUtil.mapToAesthetics
import org.jetbrains.letsPlot.core.plot.builder.guide.*
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Legend
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class LegendAssembler(
    private val legendTitle: String,
    private val guideOptionsMap: Map<Aes<*>, GuideOptions>, // Map<Any, GuideOptions> ?
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val theme: LegendTheme
) {

    private val legendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        aesList: List<Aes<*>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>,
        isMarginal: Boolean,
        ctx: PlotContext
    ) {
        legendLayers.add(
            LegendLayer.create(
                keyFactory,
                aesList,
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

    // ToDo
    fun addLayer(
        legendItem: LegendItem,
        keyFactory: LegendKeyElementFactory,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>,
        isMarginal: Boolean
    ) {
        legendLayers.add(
            LegendLayer.withLegendItem(
                legendItem,
                keyFactory,
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
            // ToDo Add ordering by index
            //.sortedBy { it.index }

        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in legendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics.dataPoints().iterator()
            for (label in legendLayer.keyLabels) {
                legendBreaksByLabel.getOrPut(label) {
                    LegendBreak(wrap(label, Legend.LINES_MAX_LENGTH, Legend.LINES_MAX_COUNT))
                }.addLayer(dataPoints.next(), keyElementFactory)
            }
        }

        val legendBreaks = ArrayList<LegendBreak>()
        for (legendBreak in legendBreaksByLabel.values) {
            if (legendBreak.isEmpty) {
                continue
            }
            legendBreaks.add(legendBreak)
        }


        if (legendBreaks.isEmpty()) {
            return LegendBoxInfo.EMPTY
        }

        // legend options
        val legendOptionsList = ArrayList<LegendOptions>()
        for (legendLayer in legendLayers) {
            val aesList = legendLayer.aesList
            for (aes in aesList) {
                if (guideOptionsMap[aes] is LegendOptions) {
                    legendOptionsList.add(guideOptionsMap[aes] as LegendOptions)
                }
            }
        }

        val spec =
            createLegendSpec(
                legendTitle, legendBreaks, theme,
                LegendOptions.combine(
                    legendOptionsList
                )
            )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = LegendComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }


    private class LegendLayer(
        val keyElementFactory: LegendKeyElementFactory,
        val aesList: List<Aes<*>>,
        val isMarginal: Boolean,
        val keyAesthetics: Aesthetics,
        val keyLabels: List<String>,
        val index: Int? = null
    ) {
        companion object {
            fun create(
                keyElementFactory: LegendKeyElementFactory,
                aesList: List<Aes<*>>,
                constantByAes: Map<Aes<*>, Any>,
                aestheticsDefaults: AestheticsDefaults,
                scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
                colorByAes: Aes<Color>,
                fillByAes: Aes<Color>,
                isMarginal: Boolean,
                ctx: PlotContext
            ): LegendLayer {

                val aesValuesByLabel = LinkedHashMap<String, MutableMap<Aes<*>, Any>>()

                for (aes in aesList) {
                    var scale = ctx.getScale(aes)
                    if (!scale.hasBreaks()) {
                        scale = ScaleBreaksUtil.withBreaks(scale, ctx.overallTransformedDomain(aes), 5)
                    }
                    check(scale.hasBreaks()) { "No breaks were defined for scale $aes" }

                    val scaleBreaks = scale.getShortenedScaleBreaks()
                    val aesValues = scaleBreaks.transformedValues.map {
                        scaleMappers.getValue(aes)(it) as Any // Don't expect nulls.
                    }
                    val labels = scaleBreaks.labels
                    for ((label, aesValue) in labels.zip(aesValues)) {
                        aesValuesByLabel.getOrPut(label) { HashMap() }[aes] = aesValue
                    }
                }
                // build 'key' aesthetics
                val keyAesthetics = mapToAesthetics(
                    aesValuesByLabel.values,
                    constantByAes,
                    aestheticsDefaults,
                    colorByAes,
                    fillByAes
                )

                val keyLabels = ArrayList(aesValuesByLabel.keys)
                return LegendLayer(
                    keyElementFactory,
                    aesList,
                    isMarginal,
                    keyAesthetics,
                    keyLabels
                )
            }

            fun withLegendItem(
                legendItem: LegendItem,
                keyElementFactory: LegendKeyElementFactory,
                constantByAes: Map<Aes<*>, Any>,
                aestheticsDefaults: AestheticsDefaults,
                colorByAes: Aes<Color>,
                fillByAes: Aes<Color>,
                isMarginal: Boolean
            ): LegendLayer {
                // ToDo
                // build 'key' aesthetics
                val keyAesthetics = mapToAesthetics(
                    listOf(legendItem.aesValues),
                    constantByAes,
                    aestheticsDefaults,
                    colorByAes,
                    fillByAes

                )
                /*val builder = AestheticsBuilder(1)
                for (aes in Aes.values()) {
                    @Suppress("UNCHECKED_CAST")
                    builder.constantAes(
                        aes as Aes<Any>,
                        aestheticsDefaults.defaultValue(aes)
                    )
                }
                for (aes in constantByAes.keys) {
                    @Suppress("UNCHECKED_CAST")
                    builder.constantAes(aes as Aes<Any>, constantByAes[aes])
                }
                val keyAesthetics = builder.build() */
                return LegendLayer(
                    keyElementFactory,
                    emptyList(),
                    isMarginal,
                    keyAesthetics,
                    keyLabels = listOf(legendItem.label),
                    index = legendItem.index
                )
            }
        }
    }

    companion object {
        private const val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING

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

            val themeKeySize = DoubleVector(theme.keySize(), theme.keySize())
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
