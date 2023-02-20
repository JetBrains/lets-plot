/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

class MultiLineString<TypeT>(
    geometry: List<LineString<TypeT>>
) : AbstractGeometryList<LineString<TypeT>>(geometry) {
    constructor(linString: LineString<TypeT>) : this(listOf(linString))

    val bbox: Rect<TypeT>? by lazy(geometry.map(LineString<TypeT>::bbox)::union)
}