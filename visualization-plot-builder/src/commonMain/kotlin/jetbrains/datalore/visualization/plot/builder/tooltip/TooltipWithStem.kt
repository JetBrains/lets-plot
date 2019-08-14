package jetbrains.datalore.visualization.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement.Visibility
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.base.svg.SvgPathDataBuilder
import jetbrains.datalore.visualization.base.svg.SvgPathElement
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_WIDTH

class TooltipWithStem : SvgComponent() {
    private val myTooltipBox: TooltipBox = TooltipBox()
    private val myStemFootingLine: SvgLineElement = SvgLineElement()
    private val myStemArrow: SvgPathElement
    private var myTooltipCoord: DoubleVector? = null
    private var myObjectCoord: DoubleVector? = null
    private var myOrientation: Orientation? = null

    val contentRect get() = myTooltipBox.contentRect

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

        setFrameColors()
    }

    override fun buildComponent() {
        add(myTooltipBox)
        add(myStemFootingLine)
        add(myStemArrow)
    }

    fun update(fillColor: Color, text: List<String>, fontSize: Double) {
        myTooltipBox.update(fillColor, text, fontSize)

        setFrameColors()
        redrawTooltip()
    }

    private fun setFrameColors() {
        myStemArrow.strokeColor().set(myTooltipBox.textColor)
        myStemArrow.fillColor().set(myTooltipBox.fillColor)
        myStemFootingLine.strokeColor().set(myTooltipBox.fillColor)
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

        moveTo(myTooltipCoord!!.x - contentRect.origin.x, myTooltipCoord!!.y - contentRect.origin.y)
        updateStem()
    }

    private fun updateStem() {
        val stemArrowTarget = DoubleVector(
                myObjectCoord!!.x - myTooltipCoord!!.x + contentRect.origin.x,
                myObjectCoord!!.y - myTooltipCoord!!.y + contentRect.origin.y
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
    }
}
