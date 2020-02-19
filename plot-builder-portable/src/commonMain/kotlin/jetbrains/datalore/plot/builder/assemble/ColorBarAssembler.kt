/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.guide.ColorBarComponent
import jetbrains.datalore.plot.builder.guide.ColorBarComponentLayout
import jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec
import jetbrains.datalore.plot.builder.guide.LegendBox
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

        val spec =
            createColorBarSpec(
                legendTitle,
                domain,
                guideBreaks,
                scale,
                theme,
                myOptions
            )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = ColorBarComponent(spec)
                c.debug =
                    DEBUG_DRAWING
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
                               options: ColorBarOptions? = null): ColorBarComponentSpec {

            val legendDirection =
                LegendAssemblerUtil.legendDirection(theme)

            val width = options?.width
            val height = options?.height
            var barSize = ColorBarComponentSpec.barAbsoluteSize(legendDirection, theme)
            if (width != null) {
                barSize = DoubleVector(width, barSize.y)
            }
            if (height != null) {
                barSize = DoubleVector(barSize.x, height)
            }

            val layout = when {
                legendDirection === jetbrains.datalore.plot.builder.guide.LegendDirection.HORIZONTAL ->
                    ColorBarComponentLayout.horizontal(title, domain, breaks, barSize)
                else ->
                    ColorBarComponentLayout.vertical(title, domain, breaks, barSize)
            }

            val spec =
                ColorBarComponentSpec(
                    title,
                    domain,
                    breaks,
                    scale,
                    theme,
                    layout
                )
            val binCount = options?.binCount
            if (binCount != null) {
                spec.binCount = binCount
            }

            return spec
        }
    }
}
