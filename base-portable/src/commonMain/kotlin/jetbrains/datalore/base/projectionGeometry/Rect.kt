/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.projectionGeometry

data class Rect<TypeT>(
    val origin: Vec<TypeT>,
    val dimension: Vec<TypeT>
) {
    constructor(
        left: Double,
        top: Double,
        width: Double,
        height: Double
    ) : this(
        Vec(left, top),
        Vec(width, height)
    )
}
