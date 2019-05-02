package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.FeatureSwitch
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.visualization.plot.gog.plot.ColorBarOptions
import jetbrains.datalore.visualization.plot.gog.plot.guide.ColorBarComponent
import jetbrains.datalore.visualization.plot.gog.plot.guide.ColorBarComponentSpec
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendBox
import jetbrains.datalore.visualization.plot.gog.plot.layout.LegendBoxInfo
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.theme.LegendTheme

class ColorBarAssembler(private val myLegendTitle: String, private val myDomain: ClosedRange<Double>, private val myScale: Scale2<Color>, private val myTheme: LegendTheme) {
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
                c.debug.set(DEBUG_DRAWING)
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
