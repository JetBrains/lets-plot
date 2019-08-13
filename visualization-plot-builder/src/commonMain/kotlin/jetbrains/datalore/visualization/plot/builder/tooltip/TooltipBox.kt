package jetbrains.datalore.visualization.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.base.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip
import jetbrains.datalore.visualization.plot.builder.presentation.Style

internal class TooltipBox : SvgComponent() {

    private val myFrame = SvgRectElement()
    private val myText = GroupComponent()
    var borderColor: Color? = null; private set
    var fillColor: Color? = null; private set
    var contentRect = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO); private set

    init {
        myFrame.addClass(Style.BACK)
        myFrame.stroke().set(STROKE_COLOR)
        myFrame.strokeWidth().set(STROKE_WIDTH)
        setFrameSize(0.0, 0.0, 0.0, 0.0)

        myText.rootGroup.addClass(Style.PLOT_TOOLTIP_TEXT)
    }

    override fun buildComponent() {
        add(myFrame)
        add(myText)
    }

    fun update(fillColor: Color, lines: List<String>, fontSize: Double) {
        val blendedFillColor = blendFillColor(fillColor)
        this.fillColor = blendedFillColor
        borderColor = getProperTextColor(blendedFillColor)

        myFrame.fillColor().set(this.fillColor)
        myFrame.strokeColor().set(borderColor)

        myText.clear()
        lines.mapIndexed {index, it ->
            TextLabel(it).apply {
                textColor().set(borderColor)
                setFontSize(fontSize)
                moveTo(0.0, (Tooltip.LINE_HEIGHT * index).toDouble())
            }
        }.forEach { myText.add(it) }


        var newWidth = 0.0
        var newHeight = 0.0
        var newX = 0.0
        var newY = 0.0

        if (lines.isNotEmpty()) {
            val textDim = myText.rootGroup.bBox.dimension
            val frameDim = textDim.add(DoubleVector(2 * H_PADDING, 2 * V_PADDING))
            newWidth = frameDim.x
            newHeight = frameDim.y

            newX = myText.rootGroup.bBox.origin.x - H_PADDING
            newY = myText.rootGroup.bBox.origin.y - V_PADDING
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
        return when (luminance < FILL_COLOR_LUMINANCE_THRESHOLD) {
            true -> Tooltip.LIGHT_TEXT_COLOR
            else -> Tooltip.DARK_TEXT_COLOR
        }
    }

    companion object {
        const val STROKE_WIDTH = 1.0

        private val FILL_COLOR_RGB = Color.GRAY
        private val STROKE_COLOR = SvgColors.WHITE
        private const val H_PADDING = 4.0
        private const val V_PADDING = 4.0
        private const val FILL_COLOR_LUMINANCE_THRESHOLD = 129.0

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
