/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface PositionAdjustment {
    val isIdentity: Boolean
        get() = false

    fun handlesGroups(): Boolean

    fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector
}
