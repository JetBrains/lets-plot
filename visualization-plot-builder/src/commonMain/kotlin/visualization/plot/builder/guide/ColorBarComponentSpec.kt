package jetbrains.datalore.visualization.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.scale.Scale2
import jetbrains.datalore.visualization.plot.builder.scale.GuideBreak
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

class ColorBarComponentSpec(title: String, internal val domain: ClosedRange<Double>, internal val breaks: List<GuideBreak<Double>>, internal val scale: Scale2<Color>, private val myTheme: LegendTheme) : LegendBoxSpec(title, myTheme) {

    internal var binCount = DEF_NUM_BIN
    private var myBarWidth = Double.NaN
    private var myRodHeight = Double.NaN
    private var myLayout: ColorBarComponentLayout? = null

    override val contentSize: DoubleVector
        get() = layout.size

    override val layout: ColorBarComponentLayout
        get() {
            ensureInited()
            return myLayout!!
        }

    private fun ensureInited() {
        if (myLayout == null) {
            // no setters after this point
            val legendDirection = legendDirection
            var barSize = barAbsoluteSize(legendDirection, myTheme)
            if (!myBarWidth.isNaN()) {
                barSize = DoubleVector(myBarWidth, barSize.y)
            }
            if (!myRodHeight.isNaN()) {
                barSize = DoubleVector(barSize.x, myRodHeight)
            }
            if (legendDirection === LegendDirection.HORIZONTAL) {
                myLayout = ColorBarComponentLayout.horizontal(title, domain, breaks, barSize)
            } else {
                myLayout = ColorBarComponentLayout.vertical(title, domain, breaks, barSize)
            }
        }
    }

    fun setBarWidth(barWidth: Double) {
        myBarWidth = barWidth
    }

    fun setRodHeight(rodHeight: Double) {
        myRodHeight = rodHeight
    }

    companion object {
        private val DEF_BAR_THICKNESS = 1.0  // in 'key-size' multiples
        private val DEF_BAR_LENGTH = 5.0   // in 'key-size' multiples

        private val DEF_NUM_BIN = 20

        private fun barAbsoluteSize(legendDirection: LegendDirection, theme: LegendTheme): DoubleVector {
            return if (legendDirection === LegendDirection.HORIZONTAL) {
                DoubleVector(
                        DEF_BAR_LENGTH * theme.keySize(),
                        DEF_BAR_THICKNESS * theme.keySize())
            } else DoubleVector(
                    DEF_BAR_THICKNESS * theme.keySize(),
                    DEF_BAR_LENGTH * theme.keySize())
        }
    }
}
