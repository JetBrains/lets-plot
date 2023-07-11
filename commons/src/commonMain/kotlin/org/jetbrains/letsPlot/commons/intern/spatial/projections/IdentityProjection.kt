/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial.projections

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

internal class IdentityProjection : Projection {
    override val nonlinear: Boolean = false

    override fun project(v: DoubleVector): DoubleVector = v

    override fun invert(v: DoubleVector): DoubleVector = v

    override fun validDomain(): DoubleRectangle = VALID_RECTANGLE

    companion object {
        private const val INF_DIM = Double.MAX_VALUE / 1000
        private val VALID_RECTANGLE = DoubleRectangle(
            origin = DoubleVector(-INF_DIM / 2, -INF_DIM / 2),
            dimension = DoubleVector(INF_DIM, INF_DIM)
        )
    }
}