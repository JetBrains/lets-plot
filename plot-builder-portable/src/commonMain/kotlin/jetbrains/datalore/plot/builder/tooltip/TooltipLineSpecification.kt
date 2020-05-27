/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.ValueSource


open class TooltipLineSpecification(
    val label: String,
    val format: String,
    val data: List<ValueSource>
) {
    companion object {

        fun multiValueLine(label: String, format: String, data: List<ValueSource>): TooltipLineSpecification =
            TooltipLineSpecification(label, format, data)

        fun singleValueLine(label: String, format: String, datum: ValueSource): TooltipLineSpecification =
            TooltipLineSpecification(label, format, listOf(datum))
    }
}
