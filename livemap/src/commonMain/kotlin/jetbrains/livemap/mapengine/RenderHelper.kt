/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.typedGeometry.Scalar
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.newVec
import jetbrains.datalore.base.typedGeometry.scalarX
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.mapengine.viewport.Viewport
import kotlin.math.pow

class RenderHelper(
    private val viewport: Viewport
) {
    val zoomFactor get()  = 2.0.pow(viewport.zoom)

    fun toWorld(v: Scalar<Client>): Scalar<World> {
        return viewport.toWorldDimension(newVec(v, v)).scalarX
    }

    fun toScreen(p: Vec<World>): Vec<Client> {
        return Vec(p.x * zoomFactor, p.y * zoomFactor)
    }
}
