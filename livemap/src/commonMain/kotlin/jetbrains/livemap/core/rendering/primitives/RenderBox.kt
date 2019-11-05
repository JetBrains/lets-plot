/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector

interface RenderBox : RenderObject {
    val origin: DoubleVector
    val dimension: DoubleVector
}