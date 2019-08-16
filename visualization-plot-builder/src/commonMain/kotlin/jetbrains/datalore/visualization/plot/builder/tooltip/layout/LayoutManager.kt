package jetbrains.datalore.visualization.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.NORMAL_STEM_LENGTH
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.SHORT_STEM_LENGTH
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP
import kotlin.math.min

class LayoutManager(private val myViewport: DoubleRectangle, private val myPreferredHorizontalAlignment: HorizontalAlignment) {
    private val myHorizontalSpace: DoubleRange = DoubleRange.withStartAndEnd(myViewport.left, myViewport.right)
    private var myVerticalSpace: DoubleRange = DoubleRange.withStartAndEnd(0.0, 0.0)
    private var myCursorCoord: DoubleVector = DoubleVector.ZERO
    private var myVerticalAlignmentResolver: VerticalAlignmentResolver? = null

    fun arrange(tooltips: List<MeasuredTooltip>, cursorCoord: DoubleVector): List<PositionedTooltip> {
        myCursorCoord = cursorCoord
        myVerticalSpace = DoubleRange.withStartAndEnd(myViewport.top, myViewport.bottom)
        myVerticalAlignmentResolver = VerticalAlignmentResolver(myVerticalSpace)

        val desiredPosition = ArrayList<PositionedTooltip>()

        // x-axis tooltip
        tooltips
            .firstOrNull { it.hintKind === X_AXIS_TOOLTIP }
            ?.let { xAxisTooltip ->
                val positionedTooltip = calculateVerticalTooltipPosition(xAxisTooltip, BOTTOM, SHORT_STEM_LENGTH, true)
                desiredPosition.add(positionedTooltip)

                // Limit available vertical space for other tooltips by the axis or top side of the tooltip (if not fit under the axis)
                myVerticalSpace = DoubleRange.withStartAndEnd(
                    myViewport.top,
                    min(
                        positionedTooltip.stemCoord.y,
                        positionedTooltip.top
                    )
                )
                myVerticalAlignmentResolver = VerticalAlignmentResolver(myVerticalSpace)
            }

        // y-axis tooltip
        tooltips
            .firstOrNull { it.hintKind === Y_AXIS_TOOLTIP }
            ?.let { desiredPosition.add(calculateHorizontalTooltipPosition(it, SHORT_STEM_LENGTH)) }

        // all other tooltips (axis tooltips are ignored in this method)
        desiredPosition.addAll(calculateDesiredPosition(tooltips))

        return rearrangeWithoutOverlapping(desiredPosition)
    }

    private fun calculateDesiredPosition(tooltips: List<MeasuredTooltip>): List<PositionedTooltip> {
        val placementList = ArrayList<PositionedTooltip>()

        for (measuredTooltip in tooltips) {
            when (measuredTooltip.hintKind) {
                VERTICAL_TOOLTIP -> placementList.add(calculateVerticalTooltipPosition(measuredTooltip, TOP, NORMAL_STEM_LENGTH, false))

                HORIZONTAL_TOOLTIP -> placementList.add(calculateHorizontalTooltipPosition(measuredTooltip, NORMAL_STEM_LENGTH))

                CURSOR_TOOLTIP -> placementList.add(calculateCursorTooltipPosition(measuredTooltip))

                X_AXIS_TOOLTIP ->
                    // X_AXIS should be processed separately to configure vertical space. If process axis tooltip with narrowed space
                    // Y coords will be moved too far from axis.
                    Unit

                Y_AXIS_TOOLTIP ->
                    // Y_AXIS should be processed separately to configure horizontal space. If process axis tooltip with narrowed space
                    // X coords will be moved too far from axis.
                    Unit
            }
        }

        return placementList
    }

    private fun rearrangeWithoutOverlapping(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        if (tooltips.isEmpty()) {
            return tooltips
        }

        val restrictions = ArrayList<DoubleRectangle>()
        val separatedTooltips = ArrayList<PositionedTooltip>()

        val fixate: (PositionedTooltip) -> Unit = { positionedTooltip ->
            separatedTooltips.add(positionedTooltip)
            restrictions.add(positionedTooltip.rect())
        }

        // First add tooltips with pre-arranged position
        tooltips.select(CURSOR_TOOLTIP, X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP).forEach(fixate)

        // Now try to space out other tooltips.
        // Order matters - vertical tooltips should be added last, because it's easier to space them out.
        HorizontalTooltipExpander(myVerticalSpace)
                .fixOverlapping(tooltips.select(HORIZONTAL_TOOLTIP))
                .forEach(fixate)

        VerticalTooltipRotatingExpander(myVerticalSpace, myHorizontalSpace)
                .fixOverlapping(tooltips.select(VERTICAL_TOOLTIP), restrictions)
                .forEach(fixate)

        return separatedTooltips
    }

    private fun calculateVerticalTooltipPosition(measuredTooltip: MeasuredTooltip, alignment: VerticalAlignment,
                                                 stemLength: Double, ignoreCursor: Boolean): PositionedTooltip {
        val tooltipX = centerInsideRange(measuredTooltip.hintCoord.x, measuredTooltip.size.x, myHorizontalSpace)

        val stemY: Double
        val tooltipY: Double
        run {
            val targetCoordY = measuredTooltip.hintCoord.y

            val targetTopPoint = targetCoordY - measuredTooltip.hintRadius
            val targetBottomPoint = targetCoordY + measuredTooltip.hintRadius

            val tooltipHeight = measuredTooltip.size.y
            val topTooltipRange = leftAligned(targetTopPoint, tooltipHeight, stemLength)
            val bottomTooltipRange = rightAligned(targetBottomPoint, tooltipHeight, stemLength)

            val cursorVerticalRange = if (!ignoreCursor && overlapsCursorHorizontalRange(measuredTooltip, tooltipX))
                DoubleRange.withStartAndLength(myCursorCoord.y, CURSOR_DIMENSION.y)
            else
                EMPTY_DOUBLE_RANGE

            if (myVerticalAlignmentResolver!!.resolve(topTooltipRange, bottomTooltipRange, alignment, cursorVerticalRange) === TOP) {
                tooltipY = topTooltipRange.start()
                stemY = targetTopPoint
            } else {
                tooltipY = bottomTooltipRange.start()
                stemY = targetBottomPoint
            }
        }

        val stemCoord = DoubleVector(measuredTooltip.hintCoord.x, stemY)
        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, stemCoord)
    }

    private fun calculateHorizontalTooltipPosition(measuredTooltip: MeasuredTooltip, stemLength: Double): PositionedTooltip {
        val tooltipY = centerInsideRange(measuredTooltip.hintCoord.y, measuredTooltip.size.y, myVerticalSpace)

        val tooltipX: Double
        val stemX: Double
        run {
            val targetCoordX = measuredTooltip.hintCoord.x
            val tooltipWidth = measuredTooltip.size.x
            val hintSize = measuredTooltip.hintRadius
            val margin = hintSize + stemLength

            val leftTooltipPlacement = leftAligned(targetCoordX, tooltipWidth, margin)
            val rightTooltipPlacement = rightAligned(targetCoordX, tooltipWidth, margin)

            var canFitLeft = false
            var canFitRight = false
            if (leftTooltipPlacement.inside(myHorizontalSpace)) {
                canFitLeft = true
            }
            if (rightTooltipPlacement.inside(myHorizontalSpace)) {
                canFitRight = true
            }

            if (!(canFitLeft || canFitRight)) {
                tooltipX = 0.0
                stemX = targetCoordX
            } else if (myPreferredHorizontalAlignment == HorizontalAlignment.LEFT && canFitLeft || !canFitRight) {
                tooltipX = leftTooltipPlacement.start()
                stemX = targetCoordX - hintSize
            } else {
                tooltipX = rightTooltipPlacement.start()
                stemX = targetCoordX + hintSize
            }
        }

        val stemCoord = DoubleVector(stemX, measuredTooltip.hintCoord.y)
        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, stemCoord)
    }

    private fun calculateCursorTooltipPosition(measuredTooltip: MeasuredTooltip): PositionedTooltip {
        val tooltipX = centerInsideRange(myCursorCoord.x, measuredTooltip.size.x, myHorizontalSpace)

        val targetCoordY = myCursorCoord.y
        val tooltipHeight = measuredTooltip.size.y
        val verticalMargin = NORMAL_STEM_LENGTH

        val topTooltipPlacement = leftAligned(targetCoordY, tooltipHeight, verticalMargin)
        val bottomTooltipPlacement = rightAligned(targetCoordY, tooltipHeight, verticalMargin)

        val tooltipY = if (topTooltipPlacement.inside(myVerticalSpace)) {
            topTooltipPlacement.start()
        } else {
            bottomTooltipPlacement.start()
        }

        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, myCursorCoord)
    }

    private fun overlapsCursorHorizontalRange(measuredTooltip: MeasuredTooltip, tooltipX: Double): Boolean {
        val horizontalTooltipRange = DoubleRange.withStartAndLength(tooltipX, measuredTooltip.size.x)
        val cursorHorizontalRange = DoubleRange.withStartAndLength(myCursorCoord.x, CURSOR_DIMENSION.x)
        return horizontalTooltipRange.overlaps(cursorHorizontalRange)
    }


    internal enum class VerticalAlignment {
        TOP,
        BOTTOM
    }

    enum class HorizontalAlignment {
        LEFT,
        RIGHT,
        CENTER
    }

    class PositionedTooltip {
        internal val tooltipSize: DoubleVector
        val tooltipSpec: TooltipSpec
        val tooltipCoord: DoubleVector
        val stemCoord: DoubleVector

        internal val left get() = tooltipCoord.x
        internal val top get() = tooltipCoord.y
        internal val width get() = tooltipSize.x
        internal val height get() = tooltipSize.y
        internal val bottom get() = tooltipCoord.y + height
        internal val right get() = tooltipCoord.x + width
        internal val hintKind get() = tooltipSpec.layoutHint.kind

        internal constructor(measuredTooltip: MeasuredTooltip, tooltipCoord: DoubleVector, stemCoord: DoubleVector) {
            tooltipSpec = measuredTooltip.tooltipSpec
            tooltipSize = measuredTooltip.size
            this.tooltipCoord = tooltipCoord
            this.stemCoord = stemCoord
        }

        private constructor(positionedTooltip: PositionedTooltip, newTooltipCoord: DoubleVector) {
            tooltipSpec = positionedTooltip.tooltipSpec
            tooltipCoord = newTooltipCoord
            tooltipSize = positionedTooltip.tooltipSize
            stemCoord = positionedTooltip.stemCoord
        }

        internal fun moveTo(newTooltipCoord: DoubleVector): PositionedTooltip {
            return PositionedTooltip(this, newTooltipCoord)
        }

        internal fun rect(): DoubleRectangle {
            return DoubleRectangle(tooltipCoord, tooltipSize)
        }
    }

    class MeasuredTooltip(
        internal val tooltipSpec: TooltipSpec,
        internal val size: DoubleVector
    ) {
        internal val hintCoord get() = tooltipSpec.layoutHint.coord!!
        internal val hintKind get() = tooltipSpec.layoutHint.kind
        internal val hintRadius get() = tooltipSpec.layoutHint.objectRadius
    }

    companion object {

        private val CURSOR_DIMENSION = DoubleVector(10.0, 10.0)
        private val EMPTY_DOUBLE_RANGE = DoubleRange.withStartAndLength(0.0, 0.0)

        internal fun moveIntoLimit(range: DoubleRange, limit: DoubleRange): DoubleRange {
            if (range.inside(limit)) {
                return range
            }

            if (range.start() < limit.start()) {
                return range.move(limit.start() - range.start())
            }

            return if (range.end() > limit.end()) {
                range.move(limit.end() - range.end())
            } else range

        }

        private fun centered(start: Double, length: Double): DoubleRange {
            return DoubleRange.withStartAndLength(start - length / 2, length)
        }

        private fun leftAligned(start: Double, length: Double, margin: Double): DoubleRange {
            return DoubleRange.withStartAndLength(start - length - margin, length)
        }

        private fun rightAligned(start: Double, length: Double, margin: Double): DoubleRange {
            return DoubleRange.withStartAndLength(start + margin, length)
        }

        private fun centerInsideRange(position: Double, size: Double, range: DoubleRange): Double {
            return moveIntoLimit(centered(position, size), range).start()
        }

        private fun List<PositionedTooltip>.select(vararg kinds: Kind): List<PositionedTooltip> {
            val filteredTooltips = ArrayList<PositionedTooltip>()

            for (tooltip in this) {
                for (kind in kinds) {
                    if (tooltip.hintKind === kind) {
                        filteredTooltips.add(tooltip)
                    }
                }
            }
            return filteredTooltips
        }
    }
}
