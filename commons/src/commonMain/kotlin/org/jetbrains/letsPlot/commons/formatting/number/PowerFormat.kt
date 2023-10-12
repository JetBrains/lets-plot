/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.*

class PowerFormat(private val base: Int) {
    fun apply(num: Number): String {
        val sign: String = if (num.toDouble() < 0) "-" else ""
        val deg: Int = num.toDouble().absoluteValue.let {
            log(it, base.toDouble()).roundToInt()
        }
        return when (deg) {
            0 -> "${sign}1"
            1 -> "$sign$base"
            else -> "\\($sign$base^{$deg}\\)"
        }
    }

    fun isPowerDegree(value: Number): Boolean {
        if (value.toDouble() == 0.0) {
            return false
        }
        val deg = log(value.toDouble().absoluteValue, base.toDouble())
        return abs(deg - deg.roundToInt()) < POWER_FORMATTING_THRESHOLD
    }

    companion object {
        private const val POWER_FORMATTING_THRESHOLD = 1e-6
    }
}