/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.test.Test
import kotlin.test.assertTrue

class DistanceTest {
    @Test
    fun simpleDistance() {
        val dist = DoubleSegment(DoubleVector(0.0, 0.0), DoubleVector(0.0, 25.0)).distance(DoubleVector(20.0, 30.0))
        val dist2 = DoubleSegment(DoubleVector(0.0, 25.0), DoubleVector(0.0, 50.0))
                .distance(DoubleVector(20.0, 30.0))
        assertTrue(dist2 < dist)
        val dist3 = DoubleRectangle(DoubleVector(50.0, 10.0), DoubleVector(601.0, 25.0))
                .distance(DoubleVector(676.0, 42.0))
        val dist4 = DoubleRectangle(DoubleVector(50.0, 35.0), DoubleVector(601.0, 42.0))
                .distance(DoubleVector(676.0, 42.0))
        assertTrue(dist4 < dist3)
    }
}