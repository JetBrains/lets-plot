/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks

class FixedAxisBreaksProvider(
    override val fixedBreaks: ScaleBreaks
) : AxisBreaksProvider {

    override val isFixedBreaks: Boolean
        get() = true

    override fun getBreaks(targetCount: Int, axisLength: Double): ScaleBreaks {
        return fixedBreaks
    }
}
