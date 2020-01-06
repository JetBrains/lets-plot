/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projection

import jetbrains.livemap.core.projections.Projection
import jetbrains.livemap.core.projections.ProjectionUtil

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
        ProjectionUtil.square(
            ProjectionUtil.zoom(
                zoom
            )
        )
}