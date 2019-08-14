package jetbrains.datalore.visualization.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_WIDTH
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.H_TEXT_PADDING
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.V_TEXT_PADDING
import jetbrains.datalore.visualization.plot.builder.presentation.Style

internal class TooltipBox : SvgComponent() {

    private val myText = SvgGElement()
    private val myFrame = SvgRectElement().apply {
        addClass(Style.BACK)
        strokeWidth().set(BORDER_WIDTH)
        width().set(0.0)
        height().set(0.0)
        x().set(0.0)
        y().set(0.0)
    }


    var textColor: Color? = null; private set
    var fillColor: Color? = null; private set
    var contentRect = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO); private set
    private val bBox: DoubleRectangle get() = myText.bBox

    override fun buildComponent() {
        add(myFrame)
        add(myText)
    }

    fun update(tooltilColor: Color, lines: List<String>, fontSize: Double) {
        if (lines.isEmpty()) {
            contentRect = DoubleRectangle(0.0, 0.0, 0.0, 0.0)
            return
        }

        fillColor = alphaBlendColor(tooltilColor, Color.WHITE)
        textColor = fillColor?.let { computeContrastColor(it) }

        myText.children().clear()
        lines.forEachIndexed { index, line ->
            myText.children().add(
                TextLabel(line).apply {
                    textColor().set(textColor)
                    setFontSize(fontSize)
                    moveTo(0.0, fontSize * index)
                }.rootGroup
            )
        }

        val frameDim = bBox.dimension.add(DoubleVector(2 * H_TEXT_PADDING, 2 * V_TEXT_PADDING))
        contentRect = DoubleRectangle(
            bBox.origin.x - H_TEXT_PADDING,
            bBox.origin.y - V_TEXT_PADDING,
            frameDim.x,
            frameDim.y
        )

        myFrame.apply {
            fillColor().set(fillColor)
            strokeColor().set(textColor)
            width().set(contentRect.width)
            height().set(contentRect.height)
            x().set(contentRect.left)
            y().set(contentRect.top)
        }
    }


    private fun computeContrastColor(color: Color): Color {
        val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
        return when (luminance < FILL_COLOR_LUMINANCE_THRESHOLD) {
            true -> Tooltip.LIGHT_TEXT_COLOR
            else -> Tooltip.DARK_TEXT_COLOR
        }
    }

    companion object {
        private val FILL_COLOR_RGB = Color.GRAY
        private val STROKE_COLOR = SvgColors.WHITE
        private const val FILL_COLOR_LUMINANCE_THRESHOLD = 129.0

        private fun alphaBlendColor(color: Color, backColor: Color): Color {
            val alpha = color.alpha / 255.0
            val r = blend(color.red.toDouble(), backColor.red.toDouble(), alpha)
            val g = blend(color.green.toDouble(), backColor.green.toDouble(), alpha)
            val b = blend(color.blue.toDouble(), backColor.blue.toDouble(), alpha)

            return Color(r, g, b)
        }

        private fun blend(firstComponent: Double, secondComponent: Double, alpha: Double): Int {
            val backgroundAlpha = 1.0 - alpha
            return (firstComponent * alpha + backgroundAlpha * secondComponent).toInt()
        }
    }
}
