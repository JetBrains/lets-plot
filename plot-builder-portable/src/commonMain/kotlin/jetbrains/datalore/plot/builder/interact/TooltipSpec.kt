/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TipLayoutHint

class TooltipSpec(val layoutHint: TipLayoutHint, val fill: Color, val isOutlier: Boolean, labelValues: List<LabelValue>) {

    constructor(layoutHint: TipLayoutHint, lines: List<String>, fill: Color, isOutlier: Boolean) :
            this(layoutHint, fill, isOutlier, lines.map { LabelValue(null, it) })

    val labelValues: List<LabelValue> = ArrayList(labelValues)
    val lines: List<String> = ArrayList(labelValues.map(LabelValue::toString))


    override fun toString(): String {
        return "TooltipSpec($layoutHint, lines=$lines)"
    }

    class LabelValue(val label: String?, val value: String) {
        override fun toString(): String {
            return if (label.isNullOrEmpty()) value else "${label}: $value"
        }
    }
}