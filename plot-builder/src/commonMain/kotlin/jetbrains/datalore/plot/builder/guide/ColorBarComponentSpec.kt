package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.visualization.plot.base.Scale

class ColorBarComponentSpec(title: String,
                            internal val domain: ClosedRange<Double>,
                            internal val breaks: List<GuideBreak<Double>>,
                            internal val scale: Scale<Color>,
                            theme: LegendTheme,
                            override val layout: jetbrains.datalore.plot.builder.guide.ColorBarComponentLayout
) : jetbrains.datalore.plot.builder.guide.LegendBoxSpec(title, theme) {

    internal var binCount = jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_NUM_BIN

    companion object {
        private const val DEF_BAR_THICKNESS = 1.0  // in 'key-size' multiples
        private const val DEF_BAR_LENGTH = 5.0   // in 'key-size' multiples

        private const val DEF_NUM_BIN = 20

        internal fun barAbsoluteSize(legendDirection: jetbrains.datalore.plot.builder.guide.LegendDirection, theme: LegendTheme): DoubleVector {
            return if (legendDirection === jetbrains.datalore.plot.builder.guide.LegendDirection.HORIZONTAL) {
                DoubleVector(
                        jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_BAR_LENGTH * theme.keySize(),
                        jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_BAR_THICKNESS * theme.keySize())
            } else DoubleVector(
                    jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_BAR_THICKNESS * theme.keySize(),
                    jetbrains.datalore.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_BAR_LENGTH * theme.keySize())
        }
    }
}
