/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.toDoubleVector
import jetbrains.datalore.base.typedGeometry.toRect
import jetbrains.datalore.base.typedGeometry.toVec

internal class ProjectionWrapper(
    private val projection: jetbrains.datalore.base.spatial.projections.Projection
) : GeoProjection {
    override fun validRect(): Rect<LonLat> = projection.validDomain().toRect()
    override val cylindrical: Boolean = projection.cylindrical
    override fun apply(v: LonLatPoint): GeographicPoint? = projection.project(v.toDoubleVector())?.toVec()
    override fun invert(v: GeographicPoint): LonLatPoint? = projection.invert(v.toDoubleVector())?.toVec()
}