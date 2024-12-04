/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class SeriesAnnotationOptions : Options() {
    var column: String? by map(Option.Meta.SeriesAnnotation.COLUMN)
    var type: Types? by map(Option.Meta.SeriesAnnotation.TYPE)
    var order: Order? by map(Option.Meta.SeriesAnnotation.ORDER)
    var factorLevels: List<Any?>? by map(Option.Meta.SeriesAnnotation.FACTOR_LEVELS)

    enum class Order(
        val value: Int
    ) {
        ASCENDING(1),
        DESCENDING(-1)
    }

    enum class Types(
        val value: String
    ) {
        DATE_TIME(Option.Meta.SeriesAnnotation.Types.DATE_TIME),
        INTEGER(Option.Meta.SeriesAnnotation.Types.INTEGER),
        FLOATING(Option.Meta.SeriesAnnotation.Types.FLOATING),
        STRING(Option.Meta.SeriesAnnotation.Types.STRING),
        BOOLEAN(Option.Meta.SeriesAnnotation.Types.BOOLEAN),
        UNKNOWN(Option.Meta.SeriesAnnotation.Types.UNKNOWN);

        companion object {
            fun from(value: String): Types? {
                return entries.firstOrNull { it.value == value }
            }
        }
    }
}
