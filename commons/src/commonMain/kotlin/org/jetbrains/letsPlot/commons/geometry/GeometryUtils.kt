/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry


object GeometryUtils {
    fun arePolygonsIntersected(pg1: List<DoubleVector>, pg2: List<DoubleVector>): Boolean {
        fun projectPolygon(axis: DoubleVector, polygon: List<DoubleVector>): Pair<Double, Double> {
            val dots = polygon.map { it.dotProduct(axis) }
            return dots.min() to dots.max()
        }

        val edges = listOf(pg1, pg2).flatMap { polygon ->
            polygon.indices.map { i ->
                polygon[i].subtract(polygon[(i + 1) % polygon.size])
                    .orthogonal().normalize()
            }
        }

        return edges.none { axis ->
            val (min1, max1) = projectPolygon(axis, pg1)
            val (min2, max2) = projectPolygon(axis, pg2)
            max1 < min2 || max2 < min1
        }
    }
}

