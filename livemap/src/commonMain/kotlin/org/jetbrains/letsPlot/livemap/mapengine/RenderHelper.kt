/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.Transforms
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport

class RenderHelper(
    private val viewport: Viewport
) {
    private val dimWorldToClientTransform = Transforms.scale(::zoomFactor)
    val zoomFactor get()  = Transforms.zoomFactor(viewport.zoom)

    fun dimToScreen(p: Vec<World>): Vec<Client> {
        return Vec(
            dimWorldToClientTransform.apply(p.x),
            dimWorldToClientTransform.apply(p.y)
        )
    }

    fun posToWorld(p: Vec<Client>): Vec<World> {
        return viewport.getMapCoord(p)
    }

    fun dimToWorld(clientDimension: Double): Scalar<World> {
        return Scalar(dimWorldToClientTransform.invert(clientDimension))
    }

    fun dimToWorld(clientDimension: Scalar<Client>): Scalar<World> {
        return dimToWorld(clientDimension.value)
    }

    fun dimToClient(worldDimension: Double): Scalar<Client> {
        return Scalar(dimWorldToClientTransform.apply(worldDimension))
    }

    fun dimToClient(worldDimension: Scalar<World>): Scalar<Client> {
        return dimToClient(worldDimension.value)
    }
}
