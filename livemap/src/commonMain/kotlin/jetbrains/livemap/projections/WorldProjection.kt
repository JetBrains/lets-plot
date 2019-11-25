/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

class WorldProjection(
    zoom: Int
) : Projection<WorldPoint, ClientPoint> {
    override fun project(v: WorldPoint): ClientPoint {
        return projector.project(v)
    }

    override fun invert(v: ClientPoint): WorldPoint {
        return projector.invert(v)
    }

    private val projector: Projection<WorldPoint, ClientPoint> =
        ProjectionUtil.square(ProjectionUtil.zoom(zoom))
}