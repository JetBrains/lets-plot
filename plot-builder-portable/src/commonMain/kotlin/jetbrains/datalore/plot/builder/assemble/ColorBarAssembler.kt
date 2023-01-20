/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.guide.*
import jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_NUM_BIN
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.theme.LegendTheme

class ColorBarAssembler(
    private val legendTitle: String,
    private val transformedDomain: DoubleSpan,
    private val scale: Scale,
    private val scaleMapper: ScaleMapper<Color>,
    private val theme: LegendTheme
) {

    private var colorBarOptions: ColorBarOptions? = null

    fun createColorBar(): LegendBoxInfo {
        var scale = scale
        if (!scale.hasBreaks()) {
            scale = ScaleBreaksUtil.withBreaks(scale, transformedDomain, 5)
        }

        val scaleBreaks = scale.getScaleBreaks()
        if (scaleBreaks.isEmpty) {
            return LegendBoxInfo.EMPTY
        }

        val spec = createColorBarSpec(
            legendTitle,
            transformedDomain,
            scaleBreaks,
            scaleMapper,
            theme,
            colorBarOptions
        )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = ColorBarComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }

    internal fun setOptions(options: ColorBarOptions?) {
        colorBarOptions = options
    }

    companion object {
        private const val DEBUG_DRAWING = jetbrains.datalore.plot.FeatureSwitch.LEGEND_DEBUG_DRAWING

        fun createColorBarSpec(
            title: String,
            transformedDomain: DoubleSpan,
            breaks: ScaleBreaks,
            scaleMapper: ScaleMapper<Color>,
            theme: LegendTheme,
            options: ColorBarOptions? = null
        ): ColorBarComponentSpec {

            val legendDirection = LegendAssemblerUtil.legendDirection(theme)
            val horizontal: Boolean = legendDirection == LegendDirection.HORIZONTAL

            val width = options?.width
            val height = options?.height
            var barSize = ColorBarComponentSpec.barAbsoluteSize(horizontal, theme)
            if (width != null) {
                barSize = DoubleVector(width, barSize.y)
            }
            if (height != null) {
                barSize = DoubleVector(barSize.x, height)
            }

            val reverse = !horizontal

            val layout = when {
                horizontal -> ColorBarComponentLayout.horizontal(
                    title,
                    transformedDomain,
                    breaks,
                    barSize,
                    reverse,
                    theme
                )

                else -> ColorBarComponentLayout.vertical(title, transformedDomain, breaks, barSize, reverse, theme)
            }

            return ColorBarComponentSpec(
                title,
                transformedDomain,
                breaks,
                scaleMapper,
                binCount = options?.binCount ?: DEF_NUM_BIN,
                theme,
                layout,
                reverse
            )
        }
    }
}
