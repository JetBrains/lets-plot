/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.FontFamily

class DefaultFontFamilyRegistry constructor(
    private val defaultWidthFactor: Double = 1.0
) : FontFamilyRegistry {

    private val familyByName: MutableMap<String, FontFamily> = HashMap()

    init {
        put("monospace", isMonospased = true)

        // Monospaced fonts from https://en.wikipedia.org/wiki/List_of_monospaced_typefaces
        put("Courier", isMonospased = true)
        put("Consolas", isMonospased = true)
        put("Fixed", isMonospased = true)
        put("Fixedsys", isMonospased = true)
        put("FreeMono", isMonospased = true)
        put("Lucida Console", isMonospased = true)
        put("Monaco", isMonospased = true)
        put("Monofur", isMonospased = true)
        put("OCR-A", isMonospased = true)
        put("OCR-B", isMonospased = true)
        put("Source Code Pro", isMonospased = true)
    }

    override fun get(name: String): FontFamily {
        val key = name.trim().lowercase()
        return familyByName.getOrPut(key) { guessFamily(name) }
    }

    fun put(name: String, isMonospased: Boolean? = null, widthFactor: Double? = null) {
        val key = name.trim().lowercase()
        val wasFamily = familyByName[key]

        val nowMonospaced = isMonospased ?: wasFamily?.monospaced ?: false
        val nowWidthFactor = widthFactor ?: wasFamily?.widthFactor ?: defaultWidthFactor
        familyByName[key] = FontFamily(name, nowMonospaced, nowWidthFactor)
    }

    private fun guessFamily(name: String): FontFamily {
        val monospaced = name.trim().lowercase().endsWith(" mono")
        return FontFamily(name, monospaced, defaultWidthFactor)
    }
}