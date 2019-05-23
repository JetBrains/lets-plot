package jetbrains.datalore.visualization.plot.builder.tooltip.layout

import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP

internal class VerticalAlignmentResolver(private val myVerticalSpace: DoubleRange) {

    fun resolve(topPlacementRange: DoubleRange, bottomPlacementRange: DoubleRange, preferredPlacement: VerticalAlignment,
                cursorRange: DoubleRange): VerticalAlignment? {

        val currentState = Matcher()
                .topCursorOk(!topPlacementRange.overlaps(cursorRange))
                .topSpaceOk(topPlacementRange.inside(myVerticalSpace))
                .bottomCursorOk(!bottomPlacementRange.overlaps(cursorRange))
                .bottomSpaceOk(bottomPlacementRange.inside(myVerticalSpace))
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
                                .topSpaceOk(false),
                        BOTTOM
                ),

                rule(
                        Matcher()
                                .bottomSpaceOk(false),
                        TOP
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
