package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList
import jetbrains.datalore.visualization.plot.core.AestheticsDefaults
import jetbrains.datalore.visualization.plot.gog.FeatureSwitch
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.visualization.plot.gog.plot.GuideOptions
import jetbrains.datalore.visualization.plot.gog.plot.LegendOptions
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.LegendAssemblerUtil.mapToAesthetics
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendBox
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendBreak
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendComponent
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendComponentSpec
import jetbrains.datalore.visualization.plot.gog.plot.layout.LegendBoxInfo
import jetbrains.datalore.visualization.plot.gog.plot.theme.LegendTheme

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
                c.debug.set(DEBUG_DRAWING)
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
                    aesValuesByLabel[label]!!.put(aes, value!!)
                }
            }

            // build 'key' aesthetics
            keyAesthetics = mapToAesthetics(aesValuesByLabel.values, myConstantByAes, myAestheticsDefaults)
            keyLabels = unmodifiableList(ArrayList(aesValuesByLabel.keys))
        }
    }

    companion object {
        private val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING
    }
}
