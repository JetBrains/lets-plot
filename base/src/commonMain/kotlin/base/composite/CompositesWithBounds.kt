package jetbrains.datalore.base.composite

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.Vector

class CompositesWithBounds(private val myThreshold: Int) {

    fun <ViewT : HasBounds> isAbove(upper: ViewT?, lower: ViewT?): Boolean {
        val upperBounds = upper!!.bounds
        val lowerBounds = lower!!.bounds
        return upperBounds.origin.y + upperBounds.dimension.y - myThreshold <= lowerBounds.origin.y
    }

    fun <ViewT : HasBounds> isBelow(lower: ViewT, upper: ViewT?): Boolean {
        return isAbove(upper, lower)
    }

    fun <ViewT> homeElement(cell: ViewT): ViewT
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        var current = cell
        while (true) {
            val prev = Composites.prevFocusable(current)
            if (prev == null || isAbove(prev, cell)) return current
            current = prev
        }
    }

    fun <ViewT> endElement(cell: ViewT): ViewT
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        var current = cell
        while (true) {
            val next = Composites.nextFocusable(current)
            if (next == null || isBelow(next, cell)) return current
            current = next
        }
    }

    fun <ViewT> upperFocusables(v: ViewT): Iterable<ViewT>
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        val nextUpperFocusable = NextUpperFocusable(v)
        return Composites.iterate(v, { nextUpperFocusable.apply(it) })
    }

    fun <ViewT> lowerFocusables(v: ViewT): Iterable<ViewT>
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        val nextLowerFocusable = NextLowerFocusable(v)
        return Composites.iterate(v, { nextLowerFocusable.apply(it) })
    }

    fun <ViewT> upperFocusable(v: ViewT, xOffset: Int): ViewT?
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        var current = Composites.prevFocusable(v)
        var bestMatch: ViewT? = null

        while (current != null) {
            if (bestMatch != null && isAbove(current, bestMatch)) {
                break
            }

            if (bestMatch != null) {
                if (distanceTo(bestMatch, xOffset) > distanceTo(current, xOffset)) {
                    bestMatch = current
                }
            } else if (isAbove(current, v)) {
                bestMatch = current
            }

            current = Composites.prevFocusable(current)
        }

        return bestMatch
    }

    fun <ViewT> lowerFocusable(v: ViewT, xOffset: Int): ViewT?
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {
        var current = Composites.nextFocusable(v)
        var bestMatch: ViewT? = null

        while (current != null) {
            if (bestMatch != null && isBelow(current, bestMatch)) {
                break
            }

            if (bestMatch != null) {
                if (distanceTo(bestMatch, xOffset) > distanceTo(current, xOffset)) {
                    bestMatch = current
                }
            } else if (isBelow(current, v)) {
                bestMatch = current
            }

            current = Composites.nextFocusable(current)
        }

        return bestMatch
    }

    fun <ViewT : HasBounds> distanceTo(c: ViewT, x: Int): Double {
        val bounds = c.bounds
        return bounds.distance(Vector(x, bounds.origin.y))
    }

    private inner class NextUpperFocusable<ViewT> internal constructor(private val myInitial: ViewT) : Function<ViewT, ViewT?>
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {

        private val myFirstFocusableAbove: ViewT?

        init {
            myFirstFocusableAbove = firstFocusableAbove(myInitial)
        }

        private fun firstFocusableAbove(initial: ViewT): ViewT? {
            var current = Composites.prevFocusable(initial)
            while (current != null && !isAbove(current, initial)) {
                current = Composites.prevFocusable(current)
            }
            return current
        }

        override fun apply(value: ViewT): ViewT? {
            if (value === myInitial) {
                return myFirstFocusableAbove
            }
            val next = Composites.prevFocusable(value)
            return if (next != null && !isAbove(next, myFirstFocusableAbove)) next else null
        }
    }

    private inner class NextLowerFocusable<ViewT> internal constructor(private val myInitial: ViewT) : Function<ViewT, ViewT?>
            where ViewT : NavComposite<ViewT>, ViewT : HasFocusability, ViewT : HasVisibility, ViewT : HasBounds {

        private val myFirstFocusableBelow: ViewT?

        init {
            myFirstFocusableBelow = firstFocusableBelow(myInitial)
        }

        private fun firstFocusableBelow(initial: ViewT): ViewT? {
            var current = Composites.nextFocusable(initial)
            while (current != null && !isBelow(current, initial)) {
                current = Composites.nextFocusable(current)
            }
            return current
        }

        override fun apply(value: ViewT): ViewT? {
            if (value === myInitial) {
                return myFirstFocusableBelow
            }
            val next = Composites.nextFocusable(value)
            return if (next != null && !isBelow(next, myFirstFocusableBelow)) next else null
        }
    }
}