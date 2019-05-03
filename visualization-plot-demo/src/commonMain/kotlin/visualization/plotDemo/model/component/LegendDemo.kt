package jetbrains.datalore.visualization.plotDemo.model.component

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.aes.AestheticsBuilder
import jetbrains.datalore.visualization.plot.gog.core.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GenericLegendKeyElementFactory
import jetbrains.datalore.visualization.plot.gog.core.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.gog.core.scale.Scales
import jetbrains.datalore.visualization.plot.gog.plot.guide.ColorBarComponent
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendBreak
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendComponent
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.ColorMapper
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase

open class LegendDemo : SimpleDemoBase() {

    protected fun createModels(): List<GroupComponent> {
        return listOf(
                simple(),
                colorBar()
        )
    }

    private fun simple(): GroupComponent {
        val count = 3
        val labels = listOf("First", "Second", "Third")
        val colors = arrayOf(Color.LIGHT_BLUE, Color.LIGHT_CYAN, Color.LIGHT_GRAY)

        // layer
        val aes = AestheticsBuilder(count)
                .fill(array(*colors))
                //      .width(constant(0.75))
                .build()

        val keyElementFactory = GenericLegendKeyElementFactory()
        val legendBreaks = ArrayList<LegendBreak>()
        val labelsIterator = labels.iterator()
        for (aesthetics in aes.dataPoints()) {
            legendBreaks.add(LegendBreak.simple(labelsIterator.next(), aesthetics, keyElementFactory))
        }


        val legendComponent = LegendComponent.create("Simple legend", legendBreaks, theme.legend())
        legendComponent.debug = DEBUG_DRAWING

        val groupComponent = GroupComponent()
        groupComponent.add(legendComponent.rootGroup)
        return groupComponent
    }

    private fun colorBar(): GroupComponent {
        val count = 3
        val breaks = ArrayList<GuideBreak<Double>>()
        for (i in 0 until count) {
            val v = (i + 1).toDouble()
            val l = "" + v
            breaks.add(GuideBreak(v, l))
        }

        val domain = ClosedRange.closed(0.0, 4.0)

        val mapper = ColorMapper.gradientDefault(domain)
        val scale = Scales.continuousDomain("color", mapper, true)
                .with()
                .lowerLimit(domain.lowerEndpoint())
                .upperLimit(domain.upperEndpoint())
                .build()

        val legendComponent = ColorBarComponent.create("Color Bar", domain, breaks, scale, theme.legend())
        legendComponent.debug = DEBUG_DRAWING

        val groupComponent = GroupComponent()
        groupComponent.add(legendComponent.rootGroup)
        return groupComponent
    }

    companion object {
        private val DEBUG_DRAWING = true
    }

}
