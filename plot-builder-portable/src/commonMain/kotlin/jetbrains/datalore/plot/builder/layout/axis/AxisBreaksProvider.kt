/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface AxisBreaksProvider {
    val isFixedBreaks: Boolean

    val fixedBreaks: ScaleBreaks

    fun getBreaks(targetCount: Int, axisLength: Double): ScaleBreaks
}
