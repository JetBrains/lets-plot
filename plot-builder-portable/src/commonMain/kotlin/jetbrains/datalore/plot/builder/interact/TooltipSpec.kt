/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TipLayoutHint

class TooltipSpec(
    val layoutHint: TipLayoutHint,
    lines: List<String>,
    val fill: Color,
    val isOutlier: Boolean,
    val showStem: Boolean = true
) {
    val lines: List<String> = ArrayList(lines)

    override fun toString(): String {
        return "TooltipSpec($layoutHint, lines=$lines)"
    }
}
