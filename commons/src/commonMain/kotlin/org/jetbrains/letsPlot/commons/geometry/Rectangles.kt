/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

/**
 * X axis positive direction is right;
 * Y axis positive direction is down.
 */
object Rectangles {
    fun zeroOrigin(r: Rectangle): Rectangle {
        return Rectangle(Vector.ZERO, r.dimension)
    }

    fun upperDistance(inner: Rectangle, outer: Rectangle): Int {
        assertOuterInner(outer, inner)
        return topLeft(inner).y - topLeft(outer).y
    }

    fun lowerDistance(inner: Rectangle, outer: Rectangle): Int {
        assertOuterInner(outer, inner)
        return bottomLeft(outer).y - bottomLeft(inner).y
    }

    fun leftDistance(inner: Rectangle, outer: Rectangle): Int {
        assertOuterInner(outer, inner)
        return topLeft(inner).x - topLeft(outer).x
    }

    fun rightDistance(inner: Rectangle, outer: Rectangle): Int {
        assertOuterInner(outer, inner)
        return topRight(outer).x - topRight(inner).x
    }

    private fun assertOuterInner(outer: Rectangle, inner: Rectangle) {
        if (!outer.contains(inner)) {
            throw IllegalArgumentException("Outer does not contain inner: outer = $outer, inner = $inner")
        }
    }

    fun extendUp(r: Rectangle, distance: Int): Rectangle {
        val change = Vector(0, distance)
        return Rectangle(r.origin.sub(change), r.dimension.add(change))
    }

    fun extendDown(r: Rectangle, distance: Int): Rectangle {
        return r.changeDimension(r.dimension.add(Vector(0, distance)))
    }

    fun extendLeft(r: Rectangle, distance: Int): Rectangle {
        val change = Vector(distance, 0)
        return Rectangle(r.origin.sub(change), r.dimension.add(change))
    }

    fun extendRight(r: Rectangle, distance: Int): Rectangle {
        return r.changeDimension(r.dimension.add(Vector(distance, 0)))
    }

    fun extendSides(left: Int, r: Rectangle, right: Int): Rectangle {
        return extendRight(extendLeft(r, left), right)
    }

    fun shrinkRight(r: Rectangle, distance: Int): Rectangle {
        if (r.dimension.x < distance) {
            throw IllegalArgumentException("To small rectangle = $r, distance = $distance")
        }
        return r.changeDimension(r.dimension.sub(Vector(distance, 0)))
    }

    private fun topLeft(r: Rectangle): Vector {
        return r.origin
    }

    fun topRight(r: Rectangle): Vector {
        return r.origin.add(Vector(r.dimension.x, 0))
    }

    private fun bottomLeft(r: Rectangle): Vector {
        return r.origin.add(Vector(0, r.dimension.y))
    }
}