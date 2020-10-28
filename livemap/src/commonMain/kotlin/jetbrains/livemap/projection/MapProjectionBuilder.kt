/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projection

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.livemap.core.projections.GeoProjection
import jetbrains.livemap.core.projections.Geographic
import jetbrains.livemap.core.projections.ProjectionType
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.core.projections.ProjectionUtil.transformBBox
import kotlin.math.min

internal class MapProjectionBuilder(
    private val geoProjection: GeoProjection,
    private val mapRect: WorldRectangle
) {
    private var reverseX = false
    private var reverseY = false

    fun reverseX(): MapProjectionBuilder {
        reverseX = true
        return this
    }

    fun reverseY(): MapProjectionBuilder {
        reverseY = true
        return this
    }

    fun create(): MapProjection {
        val rect = transformBBox(geoProjection.validRect(), geoProjection::project)

        val scale = min(mapRect.width / rect.width, mapRect.height / rect.height)

        @Suppress("UNCHECKED_CAST")
        val projSize = (mapRect.dimension * (1.0 / scale)) as Vec<Geographic>
        val projRect = Rect(rect.center - projSize * 0.5, projSize)

        val offsetX = if (reverseX) projRect.right else projRect.left
        val scaleX = if (reverseX) -scale else scale
        val offsetY = if (reverseY) projRect.bottom else projRect.top
        val scaleY = if (reverseY) -scale else scale

        val linearProjection =
            ProjectionUtil.tuple<Geographic, World>(
                ProjectionUtil.linear(offsetX, scaleX),
                ProjectionUtil.linear(offsetY, scaleY)
            )

        val proj =
            ProjectionUtil.composite(geoProjection, linearProjection)

        return object : MapProjection {

            override val mapRect: WorldRectangle
                get() = this@MapProjectionBuilder.mapRect

            override fun project(v: LonLatPoint): WorldPoint {
                return proj.project(v)
            }

            override fun invert(v: WorldPoint): LonLatPoint {
                return proj.invert(v)
            }
        }
    }
}


fun createMapProjection(projectionType: ProjectionType, mapRect: WorldRectangle): MapProjection {
    return MapProjectionBuilder(
        ProjectionUtil.createGeoProjection(
            projectionType
        ), mapRect
    )
        .reverseY()
        .create()
}