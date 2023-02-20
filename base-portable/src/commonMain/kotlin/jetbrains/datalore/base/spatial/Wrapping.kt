/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.abs

/**
 * Wrap the path through the shortest way, including a path that goes through borders.
 * Domain.width = 20
 * x1=3, x2=7
 * x2-x1=3 => path doesn't go though borders
 * |   .---.            |
 *
 * Domain.width = 20
 * x1=3, x2=16
 * x2-x1=13 => shortest path goes through borders
 * |---.            .---|
 */
fun <TypeT> wrapPath(path: List<Vec<TypeT>>, domain: Rect<TypeT>): List<List<Vec<TypeT>>> {
    if (path.isEmpty()) {
        return emptyList()
    }

    val splitPath = ArrayList<List<Vec<TypeT>>>()
    var currentPath = ArrayList<Vec<TypeT>>()
    currentPath.add(path[0])

    fun yOnLine(p1: Vec<TypeT>, p2: Vec<TypeT>, x: Double): Double {
        // the Equation for the Line
        // y = m * x + b
        // Where
        // m = (y2 - y1) / (x2 - x1)
        // and b computed by substitution p1 or p2 to the equation of the line

        val m = (p2.y - p1.y) / ((p2.x) - p1.x)
        val b = p2.y - m * (p2.x)

        // Result
        return m * x + b
    }

    for (i in 1 until path.size) {
        val p1 = path[i - 1]
        val p2 = path[i]

        if (abs(p2.x - p1.x) > domain.dimension.x / 2) {
            val xa: Double
            val xb: Double
            val y: Double

            if (p1.x < p2.x) {
                y = yOnLine(p1, p2.copy(x = p2.x - domain.width), x = domain.left)
                xa = domain.left
                xb = domain.right
            } else {
                y = yOnLine(p1, p2.copy(x = p2.x + domain.width), x = domain.right)
                xa = domain.right
                xb = domain.left
            }
            currentPath.add(explicitVec(xa, y))
            splitPath.add(currentPath)
            currentPath = ArrayList()
            currentPath.add(explicitVec(xb, y))
        }

        currentPath.add(p2)
    }

    splitPath.add(currentPath)
    return splitPath
}
