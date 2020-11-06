/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.VarBinding
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
    private val theme: LegendTheme
) {

    private val myLegendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        varBindings: List<VarBinding>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        scaleByAes: TypedScaleMap,
        dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>
    ) {

        myLegendLayers.add(
            LegendLayer(
                keyFactory,
                varBindings,
                constantByAes,
                aestheticsDefaults,
                scaleByAes,
                dataRangeByAes
            )
        )
    }

    fun createLegend(): LegendBoxInfo {
        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in myLegendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics!!.dataPoints().iterator()
            for (label in legendLayer.keyLabels!!) {
                if (!legendBreaksByLabel.containsKey(label)) {
                    legendBreaksByLabel[label] =
                        LegendBreak(label)
                }
                legendBreaksByLabel[label]!!.addLayer(dataPoints.next(), keyElementFactory)
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
        for (legendLayer in myLegendLayers) {
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
                c.debug =
                    DEBUG_DRAWING
                return c
            }
        }
    }


    private class LegendLayer(
        internal val keyElementFactory: LegendKeyElementFactory,
        private val varBindings: List<VarBinding>,
        private val constantByAes: Map<Aes<*>, Any>,
        private val aestheticsDefaults: AestheticsDefaults,
        private val scaleMap: TypedScaleMap,
        dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>
    ) {

        internal var keyAesthetics: Aesthetics? = null
        internal var keyLabels: List<String>? = null

        internal val aesList: List<Aes<*>>
            get() {
                val result = ArrayList<Aes<*>>()
                for (binding in varBindings) {
                    result.add(binding.aes)
                }
                return result
            }

        init {
            init(dataRangeByAes)
        }

        private fun init(dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>) {
            val aesValuesByLabel = LinkedHashMap<String, MutableMap<Aes<*>, Any>>()
            for (varBinding in varBindings) {
                val aes = varBinding.aes
                var scale = scaleMap[aes]
                if (!scale.hasBreaks()) {
                    if (dataRangeByAes.containsKey(aes)) {
                        scale = ScaleBreaksUtil.withBreaks(scale, dataRangeByAes[aes]!!, 5)
                    } else {
                        // skip this scale
                        // (we should never get here)
                        continue
                    }
                }
                checkState(scale.hasBreaks(), "No breaks were defined for scale $aes")
                val values = ScaleUtil.breaksAesthetics(scale).iterator()
                val labels = ScaleUtil.labels(scale)
                for (label in labels) {
                    if (!aesValuesByLabel.containsKey(label)) {
                        aesValuesByLabel[label] = HashMap()
                    }

                    val value = values.next()
                    @Suppress("ReplacePutWithAssignment")
                    aesValuesByLabel[label]!!.put(aes, value!!)
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

            val legendDirection =
                LegendAssemblerUtil.legendDirection(theme)

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
                    layout =
                        LegendComponentLayout.horizontal(title, breaks, keySize)
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
                layout
            )
        }
    }
}
