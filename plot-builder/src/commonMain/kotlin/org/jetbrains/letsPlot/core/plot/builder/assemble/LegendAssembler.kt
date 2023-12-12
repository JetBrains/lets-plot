/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.FeatureSwitch
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
    private val guideOptionsMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, GuideOptions>,
    private val scaleMappers: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>,
    private val theme: LegendTheme
) {

    private val legendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        aesList: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        constantByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
//        scaleByAes: Map<Aes<*>, Scale>,
//        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
        ctx: PlotContext,
        colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>,
        fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>
    ) {

        legendLayers.add(
            LegendLayer(
                keyFactory,
                aesList,
                constantByAes,
                aestheticsDefaults,
//                scaleByAes,
                scaleMappers,
//                transformedDomainByAes
                ctx,
                colorByAes,
                fillByAes
            )
        )
    }

    fun createLegend(): LegendBoxInfo {
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
        val aesList: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        constantByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
//        scaleMap: Map<Aes<*>, Scale>,
        scaleMappers: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>,
//        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
        ctx: PlotContext,
        colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>,
        fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>
    ) {

        val keyAesthetics: Aesthetics
        val keyLabels: List<String>

        init {
            val aesValuesByLabel =
                LinkedHashMap<String, MutableMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>>()
            for (aes in aesList) {
//                var scale = scaleMap[aes]
                var scale = ctx.getScale(aes)
                if (!scale.hasBreaks()) {
//                    scale = ScaleBreaksUtil.withBreaks(scale, transformedDomainByAes.getValue(aes), 5)
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
            keyAesthetics = mapToAesthetics(
                aesValuesByLabel.values,
                constantByAes,
                aestheticsDefaults,
                colorByAes,
                fillByAes
            )
            keyLabels = ArrayList(aesValuesByLabel.keys)
        }
    }

    companion object {
        private const val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING

        fun wrap(text: String, lengthLimit: Int, countLimit: Int = -1): String {
            if (text.length <= lengthLimit || text.contains("\n")) {
                return text
            }

            return text.split(" ")
                .let { words ->
                    val lines = mutableListOf(mutableListOf<String>())
                    words.forEach { word ->
                        val freeSpace =
                            lengthLimit - lines.last().let { line -> line.sumOf(String::length) + line.size }
                                .coerceAtMost(lengthLimit)
                        when {
                            freeSpace >= word.length -> lines.last().add(word)
                            word.length <= lengthLimit -> lines.add(mutableListOf(word))
                            else -> {
                                lines.last().takeIf { freeSpace > 0 }?.add(word.take(freeSpace))
                                word.drop(freeSpace)
                                    .chunked(lengthLimit)
                                    .forEach {
                                        lines.add(mutableListOf<String>(it))
                                    }
                            }
                        }
                    }
                    lines
                }
                .joinToString(separator = "\n", limit = countLimit) {
                    it.joinToString(separator = " ")
                }
        }

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
            if (options.isByRow) {
                colCount = when {
                    options.hasColCount() -> min(options.colCount, breakCount)
                    options.hasRowCount() -> ceil(breakCount / options.rowCount.toDouble()).toInt()
                    legendDirection === LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                rowCount = ceil(breakCount / colCount.toDouble()).toInt()
            } else {
                // by column
                rowCount = when {
                    options.hasRowCount() -> min(options.rowCount, breakCount)
                    options.hasColCount() -> ceil(breakCount / options.colCount.toDouble()).toInt()
                    legendDirection !== LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                colCount = ceil(breakCount / rowCount.toDouble()).toInt()
            }

            val layout: LegendComponentLayout
            @Suppress("LiftReturnOrAssignment")
            if (legendDirection === LegendDirection.HORIZONTAL) {
                if (options.hasRowCount() || options.hasColCount() && options.colCount < breakCount) {
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
            layout.isFillByRow = options.isByRow

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
