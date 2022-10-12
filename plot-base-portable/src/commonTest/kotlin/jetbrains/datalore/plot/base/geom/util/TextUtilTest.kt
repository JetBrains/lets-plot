/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.test.Test
import kotlin.test.assertEquals

class TextUtilTest {

    @Test
    fun horizontal() {
        fun inverse(hjust: String) = if (hjust == "left") "right" else "left"
        fun check(location: DoubleVector, angle: Double, expectedInward: String) {
            assertJustification("inward", isHorizontal = true, location, angle, expectedInward)
            assertJustification("outward", isHorizontal = true, location, angle, inverse(expectedInward))
        }

        // will compare the X
        angles(-45, 45).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "left")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "left")
            check(location = TOP_RIGHT, angle = it, expectedInward = "right")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "right")
        }
        angles(135, 225).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "right")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "right")
            check(location = TOP_RIGHT, angle = it, expectedInward = "left")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "left")
        }

        // will compare the Y
        angles(46, 134).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "right")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "left")
            check(location = TOP_RIGHT, angle = it, expectedInward = "right")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "left")
        }
        angles(-134, -46).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "left")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "right")
            check(location = TOP_RIGHT, angle = it, expectedInward = "left")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "right")
        }
    }

    @Test
    fun vertical() {
        fun inverse(vjust: String) = if (vjust == "top") "bottom" else "top"
        fun check(location: DoubleVector, angle: Double, expectedInward: String) {
            assertJustification("inward", isHorizontal = false, location, angle, expectedInward)
            assertJustification("outward", isHorizontal = false, location, angle, inverse(expectedInward))
        }

        // will compare the Y
        angles(-45, 45).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "top")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "bottom")
            check(location = TOP_RIGHT, angle = it, expectedInward = "top")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "bottom")
        }
        angles(135, 225).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "bottom")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "top")
            check(location = TOP_RIGHT, angle = it, expectedInward = "bottom")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "top")
        }

        // will compare the X
        angles(46, 134).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "top")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "top")
            check(location = TOP_RIGHT, angle = it, expectedInward = "bottom")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "bottom")
        }
        angles(-134, -46).forEach {
            check(location = TOP_LEFT, angle = it, expectedInward = "bottom")
            check(location = BOTTOM_LEFT, angle = it, expectedInward = "bottom")
            check(location = TOP_RIGHT, angle = it, expectedInward = "top")
            check(location = BOTTOM_RIGHT, angle = it, expectedInward = "top")
        }
    }

    @Test
    fun inCenter() {
        // will get central justification
        angles(0, 360, step = 45).forEach {
            assertJustification("inward", isHorizontal = true, location = CENTER, angle = it, expected = "middle")
            assertJustification("outward", isHorizontal = true, location = CENTER, angle = it, expected = "middle")
            assertJustification("inward", isHorizontal = false, location = CENTER, angle = it, expected = "center")
            assertJustification("outward", isHorizontal = false, location = CENTER, angle = it, expected = "center")
        }
    }

    @Test
    fun nonSpecialAlignments() {
        // should not change
        assertJustification("left", isHorizontal = true, location = TOP_LEFT, angle = 0.0, expected = "left")
        assertJustification("right", isHorizontal = true, location = TOP_RIGHT, angle = 90.0, expected = "right")
        assertJustification("top", isHorizontal = false, location = BOTTOM_LEFT, angle = 180.0, expected = "top")
        assertJustification("bottom", isHorizontal = false, location = BOTTOM_RIGHT, angle = 270.0, expected = "bottom")
    }

    companion object {
        val CENTER = DoubleVector.ZERO
        val TOP_LEFT = DoubleVector(-1.0, -1.0)
        val TOP_RIGHT = DoubleVector(1.0, -1.0)
        val BOTTOM_LEFT = DoubleVector(-1.0, 1.0)
        val BOTTOM_RIGHT = DoubleVector(1.0, 1.0)

        fun angles(from: Int, to: Int, step: Int = 15) = (from..to step step).map(Int::toDouble)

        fun assertJustification(
            initialJustification: String,
            isHorizontal: Boolean,
            location: DoubleVector,
            angle: Double,
            expected: String
        ) {
            val result = TextUtil.computeJustification(
                initialJustification,
                angle,
                location,
                CENTER,
                isHorizontal
            )
            assertEquals(
                expected,
                result,
                "${if (isHorizontal) "hjust" else "vjust"}=\'$initialJustification\' at $location with angle=$angle"
            )
        }
    }
}