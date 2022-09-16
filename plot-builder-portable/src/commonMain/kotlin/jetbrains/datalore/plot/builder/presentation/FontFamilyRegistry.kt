/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL_LICIDA
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL_SERIF

class FontFamilyRegistry(private val defaultWidthFactor: Double = 1.0) {
    private val defaultMonospaced = false
    private val familyByName: MutableMap<String, FontFamily> = HashMap()

    init {
        // init defaults
        put(FONT_FAMILY_NORMAL_LICIDA)
        put(FONT_FAMILY_NORMAL_SERIF)
        put(FONT_FAMILY_NORMAL)

        // ToDo: add few monospaced fonts
    }

    fun put(name: String, isMonospased: Boolean? = null, widthFactor: Double? = null): FontFamily {
        val key = name.trim().lowercase()
        val wasFamily = familyByName[key]

        val nowMonospaced = isMonospased ?: wasFamily?.monospaced ?: defaultMonospaced
        val nowWidthFactor = widthFactor ?: wasFamily?.widthFactor ?: defaultWidthFactor
        val nowFamily = FontFamily(name, nowMonospaced, nowWidthFactor)
        familyByName[key] = nowFamily
        return nowFamily
    }

    fun get(name: String): FontFamily {
        val key = name.trim().lowercase()
        return familyByName.getOrPut(key) { family(name, defaultMonospaced) }
    }

    private fun family(name: String, monospaced: Boolean): FontFamily {
        return FontFamily(name, monospaced, defaultWidthFactor)
    }
}