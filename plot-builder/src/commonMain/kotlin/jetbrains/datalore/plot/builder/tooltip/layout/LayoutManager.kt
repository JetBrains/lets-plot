/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP
import kotlin.math.min

class LayoutManager(
    private val myViewport: DoubleRectangle,
    private val myPreferredHorizontalAlignment: HorizontalAlignment
) {
    private val myHorizontalSpace: DoubleSpan = DoubleSpan(myViewport.left, myViewport.right)
    private var myVerticalSpace: DoubleSpan = DoubleSpan(0.0, 0.0)
    private var myCursorCoord: DoubleVector = DoubleVector.ZERO
    private var myHorizontalTooltipSpace = DoubleSpan(myViewport.left, myViewport.right)
    private var myVerticalTooltipSpace = DoubleSpan(myViewport.top, myViewport.bottom)
    private lateinit var myVerticalAlignmentResolver: VerticalAlignmentResolver

    fun arrange(
        tooltips: List<MeasuredTooltip>,
        cursorCoord: DoubleVector,
        geomBounds: DoubleRectangle
    ): List<PositionedTooltip> {
        myCursorCoord = cursorCoord
        myVerticalSpace = DoubleSpan(myViewport.top, myViewport.bottom)
        myVerticalAlignmentResolver = VerticalAlignmentResolver(myVerticalSpace)
        myHorizontalTooltipSpace = DoubleSpan.withLowerEnd(geomBounds.left, geomBounds.width)
        myVerticalTooltipSpace = DoubleSpan.withLowerEnd(geomBounds.top, geomBounds.height)

        val desiredPosition = ArrayList<PositionedTooltip>()

        // x-axis tooltip
        tooltips
            .firstOrNull { it.hintKind === X_AXIS_TOOLTIP }
            ?.let { xAxisTooltip ->
                val positionedTooltip = calculateVerticalTooltipPosition(xAxisTooltip, BOTTOM, ignoreCursor = true)
                if (isTooltipWithinBounds(positionedTooltip, geomBounds)) {
                    desiredPosition.add(positionedTooltip)

                    // Limit available vertical space for other tooltips by the axis or top side of the tooltip (if not fit under the axis)
                    myVerticalSpace = DoubleSpan(
                        myViewport.top,
                        min(
                            positionedTooltip.stemCoord.y,
                            positionedTooltip.top
                        )
                    )
                    myVerticalAlignmentResolver = VerticalAlignmentResolver(myVerticalSpace)
                }
            }

        // y-axis tooltip
        tooltips
            .firstOrNull { it.hintKind === Y_AXIS_TOOLTIP }
            ?.let {
                val positionedTooltip = calculateHorizontalTooltipPosition(it)
                if (isTooltipWithinBounds(positionedTooltip, geomBounds)) {
                    desiredPosition.add(positionedTooltip)
                }
            }

        // add corner tooltips - if the cursor is located within the visible boundaries
        if (geomBounds.contains(cursorCoord)) {
            desiredPosition += calculateCornerTooltipsPosition(tooltips)
        }

        // all other tooltips (axis and corner tooltips are ignored in this method)
        desiredPosition += calculateDataTooltipsPosition(
            tooltips,
            // limit horizontal tooltips by y-axis tooltips
            desiredPosition.select(Y_AXIS_TOOLTIP).map(PositionedTooltip::rect)
        )
            .filter { positionedTooltip ->
                // Select tooltips within the visibility bounds
                isTooltipWithinBounds(positionedTooltip, geomBounds)
            }

        // if general tooltips were removed => axis tooltips should also be hidden
        if (tooltips.filterNot(::isAxisTooltip).isNotEmpty() && desiredPosition.all(::isAxisTooltip)) {
            desiredPosition.clear()
        }

        return rearrangeWithoutOverlapping(desiredPosition)
    }

    private fun isTooltipWithinBounds(tooltip: PositionedTooltip, bounds: DoubleRectangle): Boolean {
        return when (tooltip.hintKind) {
            X_AXIS_TOOLTIP -> bounds.xRange().contains(tooltip.stemCoord.x)
            Y_AXIS_TOOLTIP -> bounds.yRange().contains(tooltip.stemCoord.y)
            VERTICAL_TOOLTIP, HORIZONTAL_TOOLTIP, CURSOR_TOOLTIP, ROTATED_TOOLTIP -> bounds.contains(tooltip.stemCoord)
        }
    }

    private fun calculateDataTooltipsPosition(
        tooltips: List<MeasuredTooltip>,
        restrictions: List<DoubleRectangle>
    ): List<PositionedTooltip> {
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
                        false
                    )
                )

                HORIZONTAL_TOOLTIP -> placementList.add(
                    calculateHorizontalTooltipPosition(
                        measuredTooltip,
                        restrictions
                    )
                )

                CURSOR_TOOLTIP -> placementList.add(
                    calculateCursorTooltipPosition(
                        measuredTooltip,
                        restrictions
                    )
                )

                ROTATED_TOOLTIP -> placementList.add(
                    calculateVerticalTooltipPosition(
                        measuredTooltip,
                        BOTTOM,
                        ignoreCursor = true,
                        centered = false
                    )
                )

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

    private fun calculateCornerTooltipsPosition(tooltips: List<MeasuredTooltip>): List<PositionedTooltip> {
        val placementList = ArrayList<PositionedTooltip>()

        tooltips
            .filter(::isCorner) // has an anchor
            .groupBy { it.tooltipSpec.anchor!! }
            .forEach { (tooltipAnchor, cornerTooltips) ->
                val tooltipsHeight = cornerTooltips.sumOf { it.size.y } + MARGIN_BETWEEN_TOOLTIPS * cornerTooltips.size
                val verticalTooltipRange = when (tooltipAnchor.verticalAnchor) {
                    TooltipAnchor.VerticalAnchor.TOP -> rightAligned(myVerticalTooltipSpace.lowerEnd, tooltipsHeight, 0.0)
                    TooltipAnchor.VerticalAnchor.BOTTOM -> leftAligned(myVerticalTooltipSpace.upperEnd, tooltipsHeight, 0.0)
                    TooltipAnchor.VerticalAnchor.MIDDLE -> centered(
                        (myVerticalTooltipSpace.lowerEnd + myVerticalTooltipSpace.upperEnd) / 2,
                        tooltipsHeight
                    )
                }

                var tooltipY = verticalTooltipRange.lowerEnd
                cornerTooltips.forEach { tooltip ->
                    val positionedTooltip = calculatePlotCornerTooltipPosition(
                        tooltip,
                        tooltipY,
                        verticalTooltipRange,
                        tooltipAnchor.horizontalAnchor
                    )
                    placementList.add(positionedTooltip)
                    tooltipY += positionedTooltip.height + MARGIN_BETWEEN_TOOLTIPS
                }
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

        // Now try to space out other tooltips.
        // Order matters - vertical tooltips should be added last, because it's easier to space them out.
        // Include overlapped corner tooltips.

        val horizontalsWithOverlappedCorners = tooltips.select(HORIZONTAL_TOOLTIP).withOverlapped(tooltips.selectCorner())
        horizontalsWithOverlappedCorners
            .let { horizontalTooltips ->
                if (horizontalTooltips.sumOf(PositionedTooltip::height) < myVerticalSpace.length) {
                    HorizontalTooltipExpander(myVerticalSpace)
                        .fixOverlapping(horizontalTooltips)
                        .forEach(::fixate)
                } else {
                    horizontalTooltips
                        .filter { it.stemCoord.y < myCursorCoord.y }
                        .maxByOrNull { it.stemCoord.y }
                        ?.let(::fixate)
                }
            }

        // Add corner tooltips
        (tooltips.selectCorner() - horizontalsWithOverlappedCorners).forEach(::fixate)

        (tooltips.select(VERTICAL_TOOLTIP, ROTATED_TOOLTIP) - tooltips.selectCorner())
            .let { verticalTooltips ->
                if (verticalTooltips.sumOf(PositionedTooltip::width) < myHorizontalSpace.length) {
                    fixOverlappingWithShifting(
                        VerticalTooltipRotatingExpander(myVerticalSpace, myHorizontalSpace).fixOverlapping(
                            verticalTooltips,
                            restrictions
                        )
                    ).forEach(::fixate)
                } else {
                    verticalTooltips
                        .sortedBy { it.stemCoord.x }
                        .let { tooltips ->
                            tooltips
                                .filter { it.stemCoord.x > myCursorCoord.x }
                                .minByOrNull { it.stemCoord.x }
                                ?: tooltips.last()
                        }
                        .let(::fixate)
                }
            }

        return separatedTooltips
    }

    private fun fixOverlappingWithShifting(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        val placementList = ArrayList<PositionedTooltip>()
        /*
        val allRestrictions = ArrayList(restrictions)
        val helper = VerticalTooltipShiftingExpander(myHorizontalSpace)
                helper.fixOverlapping(listOf(0 to tooltip), allRestrictions)
                val newPosition = helper.spacedTooltips?.firstOrNull()?.second
                val newTooltip = if (newPosition != null && newPosition != tooltip.tooltipCoord ) {
                    tooltip.moveTo(newPosition)//.add(DoubleVector(MARGIN_BETWEEN_TOOLTIPS, 0.0)))
                } else {
                    tooltip
                }
                allRestrictions.add(newTooltip.rect())
                placementList.add(newTooltip)

            }
        */

        tooltips
            .sortedWith(compareBy({ it.stemCoord.x }, { it.tooltipCoord.x }))
            .forEach { tooltip: PositionedTooltip ->
                val newTooltip = if (placementList.isOverlapped(tooltip)) {
                    val newPosition = DoubleVector(
                        placementList.last().rect().right + MARGIN_BETWEEN_TOOLTIPS,
                        tooltip.tooltipCoord.y
                    )
                    tooltip.moveTo(newPosition)
                } else {
                    tooltip
                }
                placementList.add(newTooltip)
            }
        return placementList
    }

    private fun calculateVerticalTooltipPosition(
        measuredTooltip: MeasuredTooltip,
        alignment: VerticalAlignment,
        ignoreCursor: Boolean,
        centered: Boolean = true
    ): PositionedTooltip {
        val tooltipX = if (centered)
            centerInsideRange(measuredTooltip.hintCoord.x, measuredTooltip.size.x, myHorizontalSpace)
        else
            measuredTooltip.hintCoord.x

        val stemY: Double
        val tooltipY: Double
        run {
            val targetCoordY = measuredTooltip.hintCoord.y
            val stemLength = measuredTooltip.stemLength
            val targetTopPoint = targetCoordY - measuredTooltip.hintRadius
            val targetBottomPoint = targetCoordY + measuredTooltip.hintRadius

            val tooltipHeight = measuredTooltip.size.y
            val topTooltipRange = leftAligned(targetTopPoint, tooltipHeight, stemLength)

            val bottomTooltipRange =
                rightAligned(targetBottomPoint, tooltipHeight, stemLength).let { bottomTooltipRange ->
                    // bottom range of the axis tooltip is out of the vertical space => move it to the border
                    if (measuredTooltip.hintKind == X_AXIS_TOOLTIP && bottomTooltipRange !in myVerticalSpace) {
                        leftAligned(myVerticalSpace.upperEnd, tooltipHeight, stemLength)
                    } else {
                        bottomTooltipRange
                    }
                }

            val cursorVerticalRange = if (!ignoreCursor && overlapsCursorHorizontalRange(measuredTooltip, tooltipX))
                DoubleSpan.withLowerEnd(myCursorCoord.y, CURSOR_DIMENSION.y)
            else
                EMPTY_DOUBLE_RANGE

            if (targetTopPoint in myVerticalTooltipSpace &&
                myVerticalAlignmentResolver.resolve(topTooltipRange, bottomTooltipRange, alignment, cursorVerticalRange) === TOP
            ) {
                tooltipY = topTooltipRange.lowerEnd
                stemY = targetTopPoint
            } else {
                tooltipY = bottomTooltipRange.lowerEnd
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
        restrictions: List<DoubleRectangle> = emptyList()
    ): PositionedTooltip {
        val tooltipY = centerInsideRange(measuredTooltip.hintCoord.y, measuredTooltip.size.y, myVerticalSpace)

        val tooltipX: Double
        val stemX: Double
        run {
            val targetCoordX = measuredTooltip.hintCoord.x
            val tooltipWidth = measuredTooltip.size.x
            val hintSize = measuredTooltip.hintRadius
            val stemLength = measuredTooltip.stemLength
            val margin = hintSize + stemLength

            val targetLeftPoint = targetCoordX - hintSize
            val leftTooltipPlacement = leftAligned(targetCoordX, tooltipWidth, margin)
            val rightTooltipPlacement = rightAligned(targetCoordX, tooltipWidth, margin)

            // The tooltip should fit in horizontal space and not intersect restrictions,
            // restrictions are expected to contain only y-axis tooltip.
            // Also the coordinate pointed to by the tooltip should be inside the geometry space.
            // Don't change canFitRight as it is not affected by restrictions (as long as y-axis is on the left side).
            val canFitLeft = leftTooltipPlacement in myHorizontalSpace &&
                    (measuredTooltip.hintKind == Y_AXIS_TOOLTIP || myHorizontalTooltipSpace.contains(targetLeftPoint)) &&
                    restrictions.all {
                        val tooltipRect = DoubleRectangle(
                            DoubleVector(leftTooltipPlacement.lowerEnd, tooltipY), measuredTooltip.size
                        )
                        !it.intersects(tooltipRect)
                    }
            val canFitRight = rightTooltipPlacement in myHorizontalSpace

            when {
                measuredTooltip.hintKind == Y_AXIS_TOOLTIP && !canFitLeft -> {
                    // move axis tooltip to the border if it doesn't fit
                    tooltipX = 0.0
                    stemX = targetLeftPoint
                }

                !(canFitLeft || canFitRight) -> {
                    when (myPreferredHorizontalAlignment) {
                        HorizontalAlignment.LEFT -> {
                            stemX = targetLeftPoint
                            tooltipX = stemX + stemLength
                        }
                        HorizontalAlignment.RIGHT -> {
                            stemX = targetCoordX + hintSize
                            tooltipX = stemX - tooltipWidth - stemLength
                        }
                        HorizontalAlignment.CENTER -> {
                            stemX = targetCoordX
                            tooltipX = targetCoordX - tooltipWidth / 2
                        }
                    }
                }

                myPreferredHorizontalAlignment == HorizontalAlignment.LEFT && canFitLeft || !canFitRight -> {
                    tooltipX = leftTooltipPlacement.lowerEnd
                    stemX = targetLeftPoint
                }

                else -> {
                    tooltipX = rightTooltipPlacement.lowerEnd
                    stemX = targetCoordX + hintSize
                }
            }
        }

        val stemCoord = DoubleVector(stemX, measuredTooltip.hintCoord.y)
        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, stemCoord)
    }

    private fun calculateCursorTooltipPosition(
        measuredTooltip: MeasuredTooltip,
        restrictions: List<DoubleRectangle>
    ): PositionedTooltip {
        val tooltipX = centerInsideRange(myCursorCoord.x, measuredTooltip.size.x, myHorizontalSpace)

        val tooltipY: Double
        val stemY: Double
        run {
            val targetCoordY = myCursorCoord.y
            val tooltipHeight = measuredTooltip.size.y
            val stemLength = measuredTooltip.stemLength

            val targetTopPoint = targetCoordY - measuredTooltip.hintRadius
            val targetBottomPoint = targetCoordY + measuredTooltip.hintRadius

            val topTooltipPlacement = leftAligned(targetTopPoint, tooltipHeight, stemLength)
            val bottomTooltipPlacement = rightAligned(targetBottomPoint, tooltipHeight, stemLength)

            // The tooltip should fit in vertical space and not intersect restrictions,
            // restrictions are expected to contain only y-axis tooltip.
            if (topTooltipPlacement in myVerticalSpace && restrictions.all {
                    val tooltipRect = DoubleRectangle(
                        DoubleVector(tooltipX, topTooltipPlacement.lowerEnd), measuredTooltip.size
                    )
                    !it.intersects(tooltipRect)
                }) {
                tooltipY = topTooltipPlacement.lowerEnd
                stemY = targetTopPoint
            } else {
                tooltipY = bottomTooltipPlacement.lowerEnd
                stemY = targetBottomPoint
            }
        }

        val tooltipCoord = DoubleVector(tooltipX, tooltipY)
        val stemCoord = DoubleVector(myCursorCoord.x, stemY)
        return PositionedTooltip(measuredTooltip, tooltipCoord, stemCoord)
    }

    private fun calculateAnchorX(measuredTooltip: MeasuredTooltip, horizontalAlignment: HorizontalAlignment): Double {
        return when (horizontalAlignment) {
            HorizontalAlignment.RIGHT -> myHorizontalTooltipSpace.upperEnd - measuredTooltip.size.x
            HorizontalAlignment.LEFT -> myHorizontalSpace.lowerEnd + myHorizontalTooltipSpace.lowerEnd + MARGIN_BETWEEN_TOOLTIPS
            HorizontalAlignment.CENTER -> (myHorizontalTooltipSpace.lowerEnd + myHorizontalTooltipSpace.upperEnd - measuredTooltip.size.x) / 2
        }
    }

    private fun calculatePlotCornerTooltipPosition(
        measuredTooltip: MeasuredTooltip,
        tooltipY: Double,
        verticalTooltipRange: DoubleSpan,
        horizontalAnchor: TooltipAnchor.HorizontalAnchor
    ): PositionedTooltip {
        val horizontalAlignment = when (horizontalAnchor) {
            TooltipAnchor.HorizontalAnchor.RIGHT -> HorizontalAlignment.RIGHT
            TooltipAnchor.HorizontalAnchor.LEFT -> HorizontalAlignment.LEFT
            TooltipAnchor.HorizontalAnchor.CENTER -> HorizontalAlignment.CENTER
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
        val horizontalTooltipRange = DoubleSpan.withLowerEnd(tooltipX, measuredTooltip.size.x)
        val cursorHorizontalRange = DoubleSpan.withLowerEnd(myCursorCoord.x, CURSOR_DIMENSION.x)
        return horizontalTooltipRange.connected(cursorHorizontalRange)
    }

    private fun overlapsCursorVerticalRange(verticalTooltipRange: DoubleSpan): Boolean {
        val cursorVerticalRange = DoubleSpan.withLowerEnd(myCursorCoord.y, CURSOR_DIMENSION.y)
        return verticalTooltipRange.connected(cursorVerticalRange)
    }

    private fun isCorner(tooltipSpec: TooltipSpec) = tooltipSpec.anchor != null
    private fun isCorner(tooltip: MeasuredTooltip) = isCorner(tooltip.tooltipSpec)
    private fun isCorner(tooltip: PositionedTooltip) = isCorner(tooltip.tooltipSpec)

    private fun isAxisTooltip(tooltipSpec: TooltipSpec) = tooltipSpec.layoutHint.kind in listOf(X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP)
    private fun isAxisTooltip(tooltip: MeasuredTooltip) = isAxisTooltip(tooltip.tooltipSpec)
    private fun isAxisTooltip(tooltip: PositionedTooltip) = isAxisTooltip(tooltip.tooltipSpec)

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
        internal val tooltipBox: TooltipBox,
        private val strokeWidth: Double
    ) {
        constructor(tooltipSpec: TooltipSpec, tooltipBox: TooltipBox, strokeWidth: Double)
                : this(tooltipSpec, tooltipBox.contentRect.dimension, tooltipBox, strokeWidth)

        internal val hintCoord get() = tooltipSpec.layoutHint.coord!!
        internal val hintKind get() = tooltipSpec.layoutHint.kind
        internal val hintRadius get() = tooltipSpec.layoutHint.objectRadius + strokeWidth
        internal val stemLength get() = tooltipSpec.layoutHint.stemLength.value
    }

    companion object {

        private val CURSOR_DIMENSION = DoubleVector(10.0, 10.0)
        private val EMPTY_DOUBLE_RANGE = DoubleSpan.withLowerEnd(0.0, 0.0)

        internal fun moveIntoLimit(range: DoubleSpan, limit: DoubleSpan): DoubleSpan {
            if (range in limit) {
                return range
            }

            return when {
                range.lowerEnd < limit.lowerEnd -> DoubleSpan.withLowerEnd(limit.lowerEnd, range.length)
                range.upperEnd > limit.upperEnd -> DoubleSpan.withUpperEnd(limit.upperEnd, range.length)
                else -> range
            }
        }

        private fun centered(start: Double, length: Double): DoubleSpan {
            return DoubleSpan.withLowerEnd(start - length / 2, length)
        }

        private fun leftAligned(start: Double, length: Double, margin: Double): DoubleSpan {
            return DoubleSpan.withLowerEnd(start - length - margin, length)
        }

        private fun rightAligned(start: Double, length: Double, margin: Double): DoubleSpan {
            return DoubleSpan.withLowerEnd(start + margin, length)
        }

        private fun centerInsideRange(position: Double, size: Double, range: DoubleSpan): Double {
            return moveIntoLimit(centered(position, size), range).lowerEnd
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
