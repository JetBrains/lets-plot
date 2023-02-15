/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

class MultiPoint<TypeT>(
    points: List<Vec<TypeT>>
) : AbstractGeometryList<Vec<TypeT>>(points) {
    constructor(point: Vec<TypeT>) : this(listOf(point))

    val bbox: Rect<TypeT>? by lazy(this::boundingBox)
}