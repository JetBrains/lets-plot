package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.FeatureSwitch
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.visualization.plot.builder.GuideOptions
import jetbrains.datalore.visualization.plot.builder.LegendOptions
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.assemble.LegendAssemblerUtil.mapToAesthetics
import jetbrains.datalore.visualization.plot.builder.guide.LegendBox
import jetbrains.datalore.visualization.plot.builder.guide.LegendBreak
import jetbrains.datalore.visualization.plot.builder.guide.LegendComponent
import jetbrains.datalore.visualization.plot.builder.guide.LegendComponentSpec
import jetbrains.datalore.visualization.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

internal class LegendAssembler(private val myLegendTitle: String, private val myGuideOptionsMap: Map<Aes<*>, GuideOptions>, private val myTheme: LegendTheme) {

    private val myLegendLayers = ArrayList<LegendLayer>()

    fun addLayer(
            keyFactory: LegendKeyElementFactory, varBindings: List<VarBinding>, constantByAes: Map<Aes<*>, Any>,
            aestheticsDefaults: AestheticsDefaults, dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>
    ) {
        myLegendLayers.add(LegendLayer(keyFactory, varBindings, constantByAes, aestheticsDefaults, dataRangeByAes))
    }

    fun createLegend(): LegendBoxInfo {
        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in myLegendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics!!.dataPoints().iterator()
            for (label in legendLayer.keyLabels!!) {
                if (!legendBreaksByLabel.containsKey(label)) {
                    legendBreaksByLabel[label] = LegendBreak(label)
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
                if (myGuideOptionsMap[aes] is LegendOptions) {
                    legendOptionsList.add(myGuideOptionsMap[aes] as LegendOptions)
                }
            }
        }

        val spec = LegendComponentSpec(myLegendTitle, legendBreaks, myTheme)
        spec.setLegendOptions(LegendOptions.combine(legendOptionsList))

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = LegendComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }

    private class LegendLayer internal constructor(
            internal val keyElementFactory: LegendKeyElementFactory, private val myVarBindings: List<VarBinding>, private val myConstantByAes: Map<Aes<*>, Any>,
            private val myAestheticsDefaults: AestheticsDefaults, dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>
    ) {

        internal var keyAesthetics: Aesthetics? = null
        internal var keyLabels: List<String>? = null

        internal val aesList: List<Aes<*>>
            get() {
                val result = ArrayList<Aes<*>>()
                for (binding in myVarBindings) {
                    result.add(binding.aes)
                }
                return result
            }

        init {
            init(dataRangeByAes)
        }

        private fun init(dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>) {
            val aesValuesByLabel = LinkedHashMap<String, MutableMap<Aes<*>, Any>>()
            for (varBinding in myVarBindings) {
                val aes = varBinding.aes
                var scale = varBinding.scale
                if (!scale!!.hasBreaks()) {
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
            keyAesthetics = mapToAesthetics(aesValuesByLabel.values, myConstantByAes, myAestheticsDefaults)
            keyLabels = ArrayList(aesValuesByLabel.keys)
        }
    }

    companion object {
        private const val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING
    }
}
