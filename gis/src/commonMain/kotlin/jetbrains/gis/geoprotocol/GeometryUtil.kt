/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.projectionGeometry.*

object GeometryUtil {
    fun <TypeT> bbox(multipolygon: MultiPolygon<TypeT>): Rect<TypeT>? {
        val rects = multipolygon.limit()
        return if (rects.isEmpty()) {
            null
        } else DoubleRectangles.boundingBox(
            sequenceOf(
                rects.asSequence().map { it.origin },
                rects.asSequence().map { it.origin + it.dimension }
            ).flatten().asIterable()
        )
    }

    fun <TypeT> asLineString(geometry: MultiPolygon<TypeT>): LineString<TypeT> {
        return LineString(geometry[0][0])
    }
}
