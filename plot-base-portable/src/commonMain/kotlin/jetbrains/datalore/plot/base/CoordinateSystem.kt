/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.projections.Projection

interface CoordinateSystem {

    val projection: Projection

    fun toClient(p: DoubleVector): DoubleVector

    fun fromClient(p: DoubleVector): DoubleVector

    fun flip(): CoordinateSystem
}
