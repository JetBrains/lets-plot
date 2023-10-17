/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.*

class PowerFormat(
    private val base: Int,
    private val formattingThreshold: Double
) {
    fun apply(value: Number): String {
        val sign: String = if (value.toDouble() < 0) "-" else ""
        val powerDegree = getPowerDegreeOrNull(value.toDouble()) ?: return value.toString()
        return when (powerDegree.degree) {
            0 -> "$sign${powerDegree.coefficient}"
            1 -> "$sign${base * powerDegree.coefficient}"
            else -> {
                val coefficient = if (powerDegree.coefficient > 1) {
                    "${powerDegree.coefficient}$MULTIPLICATION_OPERATOR"
                } else {
                    ""
                }
                "\\($sign$coefficient$base^{${powerDegree.degree}}\\)"
            }
        }
    }

    fun isPowerDegreeLike(value: Double): Boolean {
        return getPowerDegreeOrNull(value) != null
    }

    private fun getPowerDegreeOrNull(value: Double): PowerDegree? {
        if (value == 0.0) {
            return null
        }
        for (coefficient in 1 until base) {
            val deg = log(value.absoluteValue / coefficient, base.toDouble())
            if (abs(deg - deg.roundToInt()) < formattingThreshold) {
                return PowerDegree(coefficient, deg.roundToInt())
            }
        }
        return null
    }

    data class PowerDegree(val coefficient: Int, val degree: Int)

    companion object {
        const val MULTIPLICATION_OPERATOR = "\\cdot"
    }
}