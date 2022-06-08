/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.assemble.LegendAssemblerUtil.mapToAesthetics
import jetbrains.datalore.plot.builder.guide.*
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.theme.LegendTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class LegendAssembler(
    private val legendTitle: String,
    private val guideOptionsMap: Map<Aes<*>, GuideOptions>,
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val theme: LegendTheme
) {

    private val legendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        aesList: List<Aes<*>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        scaleByAes: TypedScaleMap,
        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
    ) {

        legendLayers.add(
            LegendLayer(
                keyFactory,
                aesList,
                constantByAes,
                aestheticsDefaults,
                scaleByAes,
                scaleMappers,
                transformedDomainByAes
            )
        )
    }

    fun createLegend(): LegendBoxInfo {
        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in legendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics.dataPoints().iterator()
            for (label in legendLayer.keyLabels) {
                legendBreaksByLabel.getOrPut(label) { LegendBreak(label) }
                    .addLayer(dataPoints.next(), keyElementFactory)
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
        internal val keyElementFactory: LegendKeyElementFactory,
        internal val aesList: List<Aes<*>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        scaleMap: TypedScaleMap,
        scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
    ) {

        internal val keyAesthetics: Aesthetics
        internal val keyLabels: List<String>

        init {
            val aesValuesByLabel = LinkedHashMap<String, MutableMap<Aes<*>, Any>>()
            for (aes in aesList) {
                var scale = scaleMap[aes]
                if (!scale.hasBreaks()) {
                    scale = ScaleBreaksUtil.withBreaks(scale, transformedDomainByAes.getValue(aes), 5)
                }
                check(scale.hasBreaks()) { "No breaks were defined for scale $aes" }

                val scaleBreaks = scale.getScaleBreaks()
                val aesValues = scaleBreaks.transformedValues.map {
                    scaleMappers.getValue(aes)(it) as Any // Don't expect nulls.
                }
                val labels = scaleBreaks.labels
                for ((label, aesValue) in labels.zip(aesValues)) {
                    aesValuesByLabel.getOrPut(label) { HashMap() }[aes] = aesValue
                }
            }

            // build 'key' aesthetics
            keyAesthetics = mapToAesthetics(aesValuesByLabel.values, constantByAes, aestheticsDefaults)
            keyLabels = ArrayList(aesValuesByLabel.keys)
        }
    }

    companion object {
        private const val DEBUG_DRAWING = jetbrains.datalore.plot.FeatureSwitch.LEGEND_DEBUG_DRAWING

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

            var keySize = DoubleVector(theme.keySize(), theme.keySize())
            for (br in breaks) {
                val minimumKeySize = br.minimumKeySize
                keySize = keySize.max(pretty(minimumKeySize))
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
                        keySize
                    )
                } else {
                    layout = LegendComponentLayout.horizontal(title, breaks, keySize)
                }
            } else {
                layout = LegendComponentLayout.vertical(title, breaks, keySize)
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
