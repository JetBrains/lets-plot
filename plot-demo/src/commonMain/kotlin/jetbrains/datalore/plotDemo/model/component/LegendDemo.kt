/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.plot.base.geom.legend.GenericLegendKeyElementFactory
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.builder.assemble.ColorBarAssembler
import jetbrains.datalore.plot.builder.assemble.LegendAssembler
import jetbrains.datalore.plot.builder.guide.ColorBarComponent
import jetbrains.datalore.plot.builder.guide.LegendBreak
import jetbrains.datalore.plot.builder.guide.LegendComponent
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

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
        val count = 3
        val breaks = ArrayList<GuideBreak<Double>>()
        for (i in 0 until count) {
            val v = (i + 1).toDouble()
            val l = "" + v
            breaks.add(GuideBreak(v, l))
        }

        val domain = ClosedRange(0.0, 4.0)

        val mapper = ColorMapper.gradientDefault(domain)
        val scale = Scales.continuousDomain("color", mapper, true)
                .with()
                .lowerLimit(domain.lowerEndpoint())
                .upperLimit(domain.upperEndpoint())
                .build()

        val spec = ColorBarAssembler.createColorBarSpec("Color Bar", domain, breaks, scale, theme.legend())
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
