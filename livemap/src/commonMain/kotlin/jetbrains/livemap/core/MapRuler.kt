/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.typedGeometry.Rect

interface MapRuler<TypeT> {
    fun distanceX(x1: Double, x2: Double): Double
    fun distanceY(y1: Double, y2: Double): Double

    fun calculateBoundingBox(xyRects: List<Rect<TypeT>>): Rect<TypeT>
}