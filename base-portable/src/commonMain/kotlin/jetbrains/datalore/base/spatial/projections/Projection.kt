/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial.projections

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface Projection {
    val nonlinear: Boolean
        get() = true
    val cylindrical: Boolean
        get() = if (nonlinear) false else error("'cylindrical' is irrelevant for 'linear' projection ${this::class.simpleName}")

    fun project(v: DoubleVector): DoubleVector?
    fun invert(v: DoubleVector): DoubleVector?

    fun validDomain(): DoubleRectangle
}

