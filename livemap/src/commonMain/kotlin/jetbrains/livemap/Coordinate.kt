/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec

/**
 * Coordinates in [0.0.. 256.0]
 */
interface World {
    companion object {
        val ZERO_VEC: Vec<World> = Vec(0.0, 0.0)
        const val MIN_X = 0.0
        const val MIN_Y = 0.0
        const val MAX_X = 256.0
        const val MAX_Y = 256.0
        const val WIDTH = MAX_X - MIN_X
        const val HEIGHT = MAX_Y - MIN_Y
    }
}

/**
 * Coordinates used by Context2d
 */
interface Client {
    companion object {
        val ZERO_VEC: Vec<Client> = Vec(0.0, 0.0)
    }
}

typealias ClientPoint = Vec<Client>
typealias WorldPoint = Vec<World>
typealias WorldRectangle = Rect<World>

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toClientPoint() = explicitVec<Client>(x, y)
