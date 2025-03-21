/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.repel

import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleVectorExtensions.dot
import kotlin.math.sqrt

class DoubleCircle(val center: DoubleVector, val radius: Double) {
    fun intersects(line: DoubleSegment): Boolean {
        val d = line.end.subtract(line.start)
        val f = line.start.subtract(center)

        val a = d.dot(d)
        val b = 2 * f.dot(d)
        val c = f.dot(f) - radius * radius

        val discriminant = b * b - 4 * a * c

        if (discriminant < 0) {
            return false
        }

        val sqrtDiscriminant = sqrt(discriminant)
        val t1 = (-b - sqrtDiscriminant) / (2 * a)
        val t2 = (-b + sqrtDiscriminant) / (2 * a)

        return (t1 in 0.0..1.0) || (t2 in 0.0..1.0)
    }
}