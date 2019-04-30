package jetbrains.datalore.base.values

import kotlin.test.Test
import kotlin.test.assertEquals

class IntervalTest {

    private val i_1_4 = i(1, 4)

    private fun i(lower: Int, upper: Int): Interval {
        return Interval(lower, upper)
    }

    @Test
    fun contains() {
        checkContains(i_1_4)
        checkContains(2, 3)
        checkContains(1, 2)
        checkContains(3, 4)

        checkNotContains(0, 4)
        checkNotContains(1, 5)
        checkNotContains(0, 5)
        checkNotContains(4, 10)
    }

    @Test
    fun intersects() {
        checkIntersects(i_1_4)
        checkIntersects(2, 3)
        checkIntersects(1, 2)
        checkIntersects(3, 4)

        checkIntersects(0, 4)
        checkIntersects(0, 1)
        checkIntersects(2, 5)
        checkIntersects(4, 5)

        checkIntersects(0, 5)

        checkNotIntersects(-1, 0)
        checkNotIntersects(5, 6)
    }

    @Test
    fun union() {
        checkUnion(i(1, 2), i(3, 4))
        checkUnion(i(1, 3), i(2, 4))

        checkUnion(i(3, 4), i(1, 2))
        checkUnion(i(2, 4), i(1, 3))

        checkUnion(i_1_4, i(2, 3))
        checkUnion(i(2, 3), i_1_4)
    }

    @Test
    fun add() {
        checkAdd(i(0, 3), 1)
        checkAdd(i_1_4, 0)
        checkAdd(i(2, 5), -1)
    }

    @Test
    fun sub() {
        checkSub(i(0, 3), -1)
        checkSub(i_1_4, 0)
        checkSub(i(2, 5), 1)
    }

    private fun checkContains(interval: Interval, expected: Boolean = true) {
        assertEquals(expected, i_1_4.contains(interval))
    }

    private fun checkContains(lower: Int, upper: Int, expected: Boolean = true) {
        checkContains(i(lower, upper), expected)
    }

    private fun checkNotContains(lower: Int, upper: Int) {
        checkContains(lower, upper, false)
    }

    private fun checkIntersects(interval: Interval, expected: Boolean = true) {
        assertEquals(expected, i_1_4.intersects(interval))
    }

    private fun checkIntersects(lower: Int, upper: Int) {
        checkIntersects(i(lower, upper))
    }

    private fun checkNotIntersects(lower: Int, upper: Int) {
        checkIntersects(i(lower, upper), false)
    }

    private fun assertInterval(interval: Interval) {
        assertEquals(i_1_4, interval)
    }

    private fun checkUnion(i1: Interval, i2: Interval) {
        assertInterval(i1.union(i2))
    }

    private fun checkAdd(interval: Interval, delta: Int) {
        assertInterval(interval.add(delta))
    }

    private fun checkSub(interval: Interval, delta: Int) {
        assertInterval(interval.sub(delta))
    }

}
