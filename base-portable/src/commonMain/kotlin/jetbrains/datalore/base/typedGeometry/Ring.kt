/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.algorithms.isClockwise

class Ring<TypeT>(points: List<Vec<TypeT>>) : AbstractGeometryList<Vec<TypeT>>(points) {
    val bbox: Rect<TypeT>? by lazy(this::boundingBox)
    val isClockwise: Boolean by lazy { isClockwise(this, Vec<TypeT>::x, Vec<TypeT>::y) }
}