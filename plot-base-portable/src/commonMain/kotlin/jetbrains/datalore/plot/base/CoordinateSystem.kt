/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleVector

interface CoordinateSystem {
    fun transform(p: DoubleVector): DoubleVector?

    fun toClient(p: DoubleVector): DoubleVector

    fun flip(): CoordinateSystem
}
