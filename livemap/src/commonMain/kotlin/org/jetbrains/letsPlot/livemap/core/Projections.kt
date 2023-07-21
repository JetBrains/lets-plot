/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import kotlin.math.PI

object Projections {
    fun mercator(): GeoProjection = ProjectionWrapper(org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator())
    fun geographic(): GeoProjection = ProjectionWrapper(org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator())
    fun azimuthalEqualArea(): GeoProjection =
        ProjectionWrapper(org.jetbrains.letsPlot.commons.intern.spatial.projections.azimuthalEqualArea())
    fun conicEqualArea(): GeoProjection =
        ProjectionWrapper(org.jetbrains.letsPlot.commons.intern.spatial.projections.conicEqualArea(0.0, PI / 3))


}