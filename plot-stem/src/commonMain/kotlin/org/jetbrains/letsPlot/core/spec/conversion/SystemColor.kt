/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

enum class SystemColor {
    PEN, PAPER, BRUSH;

    internal companion object {
        fun canParse(str: String) = when (str.lowercase()) {
            "pen", "paper", "brush" -> true
            else -> false
        }

        fun parse(str: String): SystemColor = when (str.lowercase()) {
            "pen" -> PEN
            "paper" -> PAPER
            "brush" -> BRUSH
            else -> throw IllegalStateException("Expected system color ID: [pen|paper|brush] but was '$str'")
        }
    }
}