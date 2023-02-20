/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

class MultiPolygon<TypeT>(
    polygons: List<Polygon<TypeT>>
) : AbstractGeometryList<Polygon<TypeT>>(polygons) {
    constructor(polygon: Polygon<TypeT>) : this(listOf(polygon))

    val bbox: Rect<TypeT>? by lazy(polygons.map(Polygon<TypeT>::bbox)::union)
}
