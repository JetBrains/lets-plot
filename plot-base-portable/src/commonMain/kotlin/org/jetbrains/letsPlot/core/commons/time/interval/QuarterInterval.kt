/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

internal class QuarterInterval(count: Int) : TimeInterval(count) {

    override val tickFormatPattern: String
        get() = "Q"

    override fun range(start: Double, end: Double): List<Double> {
        throw UnsupportedOperationException()
    }

}
