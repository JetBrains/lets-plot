/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.plot.builder.guide.TooltipAnchor
import jetbrains.datalore.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.NORMAL_STEM_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_STEM_LENGTH
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP
import kotlin.math.min

class LayoutManager(
    private val myViewport: DoubleRectangle,
    private val myPreferredHorizontalAlignment: HorizontalAlignment,
    private val myTooltipAnchor: TooltipAnchor
) {
    private val myHorizontalSpace: DoubleRange = DoubleRange.withStartAndEnd(myViewport.left, myViewport.right)
    private var myVerticalSpace: DoubleRange = DoubleRange.withStartAndEnd(0.0, 0.0)
    private var myCursorCoord: DoubleVector = DoubleVector.ZERO
    private var myHorizontalGeomSpace = DoubleRange.withStartAndEnd(myViewport.left, myViewport.right)
    private var myVerticalGeomSpace = DoubleRange.withStartAndEnd(myViewport.top, myViewport.bottom)
    private lateinit var myVerticalAlignmentResolver: VerticalAlignmentResolver

    fun arrange(tooltips: List<MeasuredTooltip>, cursorCoord: DoubleVector, geomBounds: DoubleRectangle?): List<PositionedTooltip> {
        myCursorCoord = cursorCoord
        myVerticalSpace = DoubleRange.withStartAndEnd(myViewport.top, myViewport.bottom)
        myVerticalAlignmentResolver = VerticalAlignmentResolver(myVerticalSpace)
        if (geomBounds != null) {
            myHorizontalGeomSpace = DoubleRange.withStartAndLength(geomBounds.origin.x, geomBounds.dimension.x)
            myVerticalGeomSpace = DoubleRange.withStartAndLength(geomBounds.origin.y, geomBounds.dimension.y)
        }

        val desiredPosition = ArrayList<PositionedTooltip>()

        // x-axis tooltip
        tooltips
            .firstOrNull { it.hintKind === X_AXIS_TOOLTIP }
            ?.let { xAxisTooltip ->
                val positionedTooltip = calculateVerticalTooltipPosition(xAxisTooltip, BOTTOM, AXIS_STEM_LENGTH, true)
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
            ?.let { desiredPosition.add(calculateHorizontalTooltipPosition(it, AXIS_STEM_LENGTH)) }

        // add corner tooltips
        tooltips
            .filter(::isCorner)
            .let { desiredPosition += calculateCornerTooltipsPosition(it) }

        // all other tooltips (axis and corner tooltips are ignored in this method)
        desiredPosition += calculateDataTooltipsPosition(tooltips)

        return rearrangeWithoutOverlapping(desiredPosition)
    }

    private fun calculateDataTooltipsPosition(tooltips: List<MeasuredTooltip>): List<PositionedTooltip> {
        val placementList = ArrayList<PositionedTooltip>()

        for (measuredTooltip in tooltips) {
            if (isCorner(measuredTooltip))
            // Corner tooltips should be processed separately to configure corner space.
                continue

            when (measuredTooltip.hintKind) {
                VERTICAL_TOOLTIP -> placementList.add(
                    calculateVerticalTooltipPosition(
                        measuredTooltip,
                        TOP,
                        NORMAL_STEM_LENGTH,
                        false
                    )
                )

                HORIZONTAL_TOOLTIP -> placementList.add(
                    calculateHorizontalTooltipPosition(
                        measuredTooltip,
                        NORMAL_STEM_LENGTH
                    )
                )

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

    private fun calculateCornerTooltipsPosition(cornerTooltips: List<MeasuredTooltip>): List<PositionedTooltip> {
        val placementList = ArrayList<PositionedTooltip>()

        val tooltipsHeight = cornerTooltips.sumByDouble { it.size.y } + MARGIN_BETWEEN_TOOLTIPS * cornerTooltips.size
        val verticalTooltipRange = when (myTooltipAnchor) {
            //top
            TooltipAnchor.TOP_LEFT,
            TooltipAnchor.TOP_RIGHT -> rightAligned(myVerticalGeomSpace.start(), tooltipsHeight, 0.0)
            // bottom
            else -> leftAligned(myVerticalGeomSpace.end(), tooltipsHeight, 0.0)
        }

        var tooltipY = verticalTooltipRange.start()
        cornerTooltips.forEach { tooltip ->
            val positionedTooltip = calculatePlotCornerTooltipPosition(tooltip, tooltipY, verticalTooltipRange)
            placementList.add(positionedTooltip)
            tooltipY += positionedTooltip.height + MARGIN_BETWEEN_TOOLTIPS
        }

        return placementList
    }

    private fun rearrangeWithoutOverlapping(
        tooltips: List<PositionedTooltip>
    ): List<PositionedTooltip> {
        if (tooltips.isEmpty()) {
            return tooltips
        }

        val restrictions = ArrayList<DoubleRectangle>()
        val separatedTooltips = ArrayList<PositionedTooltip>()

        fun fixate(positionedTooltip: PositionedTooltip) {
            separatedTooltips.add(positionedTooltip)
            restrictions.add(positionedTooltip.rect())
        }

        // First add tooltips with pre-arranged position
        tooltips.select(CURSOR_TOOLTIP, X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP).forEach(::fixate)
        tooltips.selectCorner().forEach(::fixate)

        // Now try to space out other tooltips.
        // Order matters - vertical tooltips should be added last, because it's easier to space them out.
        // Include overlapped corner tooltips.

        tooltips.select(HORIZONTAL_TOOLTIP).withOverlapped(tooltips.selectCorner())
            .let { horizontalTooltips ->
            if (horizontalTooltips.sumByDouble(PositionedTooltip::height) < myVerticalSpace.length()) {
                HorizontalTooltipExpander(myVerticalSpace).fixOverlapping(horizontalTooltips)
                    .forEach(::fixate)
            } else {
                horizontalTooltips
                    .filter { it.stemCoord.y < myCursorCoord.y }
                    .maxBy { it.stemCoord.y }
                    ?.let(::fixate)
            }
        }

        tooltips.select(VERTICAL_TOOLTIP).withOverlapped(tooltips.selectCorner())
            .let {
                VerticalTooltipRotatingExpander(myVerticalSpace, myHorizontalSpace).fixOverlapping(
                    it,
                    restrictions
                )
            }
            .forEach(::fixate)

        return separatedTooltips
    }

    private fun calculateVerticalTooltipPosition(
        measuredTooltip: MeasuredTooltip, alignment: VerticalAlignment,
        stemLength: Double, ignoreCursor: Boolean
    ): PositionedTooltip {
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

            if (myVerticalAlignmentResolver.resolve(
                    topTooltipRange,
                    bottomTooltipRange,
                    alignment,
                    cursorVerticalRange
                ) === TOP
            ) {
                tooltipY = topTooltipRange.start()
                stemY = targetTopPoint
            } else {
                tooltipY = bottomTooltipRange.start()
                stemY = targetBottomPoint
            }
        }

        return PositionedTooltip(
            measuredTooltip = measuredTooltip,
            tooltipCoord = DoubleVector(tooltipX, tooltipY),
            stemCoord = DoubleVector(measuredTooltip.hintCoord.x, stemY)
        )
    }

    private fun calculateHorizontalTooltipPosition(
        measuredTooltip: MeasuredTooltip,
        stemLength: Double
    ): PositionedTooltip {
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

            val canFitLeft = leftTooltipPlacement.inside(myHorizontalSpace)
            val canFitRight = rightTooltipPlacement.inside(myHorizontalSpace)

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

    private fun calculateAnchorX(measuredTooltip: MeasuredTooltip, horizontalAlignment: HorizontalAlignment): Double {
        return when (horizontalAlignment) {
            HorizontalAlignment.RIGHT -> myHorizontalGeomSpace.end() - measuredTooltip.size.x
            else -> myHorizontalSpace.start() + myHorizontalGeomSpace.start() + MARGIN_BETWEEN_TOOLTIPS
        }
    }

    private fun calculatePlotCornerTooltipPosition(
        measuredTooltip: MeasuredTooltip,
        tooltipY: Double,
        verticalTooltipRange: DoubleRange
    ): PositionedTooltip {

        val horizontalAlignment = when (myTooltipAnchor) {
            TooltipAnchor.TOP_RIGHT, TooltipAnchor.BOTTOM_RIGHT -> HorizontalAlignment.RIGHT
            else -> HorizontalAlignment.LEFT
        }

        var tooltipX = calculateAnchorX(measuredTooltip, horizontalAlignment)

        // check position under cursor
        val isOverlapX = overlapsCursorHorizontalRange(measuredTooltip, tooltipX)
        val isOverlapY = overlapsCursorVerticalRange(verticalTooltipRange)
        if (isOverlapX && isOverlapY) {
            tooltipX = calculateAnchorX(
                measuredTooltip,
                horizontalAlignment.inversed()
            )
        }

        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, tooltipCoord)
    }

    private fun overlapsCursorHorizontalRange(measuredTooltip: MeasuredTooltip, tooltipX: Double): Boolean {
        val horizontalTooltipRange = DoubleRange.withStartAndLength(tooltipX, measuredTooltip.size.x)
        val cursorHorizontalRange = DoubleRange.withStartAndLength(myCursorCoord.x, CURSOR_DIMENSION.x)
        return horizontalTooltipRange.overlaps(cursorHorizontalRange)
    }

    private fun overlapsCursorVerticalRange(verticalTooltipRange: DoubleRange): Boolean {
        val cursorVerticalRange = DoubleRange.withStartAndLength(myCursorCoord.y, CURSOR_DIMENSION.y)
        return verticalTooltipRange.overlaps(cursorVerticalRange)
    }

    private fun useCornerTooltips(): Boolean {
        return myTooltipAnchor != TooltipAnchor.NONE
    }

    private fun isCorner(tooltipSpec: TooltipSpec): Boolean {
        return if (useCornerTooltips()) !tooltipSpec.isOutlier else false
    }

    private fun isCorner(tooltip: MeasuredTooltip): Boolean {
        return isCorner(tooltip.tooltipSpec)
    }

    private fun isCorner(tooltip: PositionedTooltip): Boolean {
        return isCorner(tooltip.tooltipSpec)
    }

    private fun List<PositionedTooltip>.selectCorner(): List<PositionedTooltip> {
        return this.filter(::isCorner)
    }

    internal enum class VerticalAlignment {
        TOP,
        BOTTOM
    }

    enum class HorizontalAlignment {
        LEFT,
        RIGHT,
        CENTER;

        fun inversed(): HorizontalAlignment {
            return when (this) {
                LEFT -> RIGHT
                RIGHT -> LEFT
                CENTER -> CENTER
            }
        }
    }

    class PositionedTooltip {
        val tooltipBox: TooltipBox
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
            tooltipBox = measuredTooltip.tooltipBox
            this.tooltipCoord = tooltipCoord
            this.stemCoord = stemCoord
        }

        private constructor(positionedTooltip: PositionedTooltip, newTooltipCoord: DoubleVector) {
            tooltipSpec = positionedTooltip.tooltipSpec
            tooltipSize = positionedTooltip.tooltipSize
            tooltipBox = positionedTooltip.tooltipBox
            stemCoord = positionedTooltip.stemCoord
            tooltipCoord = newTooltipCoord
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
        internal val size: DoubleVector,
        internal val tooltipBox: TooltipBox
    ) {
        constructor(tooltipSpec: TooltipSpec, tooltipBox: TooltipBox)
                : this(tooltipSpec, tooltipBox.contentRect.dimension, tooltipBox)

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
            return this.filter { kinds.contains(it.hintKind) }
        }

        private fun List<PositionedTooltip>.isOverlapped(tooltip: PositionedTooltip): Boolean {
           return this.find { it != tooltip && it.rect().intersects(tooltip.rect()) } != null
        }

        private fun List<PositionedTooltip>.withOverlapped(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
           val overlapped = tooltips.filter { tooltip -> this.isOverlapped(tooltip) }
           return this - tooltips + overlapped
        }
    }
}
