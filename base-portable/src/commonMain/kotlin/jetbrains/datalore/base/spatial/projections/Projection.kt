/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface Projection {
    fun project(v: DoubleVector): DoubleVector?
    fun invert(v: DoubleVector): DoubleVector?
    fun validRect(): DoubleRectangle
    val nonlinear: Boolean
        get() = true
    val cylindrical: Boolean
}

