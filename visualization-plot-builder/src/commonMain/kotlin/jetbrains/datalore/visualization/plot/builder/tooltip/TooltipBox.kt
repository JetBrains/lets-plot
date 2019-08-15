package jetbrains.datalore.visualization.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement.Visibility
import jetbrains.datalore.visualization.plot.base.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_WIDTH
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.H_TEXT_PADDING
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.V_TEXT_PADDING
import jetbrains.datalore.visualization.plot.builder.presentation.Style

class TooltipBox : SvgComponent() {
    private val myStemFootingLine: SvgLineElement = SvgLineElement()
    private val myStemArrow: SvgPathElement
    private val myContent = GroupComponent()
    private val myLines = GroupComponent()
    private val myBorder = SvgRectElement().apply {
        moveTo(DoubleVector.ZERO)
        addClass(Style.BACK)
        strokeWidth().set(BORDER_WIDTH)
        width().set(0.0)
        height().set(0.0)
    }

    var textColor: Color? = null; private set
    var fillColor: Color? = null; private set

    private var myTooltipCoord: DoubleVector? = null
    private var myObjectCoord: DoubleVector? = null
    private var myOrientation: Orientation? = null

    private val contentRect get() = myContent.rootGroup.bBox
    val contentSize get() = contentRect.dimension

    private val stemDirection: StemDirection
        get() {

            val tooltipRect = DoubleRectangle(myTooltipCoord!!, contentRect.dimension)

            val objectCoord = myObjectCoord!!
            if (myOrientation == Orientation.HORIZONTAL) {
                if (objectCoord.x > tooltipRect.right) {
                    return StemDirection.RIGHT
                }

                if (objectCoord.x < tooltipRect.left) {
                    return StemDirection.LEFT
                }
            }

            return if (objectCoord.y < tooltipRect.bottom) {
                StemDirection.UP
            } else StemDirection.DOWN

        }

    init {
        myStemFootingLine.strokeWidth().set(STEM_FOOTING_BORDER_WIDTH)

        myStemArrow = SvgPathElement()
        myStemArrow.strokeWidth().set(BORDER_WIDTH)

        myStemArrow.strokeColor().set(textColor)
        myStemArrow.fillColor().set(fillColor)
        myStemFootingLine.strokeColor().set(fillColor)
    }

    override fun buildComponent() {
        //myContent.add(myLines)
        //add(myBorder)
        //add(myStemFootingLine)
        //add(myStemArrow)
        //add(myContent)

        myContent.add(myBorder)
        myContent.add(myLines)
        add(myContent)
        add(myStemFootingLine)
        add(myStemArrow)
    }

    fun update(tooltilColor: Color, lines: List<String>, fontSize: Double) {
        //myTooltipBox.update(fillColor, text, fontSize)
        fillColor = alphaBlendColor(tooltilColor, Color.WHITE)
        textColor = fillColor?.let { computeContrastColor(it) }

        myBorder.apply {
            fillColor().set(fillColor)
            strokeColor().set(textColor)
        }

        lines
            .map {
                TextLabel(it).apply {
                    textColor().set(textColor)
                    setFontSize(fontSize)
                    myLines.add(this)
                }
            }
            .fold(0.0, { y, textLabel ->
                (y + textLabel.rootGroup.bBox.height).also {
                    textLabel.y().set(it)
                }
            })

        val textSize = myLines.rootGroup.bBox.dimension
        myLines.moveTo(H_TEXT_PADDING, V_TEXT_PADDING)
        myBorder.apply {
            width().set(textSize.x + H_TEXT_PADDING * 2)
            height().set(textSize.y + V_TEXT_PADDING * 2)
        }

        myStemArrow.strokeColor().set(textColor)
        myStemArrow.fillColor().set(fillColor)
        myStemFootingLine.strokeColor().set(fillColor)
        redrawTooltip()
    }

    fun moveTooltipTo(tooltipCoord: DoubleVector, objectCoord: DoubleVector, orientation: Orientation) {
        myTooltipCoord = tooltipCoord
        myObjectCoord = objectCoord
        myOrientation = orientation

        redrawTooltip()
    }

    private fun redrawTooltip() {
        if (myObjectCoord == null || myTooltipCoord == null || myOrientation == null) {
            return
        }

        moveTo(myTooltipCoord!!.x , myTooltipCoord!!.y)
        updateStem()
    }

    private fun updateStem() {
        val stemArrowTarget = DoubleVector(
                myObjectCoord!!.x - myTooltipCoord!!.x,
                myObjectCoord!!.y - myTooltipCoord!!.y
        )

        val showStem = !contentRect.contains(stemArrowTarget)
        updateStemVisibility(showStem)

        if (!showStem) {
            return
        }

        val stemFootingCoord0: DoubleVector
        val stemFootingCoord1: DoubleVector

        if (isHorizontal(stemDirection)) {
            val stemFootingX = when (stemDirection) {
                StemDirection.LEFT -> contentRect.origin.x
                StemDirection.RIGHT -> contentRect.right
                else -> error("Invalid horizontal direction")
            }

            val stemFootingIndent = calculateStemFootingIndent(contentRect.height)
            stemFootingCoord0 = DoubleVector(stemFootingX, contentRect.top + stemFootingIndent)
            stemFootingCoord1 = DoubleVector(stemFootingX, contentRect.bottom - stemFootingIndent)

        } else {
            val stemFootingY = when(stemDirection) {
                StemDirection.UP -> contentRect.top
                StemDirection.DOWN -> contentRect.bottom
                else -> error("Invalid vertical direction")
            }

            val stemFootingIndent = calculateStemFootingIndent(contentRect.width)
            stemFootingCoord0 = DoubleVector(contentRect.left + stemFootingIndent, stemFootingY)
            stemFootingCoord1 = DoubleVector(contentRect.right - stemFootingIndent, stemFootingY)
        }

        myStemFootingLine.apply {
            x1().set(stemFootingCoord0.x)
            y1().set(stemFootingCoord0.y)
            x2().set(stemFootingCoord1.x)
            y2().set(stemFootingCoord1.y)
        }

        myStemArrow.d().set(
            SvgPathDataBuilder()
                .moveTo(stemFootingCoord0)
                .lineTo(stemArrowTarget)
                .lineTo(stemFootingCoord1)
                .build()
        )
    }

    private fun updateStemVisibility(showStem: Boolean) {
        val visibility = if (showStem) Visibility.VISIBLE else Visibility.HIDDEN

        myStemFootingLine.visibility().set(visibility)
        myStemArrow.visibility().set(visibility)
    }

    private fun calculateStemFootingIndent(sideLength: Double): Double {
        return if (sideLength * STEM_FOOTING_TO_SIDE_LENGTH_RATIO > MAX_STEM_FOOTING_LENGTH) {
            (sideLength - MAX_STEM_FOOTING_LENGTH) / 2
        } else sideLength * SIDE_LENGTH_TO_STEM_FOOTING_RATIO

    }

    private fun isHorizontal(dir: StemDirection): Boolean {
        return dir == StemDirection.LEFT || dir == StemDirection.RIGHT
    }

    private enum class StemDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    companion object {
        private const val MAX_STEM_FOOTING_LENGTH = 12.0
        private const val STEM_FOOTING_TO_SIDE_LENGTH_RATIO = 0.4
        private const val SIDE_LENGTH_TO_STEM_FOOTING_RATIO = (1.0 - STEM_FOOTING_TO_SIDE_LENGTH_RATIO) / 2
        private const val STEM_FOOTING_BORDER_WIDTH = BORDER_WIDTH + 1
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

        private fun computeContrastColor(color: Color): Color {
            val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
            return when (luminance < FILL_COLOR_LUMINANCE_THRESHOLD) {
                true -> Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
                else -> Defaults.Common.Tooltip.DARK_TEXT_COLOR
            }
        }
    }
}
