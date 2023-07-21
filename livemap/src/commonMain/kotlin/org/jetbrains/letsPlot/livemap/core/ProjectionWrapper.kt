/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toDoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toVec

internal class ProjectionWrapper(
    private val projection: Projection
) : GeoProjection {
    override fun validRect(): Rect<LonLat> = projection.validDomain().toRect()
    override val cylindrical: Boolean = projection.cylindrical
    override fun apply(v: LonLatPoint): GeographicPoint? = projection.project(v.toDoubleVector())?.toVec()
    override fun invert(v: GeographicPoint): LonLatPoint? = projection.invert(v.toDoubleVector())?.toVec()
}