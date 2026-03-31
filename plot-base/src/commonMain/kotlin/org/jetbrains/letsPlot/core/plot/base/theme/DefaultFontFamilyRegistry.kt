/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.FontFamily

class DefaultFontFamilyRegistry constructor(
    private val defaultWidthFactor: Double = 1.0
) : FontFamilyRegistry {

    private val familyByName: MutableMap<String, FontFamily> = HashMap()

    init {
        put("monospace", isMonospaced = true)

        // Monospaced fonts from https://en.wikipedia.org/wiki/List_of_monospaced_typefaces
        put("Courier", isMonospaced = true)
        put("Consolas", isMonospaced = true)
        put("Fixed", isMonospaced = true)
        put("Fixedsys", isMonospaced = true)
        put("FreeMono", isMonospaced = true)
        put("Lucida Console", isMonospaced = true)
        put("Monaco", isMonospaced = true)
        put("Monofur", isMonospaced = true)
        put("OCR-A", isMonospaced = true)
        put("OCR-B", isMonospaced = true)
        put("Source Code Pro", isMonospaced = true)
    }

    override fun get(name: String): FontFamily {
        val key = name.trim().lowercase()
        return familyByName.getOrPut(key) { guessFamily(name) }
    }

    fun put(name: String, isMonospaced: Boolean? = null, widthFactor: Double? = null) {
        val key = name.trim().lowercase()
        val wasFamily = familyByName[key]

        val nowMonospaced = isMonospaced ?: wasFamily?.monospaced ?: false
        val nowWidthFactor = widthFactor ?: wasFamily?.widthFactor ?: defaultWidthFactor
        familyByName[key] = FontFamily(name, nowMonospaced, nowWidthFactor)
    }

    private fun guessFamily(name: String): FontFamily {
        val monospaced = name.trim().lowercase().endsWith(" mono")
        return FontFamily(name, monospaced, defaultWidthFactor)
    }
}