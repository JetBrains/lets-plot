package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.FeatureSwitch
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.visualization.plot.builder.ColorBarOptions
import jetbrains.datalore.visualization.plot.builder.guide.ColorBarComponent
import jetbrains.datalore.visualization.plot.builder.guide.ColorBarComponentSpec
import jetbrains.datalore.visualization.plot.builder.guide.LegendBox
import jetbrains.datalore.visualization.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.visualization.plot.builder.scale.GuideBreak
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

class ColorBarAssembler(private val myLegendTitle: String, private val myDomain: ClosedRange<Double>, private val myScale: Scale<Color>, private val myTheme: LegendTheme) {
    private var myOptions: ColorBarOptions? = null

    fun createColorBar(): LegendBoxInfo {
        var scale = myScale
        if (!scale.hasBreaks()) {
            scale = ScaleBreaksUtil.withBreaks(scale, myDomain, 5)
        }

        val guideBreaks = ArrayList<GuideBreak<Double>>()
        val breaks = ScaleUtil.breaksTransformed(scale)
        val label = ScaleUtil.labels(scale).iterator()
        for (v in breaks) {
            guideBreaks.add(GuideBreak(v, label.next()))
        }

        if (guideBreaks.isEmpty()) {
            return LegendBoxInfo.EMPTY
        }

        val spec = ColorBarComponentSpec(myLegendTitle, myDomain, guideBreaks, scale, myTheme)
        if (myOptions != null) {
            if (myOptions!!.hasBinCount()) {
                spec.binCount = myOptions!!.binCount
            }
            if (myOptions!!.hasWidth()) {
                spec.setBarWidth(myOptions!!.width!!)
            }
            if (myOptions!!.hasHeight()) {
                spec.setRodHeight(myOptions!!.height!!)
            }
        }

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = ColorBarComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }

    internal fun setOptions(options: ColorBarOptions?) {
        myOptions = options
    }

    companion object {
        private val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING
    }
}
