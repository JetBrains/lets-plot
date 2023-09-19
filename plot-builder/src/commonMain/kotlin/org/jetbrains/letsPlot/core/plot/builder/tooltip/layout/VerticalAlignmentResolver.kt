/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP

internal class VerticalAlignmentResolver(private val myVerticalSpace: DoubleSpan) {

    fun resolve(
        topPlacementRange: DoubleSpan,
        bottomPlacementRange: DoubleSpan,
        preferredPlacement: VerticalAlignment,
        cursorRange: DoubleSpan
    ): VerticalAlignment {
        val currentState = Matcher()
            .topCursorOk(!topPlacementRange.connected(cursorRange))
            .topSpaceOk(topPlacementRange in myVerticalSpace)
            .bottomCursorOk(!bottomPlacementRange.connected(cursorRange))
            .bottomSpaceOk(bottomPlacementRange in myVerticalSpace)
            .preferredAlignment(preferredPlacement)

        for (matcher in PLACEMENT_MATCHERS) {
            if (matcher.first.match(currentState)) {
                return matcher.second
            }
        }

        throw IllegalStateException("Some matcher should match")
    }

    internal class Matcher {

        private var myTopSpaceOk: Boolean? = null
        private var myTopCursorOk: Boolean? = null
        private var myBottomSpaceOk: Boolean? = null
        private var myBottomCursorOk: Boolean? = null
        private var myPreferredAlignment: VerticalAlignment? = null

        fun match(other: Matcher): Boolean {
            return (match({ getBottomCursorOk(it) }, other) &&
                    match({ getBottomSpaceOk(it) }, other) &&
                    match({ getTopCursorOk(it) }, other) &&
                    match({ getTopSpaceOk(it) }, other) &&
                    match({ getPreferredAlignment(it) }, other))

        }

        fun topSpaceOk(topSpaceOk: Boolean?): Matcher {
            myTopSpaceOk = topSpaceOk
            return this
        }

        fun topCursorOk(topCursorOk: Boolean?): Matcher {
            myTopCursorOk = topCursorOk
            return this
        }

        fun bottomSpaceOk(bottomSpaceOk: Boolean?): Matcher {
            myBottomSpaceOk = bottomSpaceOk
            return this
        }

        fun bottomCursorOk(bottomCursorOk: Boolean?): Matcher {
            myBottomCursorOk = bottomCursorOk
            return this
        }

        fun preferredAlignment(preferredAlignment: VerticalAlignment): Matcher {
            myPreferredAlignment = preferredAlignment
            return this
        }

        private fun <T> match(propertyGetter: (Matcher) -> T, actual: Matcher): Boolean {
            val expected = propertyGetter(this) ?: return true

            return expected == propertyGetter(actual)
        }

        companion object {
            fun getTopSpaceOk(matcher: Matcher): Boolean? {
                return matcher.myTopSpaceOk
            }

            fun getTopCursorOk(matcher: Matcher): Boolean? {
                return matcher.myTopCursorOk
            }

            fun getBottomSpaceOk(matcher: Matcher): Boolean? {
                return matcher.myBottomSpaceOk
            }

            fun getBottomCursorOk(matcher: Matcher): Boolean? {
                return matcher.myBottomCursorOk
            }

            fun getPreferredAlignment(matcher: Matcher): VerticalAlignment? {
                return matcher.myPreferredAlignment
            }
        }
    }

    companion object {
        private val PLACEMENT_MATCHERS = listOf(
            rule(
                Matcher()
                    .preferredAlignment(TOP)
                    .topSpaceOk(true)
                    .topCursorOk(true),
                TOP
            ),

            rule(
                Matcher()
                    .preferredAlignment(BOTTOM)
                    .bottomSpaceOk(true)
                    .bottomCursorOk(true),
                BOTTOM
            ),

            rule(
                Matcher()
                    .preferredAlignment(TOP)
                    .topSpaceOk(true)
                    .topCursorOk(false)
                    .bottomSpaceOk(true)
                    .bottomCursorOk(true),
                BOTTOM
            ),

            rule(
                Matcher()
                    .preferredAlignment(BOTTOM)
                    .bottomSpaceOk(true)
                    .bottomCursorOk(false)
                    .topSpaceOk(true)
                    .topCursorOk(true),
                TOP
            ),

            rule(
                Matcher()
                    .bottomSpaceOk(false),
                TOP
            ),

            rule(
                Matcher()
                    .topSpaceOk(false),
                BOTTOM
            ),

            rule(
                Matcher(),
                TOP
            )
        )

        private fun rule(condition: Matcher, result: VerticalAlignment): Pair<Matcher, VerticalAlignment> {
            return Pair(condition, result)
        }
    }
}
