package jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.plot.gog.core.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Defaults
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Defaults.Common.Tooltip
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style

internal class TooltipFrame : SvgComponent() {

    private val myFrame: SvgRectElement
    private val myText: SvgTextElement
    var borderColor: SvgColor? = null
        private set
    var fillColor: SvgColor? = null
        private set
    var contentRect = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        private set

    init {
        myFrame = SvgRectElement()
        myFrame.addClass(Style.BACK)
        myFrame.stroke().set(STROKE_COLOR)
        myFrame.strokeWidth().set(STROKE_WIDTH)
        setFrameSize(0.0, 0.0, 0.0, 0.0)

        myText = SvgTextElement()
        myText.x().set(H_PADDING)
        myText.y().set(V_PADDING)
    }

    override fun buildComponent() {
        add(myFrame)
        add(myText)
    }

    fun update(fillColor: Color, lines: List<String>, fontSize: Double) {
        val blendedFillColor = blendFillColor(fillColor)
        this.fillColor = SvgColors.create(blendedFillColor)
        myFrame.fill().set(this.fillColor)

        val textColor = getProperTextColor(blendedFillColor)
        borderColor = SvgColors.create(textColor)
        myFrame.stroke().set(borderColor)

        myText.children().clear()

        for (line in lines) {
            val tspan = SvgTSpanElement()
            tspan.setText(line)
            tspan.x().set(H_PADDING)
            tspan.setAttribute("dy", Defaults.Common.Tooltip.LINE_HEIGHT_CSS)
            tspan.setAttribute("font-size", fontSize.toString())
            tspan.fillColor().set(textColor)

            myText.children().add(tspan)
        }

        var newWidth = 0.0
        var newHeight = 0.0
        var newX = 0.0
        var newY = 0.0

        if (!lines.isEmpty()) {
            val textDim = myText.bBox.dimension
            val frameDim = textDim.add(DoubleVector(2 * H_PADDING, 2 * V_PADDING))
            newWidth = frameDim.x
            newHeight = frameDim.y

            newX = myText.bBox.origin.x - H_PADDING
            newY = myText.bBox.origin.y - V_PADDING
        }

        setFrameSize(newWidth, newHeight, newX, newY)
    }

    private fun setFrameSize(newWidth: Double, newHeight: Double, newX: Double, newY: Double) {
        myFrame.width().set(newWidth)
        myFrame.height().set(newHeight)
        myFrame.x().set(newX)
        myFrame.y().set(newY)
        contentRect = DoubleRectangle(newX, newY, newWidth, newHeight)
    }

    private fun getProperTextColor(tooltipFillColor: Color): Color {
        val luminance = 0.299 * tooltipFillColor.red + 0.587 * tooltipFillColor.green + 0.114 * tooltipFillColor.blue
        return if (luminance < FILL_COLOR_LUMINANCE_THRESHOLD) {
            Tooltip.LIGHT_TEXT_COLOR
        } else {
            Tooltip.DARK_TEXT_COLOR
        }
    }

    companion object {
        val STROKE_WIDTH = 1.0

        private val FILL_COLOR_RGB = Color.GRAY
        private val STROKE_COLOR = SvgColors.WHITE
        private val H_PADDING = 4.0
        private val V_PADDING = 4.0
        private val FILL_COLOR_LUMINANCE_THRESHOLD = 129.0

        private fun blendFillColor(color: Color): Color {
            val alpha = getFillAlpha(color)
            val backColor = Color.WHITE
            val r = blend(color.red.toDouble(), backColor.red.toDouble(), alpha)
            val g = blend(color.green.toDouble(), backColor.green.toDouble(), alpha)
            val b = blend(color.blue.toDouble(), backColor.blue.toDouble(), alpha)

            return Color(r, g, b)
        }

        private fun getFillAlpha(color: Color): Double {
            return if (color.alpha == 0) {
                0.0
            } else color.alpha / 255.0

        }

        private fun blend(firstComponent: Double, secondComponent: Double, alpha: Double): Int {
            val backgroundAlpha = 1.0 - alpha
            return (firstComponent * alpha + backgroundAlpha * secondComponent).toInt()
        }
    }
}
