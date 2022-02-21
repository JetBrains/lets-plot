/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class LiveMapAxisTheme : AxisTheme {
    override fun showLine(): Boolean = false

    override fun showTickMarks(): Boolean = false

    override fun showLabels(): Boolean = false

    override fun showTitle(): Boolean = false

    override fun showTooltip(): Boolean = false

    override fun titleColor(): Color {
        UNSUPPORTED()
    }

    override fun lineWidth(): Double {
        UNSUPPORTED()
    }

    override fun lineColor(): Color {
        UNSUPPORTED()
    }

    override fun tickMarkColor(): Color {
        UNSUPPORTED()
    }

    override fun labelColor(): Color {
        UNSUPPORTED()
    }

    override fun tickMarkWidth(): Double {
        UNSUPPORTED()
    }

    override fun tickMarkLength(): Double {
        UNSUPPORTED()
    }

    override fun tooltipFill(): Color {
        UNSUPPORTED()
    }

    override fun tooltipColor(): Color {
        UNSUPPORTED()
    }

    override fun tooltipStrokeWidth(): Double {
        UNSUPPORTED()
    }

    override fun tooltipTextColor(): Color {
        UNSUPPORTED()
    }
}