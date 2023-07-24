/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor

class NamedSystemColors(private val themeFlavor: ThemeFlavor?) {

    fun getColor(id: String): Color? {
        val systemColor = fromString(id) ?: return null
        return when (systemColor) {
            SystemColorId.SYSTEM_BLUE -> Color.PACIFIC_BLUE
            SystemColorId.SYSTEM_WHITE -> themeFlavor?.systemLight ?: Color.WHITE
            SystemColorId.SYSTEM_BLACK -> themeFlavor?.systemDark ?: Color.BLACK
        }
    }

    companion object {
        enum class SystemColorId {
            SYSTEM_BLUE, SYSTEM_WHITE, SYSTEM_BLACK;
        }

        private fun fromString(str: String): SystemColorId? {
            val normalized = str
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "")
            return when (normalized) {
                "sysblue" -> SystemColorId.SYSTEM_BLUE
                "syswhite" -> SystemColorId.SYSTEM_WHITE
                "sysblack" -> SystemColorId.SYSTEM_BLACK
                else -> null
            }
        }

        fun isSystemColorName(str: String) = fromString(str) != null
    }
}