/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial.projections

import kotlin.math.acos
import kotlin.math.sin

internal class AzimuthalEquidistantProjection : AzimuthalBaseProjection() {
    override fun scale(cxcy: Double): Double = acos(cxcy).let { if (it == 0.0) 0.0 else it / sin(it) }
    override fun angle(z: Double): Double = z
}
