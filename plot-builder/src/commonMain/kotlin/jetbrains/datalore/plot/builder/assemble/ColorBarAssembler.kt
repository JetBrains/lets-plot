/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.theme.LegendTheme

class ColorBarAssembler(private val legendTitle: String,
                        private val domain: ClosedRange<Double>,
                        private val scale: Scale<Color>,
                        private val theme: LegendTheme) {

    private var myOptions: ColorBarOptions? = null

    fun createColorBar(): LegendBoxInfo {
        var scale = scale
        if (!scale.hasBreaks()) {
            scale = ScaleBreaksUtil.withBreaks(scale, domain, 5)
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

        val spec = jetbrains.datalore.plot.builder.assemble.ColorBarAssembler.Companion.createColorBarSpec(
            legendTitle,
            domain,
            guideBreaks,
            scale,
            theme,
            myOptions
        )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): jetbrains.datalore.plot.builder.guide.LegendBox {
                val c = jetbrains.datalore.plot.builder.guide.ColorBarComponent(spec)
                c.debug = jetbrains.datalore.plot.builder.assemble.ColorBarAssembler.Companion.DEBUG_DRAWING
                return c
            }
        }
    }

    internal fun setOptions(options: ColorBarOptions?) {
        myOptions = options
    }

    companion object {
        private const val DEBUG_DRAWING = jetbrains.datalore.plot.FeatureSwitch.LEGEND_DEBUG_DRAWING

        fun createColorBarSpec(title: String,
                               domain: ClosedRange<Double>,
                               breaks: List<GuideBreak<Double>>,
                               scale: Scale<Color>,
                               theme: LegendTheme,
                               options: ColorBarOptions? = null): jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec {

            val legendDirection = jetbrains.datalore.plot.builder.assemble.LegendAssemblerUtil.legendDirection(theme)

            val width = options?.width
            val height = options?.height
            var barSize = jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.barAbsoluteSize(legendDirection, theme)
            if (width != null) {
                barSize = DoubleVector(width, barSize.y)
            }
            if (height != null) {
                barSize = DoubleVector(barSize.x, height)
            }

            val layout = when {
                legendDirection === jetbrains.datalore.plot.builder.guide.LegendDirection.HORIZONTAL ->
                    jetbrains.datalore.plot.builder.guide.ColorBarComponentLayout.horizontal(title, domain, breaks, barSize)
                else ->
                    jetbrains.datalore.plot.builder.guide.ColorBarComponentLayout.vertical(title, domain, breaks, barSize)
            }

            val spec =
                jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec(title, domain, breaks, scale, theme, layout)
            val binCount = options?.binCount
            if (binCount != null) {
                spec.binCount = binCount
            }

            return spec
        }
    }
}
