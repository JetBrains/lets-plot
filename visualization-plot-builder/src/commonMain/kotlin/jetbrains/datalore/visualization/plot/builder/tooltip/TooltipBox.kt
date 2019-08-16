package jetbrains.datalore.visualization.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.visualization.base.svg.SvgPathDataBuilder
import jetbrains.datalore.visualization.base.svg.SvgPathElement
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_WIDTH
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.H_CONTENT_PADDING
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.LINE_INTERVAL
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.MAX_STEM_FOOTING_LENGTH
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.STEM_FOOTING_TO_SIDE_LENGTH_RATIO
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.V_CONTENT_PADDING
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox.Orientation.HORIZONTAL
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox.Orientation.VERTICAL
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox.StemDirection.*
import kotlin.math.max
import kotlin.math.min

class TooltipBox : SvgComponent() {
    val contentRect get() = DoubleRectangle.span(DoubleVector.ZERO, myTextBox.dimension)
    private val myPointerBox = PointerBox()
    private val myTextBox = TextBox()

    private var fontSize: Double = 12.0
    private var lines: List<String> = emptyList()
    private var textColor: Color = Color.BLACK
    private var fillColor: Color = Color.WHITE
    private var direction: StemDirection? = null
    private lateinit var stemCoord: DoubleVector

    override fun buildComponent() {
        add(myPointerBox)
        add(myTextBox)
    }

    fun setContent(background: Color, lines: List<String>, fontSize: Double) {
        this.fontSize = fontSize
        this.lines = lines
        fillColor = Colors.mimicTransparency(background, background.alpha / 255.0, Color.WHITE)
        textColor = LIGHT_TEXT_COLOR.takeIf { fillColor.isDark() } ?: DARK_TEXT_COLOR
        myTextBox.update()
    }

    fun setPosition(contentCoord: DoubleVector, stemCoord: DoubleVector, orientation: Orientation) {
        this.stemCoord = stemCoord.subtract(contentCoord)
        direction =
            null.takeIf { contentRect.contains(stemCoord) }
                ?: run {
                    val tooltip = DoubleRectangle(contentCoord, contentRect.dimension)
                    when (orientation) {
                        HORIZONTAL -> RIGHT.takeIf { stemCoord.x > tooltip.right } ?: LEFT
                        VERTICAL -> UP.takeIf { stemCoord.y < tooltip.bottom } ?: DOWN
                    }
                }

        myPointerBox.update()
        moveTo(contentCoord.x, contentCoord.y)
    }

    private fun Color.isDark() = Colors.luminance(this) < 0.5

    enum class StemDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    inner class PointerBox : SvgComponent() {
        private val myPath = SvgPathElement()

        override fun buildComponent() {
            add(myPath)
        }

        fun update() {
            myPath.strokeWidth().set(BORDER_WIDTH)
            myPath.strokeColor().set(textColor)
            myPath.fillColor().set(fillColor)

            val vertIndent = -calculateStemFootingIndent(contentRect.height)
            val horIndent = calculateStemFootingIndent(contentRect.width)

            myPath.d().set(
                SvgPathDataBuilder().apply {
                    with(contentRect) {
                        // start point
                        moveTo(right, bottom)

                        // right side
                        lineTo(right, bottom + vertIndent)
                        if (direction == RIGHT) lineTo(stemCoord)
                        lineTo(right, top - vertIndent)
                        lineTo(right, top)

                        // top side
                        lineTo(right - horIndent, top)
                        if (direction == UP) lineTo(stemCoord)
                        lineTo(left + horIndent, top)
                        lineTo(left, top)

                        // left side
                        lineTo(left, top - vertIndent)
                        if (direction == LEFT) lineTo(stemCoord)
                        lineTo(left, bottom + vertIndent)
                        lineTo(left, bottom)

                        // bottom
                        lineTo(left + horIndent, bottom)
                        if (direction == DOWN) lineTo(stemCoord)
                        lineTo(right - horIndent, bottom)
                        lineTo(right, bottom)
                    }
                }.build()
            )
        }

        private fun calculateStemFootingIndent(sideLength: Double): Double {
            val footingLength = min(sideLength * STEM_FOOTING_TO_SIDE_LENGTH_RATIO, MAX_STEM_FOOTING_LENGTH)
            return (sideLength - footingLength) / 2
        }
    }

    inner class TextBox : SvgComponent() {
        private val myLines = SvgSvgElement().apply {
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }
        private val myContent = SvgSvgElement().apply {
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }

        val dimension get() = myContent.run { DoubleVector(width().get()!!, height().get()!!) }

        override fun buildComponent() {
            myContent.children().add(myLines)
            add(myContent)
        }

        fun update() {
            val textSize = lines
                .map { TextLabel(it).apply { textColor().set(textColor); setFontSize(fontSize) } }
                .onEach { myLines.children().add(it.rootGroup) }
                .fold(DoubleVector.ZERO, { size, textLabel ->
                    val bBox = textLabel.rootGroup.bBox

                    // bBox.top is negative baseline of the text.
                    // Can't use bBox.height:
                    //  - in Batik close to the abs(bBox.top)
                    //  - in JavaFx is constant = fontSize
                    textLabel.y().set(size.y - bBox.top)

                    // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                    textLabel.x().set(-bBox.left)

                    DoubleVector(
                        max(size.x, bBox.width),
                        textLabel.y().get()!! + LINE_INTERVAL
                    )
                })
                .subtract(DoubleVector(0.0, LINE_INTERVAL)) // remove LINE_INTERVAL from last line

            myLines.apply {
                x().set(H_CONTENT_PADDING)
                y().set(V_CONTENT_PADDING)
                width().set(textSize.x)
                height().set(textSize.y)
            }

            myContent.apply {
                width().set(textSize.x + H_CONTENT_PADDING * 2)
                height().set(textSize.y + V_CONTENT_PADDING * 2)
            }
        }
    }
}

