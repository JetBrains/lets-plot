/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.component

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.array
import org.jetbrains.letsPlot.core.plot.base.geom.legend.GenericLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.assemble.ColorBarAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendAssembler
import org.jetbrains.letsPlot.core.plot.builder.guide.ColorBarComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.LegendBreak
import org.jetbrains.letsPlot.core.plot.builder.guide.LegendComponent
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapper

open class LegendDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
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
            .fill(array(colors))
            //      .width(constant(0.75))
            .build()

        val keyElementFactory =
            GenericLegendKeyElementFactory()
        val legendBreaks = ArrayList<LegendBreak>()
        val labelsIterator = labels.iterator()
        for (aesthetics in aes.dataPoints()) {
            legendBreaks.add(LegendBreak.simple(labelsIterator.next(), aesthetics, keyElementFactory))
        }

        val spec = LegendAssembler.createLegendSpec("Simple legend", legendBreaks, theme.legend())
        val legendComponent = LegendComponent(spec)
        legendComponent.debug = DEBUG_DRAWING

        val groupComponent = GroupComponent()
        groupComponent.add(legendComponent.rootGroup)
        return groupComponent
    }

    private fun colorBar(): GroupComponent {
        val domain = DoubleSpan(0.0, 4.0)

        val mapper = ScaleMapper.wrap(ColorMapper.gradientDefault(domain))

        val breakValues = List(3) { i -> (i + 1).toDouble() }
        val scaleBreaks = ScaleBreaks(breakValues, breakValues, breakValues.map { "$it" })
        val spec = ColorBarAssembler.createColorBarSpec(
            "Color Bar", domain, scaleBreaks,
            mapper,
            theme.legend()
        )
        val legendComponent = ColorBarComponent(spec)
        legendComponent.debug = DEBUG_DRAWING

        val groupComponent = GroupComponent()
        groupComponent.add(legendComponent.rootGroup)
        return groupComponent
    }

    companion object {
        private const val DEBUG_DRAWING = true
    }
}
