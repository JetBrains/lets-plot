/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.livemap.Client

class ArrowSpec(
    val angle: Double,
    val length: Scalar<Client>,
    val end: End,
    val type: Type
) {
    val isOnFirstEnd: Boolean
        get() = end == End.FIRST || end == End.BOTH

    val isOnLastEnd: Boolean
        get() = end == End.LAST || end == End.BOTH

    enum class End {
        LAST, FIRST, BOTH
    }

    enum class Type {
        OPEN, CLOSED
    }
}
