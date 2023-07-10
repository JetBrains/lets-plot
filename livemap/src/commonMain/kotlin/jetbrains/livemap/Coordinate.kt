/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec

/**
 * Coordinates in [0.0.. 256.0]
 */
interface World {
    companion object {
        val ZERO_VEC: Vec<World> = Vec(0.0, 0.0)
        val DOMAIN: Rect<World> = Rect.Companion.XYWH(0.0, 0.0, 256.0, 256.0)
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
