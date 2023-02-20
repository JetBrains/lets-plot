/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

class Polygon<TypeT>(
    rings: List<Ring<TypeT>>
) : AbstractGeometryList<Ring<TypeT>>(rings) {
    constructor(ring: Ring<TypeT>) : this(listOf(ring))

    val bbox: Rect<TypeT>? by lazy(rings.map(Ring<TypeT>::bbox)::union)
}
