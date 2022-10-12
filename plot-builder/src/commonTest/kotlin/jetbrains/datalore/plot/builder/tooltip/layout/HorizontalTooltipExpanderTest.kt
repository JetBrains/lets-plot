/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HorizontalTooltipExpanderTest {
    @Test
    fun noOverlapping() {
        val expander = HorizontalTooltipExpander(DoubleSpan(0.0, 561.0))

        val fixed = expander.fixOverlapping(listOf(
            newTooltip(
                text = "a",
                size = DoubleVector(127.3639907836914, 41.71999931335449),
                position = DoubleVector(43.57102234528557, 271.94250360135726),
                pointer = DoubleVector(182.93501312897698, 292.8025032580345)
            ), newTooltip(
                text = "b",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(36.89902527497307, 374.46301688511346),
                pointer = DoubleVector(182.93501312897698, 395.3230165417907)
            ), newTooltip(
                text = "c",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(36.89902527497307, 487.2286382401689),
                pointer = DoubleVector(182.93501312897698, 508.08863789684614)
            )
        ))

        with(fixed) {
            assertEquals(271.94250360135726, findTooltip("a").top)
            assertEquals(374.46301688511346, findTooltip("b").top)
            assertEquals(487.2286382401689, findTooltip("c").top)
        }
    }


    @Test
    fun stickAB() {
        val expander = HorizontalTooltipExpander(DoubleSpan(0.0, 561.0))

        val fixed = expander.fixOverlapping(listOf(
            newTooltip(
                text = "a",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(26.168079596879153, 311.1411258105596),
                pointer = DoubleVector(172.20406745088306, 332.0011254672368)
            ),
            newTooltip(
                text = "b",
                size = DoubleVector(127.3639907836914, 41.71999931335449),
                position = DoubleVector(32.84007666719165, 331.425607332582),
                pointer = DoubleVector(172.20406745088306, 352.28560698925924)
            ),
            newTooltip(
                text = "c",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(26.168079596879153, 513.3825161879073),
                pointer = DoubleVector(172.20406745088306, 534.2425158445845)
            )
        ))

        with(fixed) {
            assertEquals(297.92336691489356, findTooltip("a").top)
            assertEquals(344.64336622824806, findTooltip("b").top)
            assertEquals(513.3825161879073, findTooltip("c").top)
        }
    }

    @Test
    fun stickAndSawpAB() {
        val expander = HorizontalTooltipExpander(DoubleSpan(0.0, 561.0))

        val fixed = expander.fixOverlapping(listOf(
            newTooltip(
                text = "a",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(28.85081601640266, 323.57452953083396),
                pointer = DoubleVector(174.88680387040657, 344.4345291875112)
            ),
            newTooltip(
                text = "b",
                size = DoubleVector(127.3639907836914, 41.71999931335449),
                position = DoubleVector(35.52281308671516, 315.60709369261485),
                pointer = DoubleVector(174.88680387040657, 336.4670933492921)
            ),
            newTooltip(
                text = "c",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(28.85081601640266, 507.89523743595464),
                pointer = DoubleVector(174.88680387040657, 528.7552370926319)
            )
        ))

        with(fixed) {
            assertEquals(342.95081126840165, findTooltip("a").top)
            assertEquals(296.23081195504716, findTooltip("b").top)
            assertEquals(507.89523743595464, findTooltip("c").top)
        }
    }

    @Test
    fun shouldCountAsOverlappedWhenDistanceIsLessThanTooltipsMargin() {

        val expander = HorizontalTooltipExpander(DoubleSpan(0.0, 561.0))

        val fixed = expander.fixOverlapping(listOf(
            newTooltip(
                text = "a",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(177.0720031950744, 519.2800006866455),
                pointer = DoubleVector(323.1079910490783, 561.0)
            ),
            newTooltip(
                text = "b",
                size = DoubleVector(127.3639907836914, 41.71999931335449),
                position = DoubleVector(183.7440002653869, 474.63073221732236),
                pointer = DoubleVector(323.1079910490783, 495.4907318739996)
            ),
            newTooltip(
                text = "c",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(177.0720031950744, 389.29218752292195),
                pointer = DoubleVector(323.1079910490783, 410.1521871795992)
            )
        ))

        with(fixed) {
            assertEquals(519.2800006866455, findTooltip("a").top)
            assertEquals(472.560001373291, findTooltip("b").top)
            assertEquals(389.29218752292195, findTooltip("c").top)

            (findTooltip("a").top - findTooltip("b").bottom).let {
                assertTrue("Distance between tooltips is less than margin: $it") {
                    it >= Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
                }
            }
        }
    }

    @Test
    fun stickAll() {
        val expander = HorizontalTooltipExpander(DoubleSpan(0.0, 561.0))

        val fixed = expander.fixOverlapping(listOf(
            newTooltip(
                text = "a",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(260.2368322003019, 519.2800006866455),
                pointer = DoubleVector(406.2728200543058, 561.0)
            ),
            newTooltip(
                text = "b",
                size = DoubleVector(127.3639907836914, 41.71999931335449),
                position = DoubleVector(266.9088292706144, 519.2800006866455),
                pointer = DoubleVector(406.2728200543058, 560.9966031728111)
            ),
            newTooltip(
                text = "c",
                size = DoubleVector(134.0359878540039, 41.71999931335449),
                position = DoubleVector(260.2368322003019, 468.4456301012522),
                pointer = DoubleVector(406.2728200543058, 489.30562975792947)
            )
        ))

        with(fixed) {
            assertEquals(519.2800006866455, findTooltip("a").top)
            assertEquals(472.560001373291, findTooltip("b").top)
            assertEquals(425.8400020599365, findTooltip("c").top)
        }
    }

    private fun List<PositionedTooltip>.findTooltip(text: String): PositionedTooltip {
        return first { it.tooltipSpec.lines.map(TooltipSpec.Line::toString) == listOf(text) }
    }

    private fun newTooltip(text: String, size: DoubleVector, position: DoubleVector, pointer: DoubleVector): PositionedTooltip {
        val spec = TooltipSpec(
            layoutHint = TipLayoutHint.cursorTooltip(ZERO),
            title = null,
            lines = listOf(TooltipSpec.Line.withValue(text)),
            fill = Color.BLACK,
            markerColors = emptyList(),
            isOutlier = true
        )
        return PositionedTooltip(
            MeasuredTooltip(spec, size, TooltipBox(), strokeWidth = 0.0),
            position,
            pointer
        )
    }

}