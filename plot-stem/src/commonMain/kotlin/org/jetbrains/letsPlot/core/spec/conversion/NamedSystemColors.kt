/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.ColorTheme

class NamedSystemColors(
    private val colorTheme: ColorTheme
) {
    fun getColor(id: String): Color? {
        val systemColor = toSystemColor(id) ?: return null
        return when (systemColor) {
            SystemColor.PEN ->  colorTheme.pen()
            SystemColor.PAPER -> colorTheme.paper()
            SystemColor.BRUSH -> colorTheme.brush()
        }
    }

    companion object {
        enum class SystemColor {
            PEN, PAPER, BRUSH;
        }

        private fun toSystemColor(str: String) = when (str.lowercase()) {
            "pen" -> SystemColor.PEN
            "paper" -> SystemColor.PAPER
            "brush" -> SystemColor.BRUSH
            else -> null
        }

        fun isSystemColorName(str: String) = toSystemColor(str) != null
    }
}