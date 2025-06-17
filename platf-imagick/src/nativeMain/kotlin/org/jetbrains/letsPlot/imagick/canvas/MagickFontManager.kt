/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import ImageMagick.ExceptionInfo
import kotlinx.cinterop.*
import platform.posix.size_t
import platform.posix.size_tVar

class MagickFontManager {
    class ResolvedFont(
        val fontFamily: String?,
        val fontFilePath: String?
    ) {
        init {
            require(fontFamily != null || fontFilePath != null) {
                "Either fontFamily or fontFilePath must be provided."
            }
        }

        companion object {
            internal fun withFontFamily(fontFamily: String) = ResolvedFont(fontFamily, null)
            internal fun withFontFilePath(fontFilePath: String) = ResolvedFont(null, fontFilePath)
        }
    }

    companion object {
        private val preferredMonospaceFonts = listOf(
            "DejaVu Sans Mono", // Linux default monospace
            "Courier New",      // Windows default monospace
            "Courier",          // macOS default monospace
            "Consolas",         // Windows code editor font
            "Menlo",            // macOS default Terminal font
            "Monaco",           // macOS legacy monospace
            "Roboto Mono",      // Chrome/Android web-safe
            "Noto Mono",        // Noto variant
            "Liberation Mono",  // Metric-compatible with Courier
            "Ubuntu Mono",      // Ubuntu Terminal default
            "Nimbus Mono",      // Ghostscript fallback
            "FreeMono"          // Full Unicode mono
        )

        private val preferredSerifFonts = listOf(
            "Times New Roman",  // Windows default serif
            "Times",            // macOS classic serif
            "Georgia",          // Widely used web font
            "Roboto Slab",      // Google’s web-safe serif
            "Noto Serif",       // Google’s multilingual serif
            "DejaVu Serif",     // Common Linux serif
            "Liberation Serif", // Metric-compatible with Times
            "Nimbus Roman",     // Ghostscript fallback
            "FreeSerif"         // Full Unicode serif
        )

        private val preferredSansFonts = listOf(
            "Helvetica",        // macOS default sans-serif
            "Arial",            // Windows default sans-serif
            "Segoe UI",         // Windows UI font
            "Roboto",           // Android, Chrome OS, and Chrome browser
            "Noto Sans",        // Google’s fallback for web fonts
            "DejaVu Sans",      // Common on Linux, fallback for sans
            "Liberation Sans",  // Metric-compatible with Arial
            "Cantarell",        // GNOME default sans
            "Ubuntu",           // Ubuntu system font
            "Verdana",          // Windows screen-optimized
            "Tahoma",           // Windows XP–7 UI font
            "Nimbus Sans"       // Ghostscript/FreeType fallback
        )

        private const val logEnabled = false
        private fun log(msg: () -> String) {
            if (logEnabled) {
                println(msg())
            }
        }

        val DEFAULT = MagickFontManager()

        fun create(): MagickFontManager {
            return MagickFontManager()
        }
    }


    private val monospaceFont: ResolvedFont = resolveFont(preferredMonospaceFonts)
    private val serifFont: ResolvedFont = resolveFont(preferredSerifFonts)
    private val sansFont: ResolvedFont = resolveFont(preferredSansFonts)
    private val fallbackFont: ResolvedFont = sansFont

    init {
        println("Monospace font: ${monospaceFont.fontFamily}")
        println("Serif font: ${serifFont.fontFamily}")
        println("Sans font: ${sansFont.fontFamily}")
        println("------------------------\n\n")

        allFamilies()
    }

    fun resolveFont(fontFamily: String): ResolvedFont {
        return resolveFont(listOf(fontFamily))
    }

    fun allFamilies() {
        val types = getTypeInfoList("*")
        val families = types
            .filter { it.stealth == 0u }
            .groupBy { it.family!!.toKString() }
            .mapValues { (_, fonts) -> fonts.map { it.name?.toKStringFromUtf8() ?: "unknown" } }

        log { "Found ${families.size} families" }
        families.forEach { (family, fonts) ->
            log { "Family: $family${fonts.joinToString(prefix = "\n\t")}" }
        }
    }

    private fun resolveFont(fontFamilies: List<String>): ResolvedFont {
        for (fontFamily in fontFamilies) {
            when (fontFamily.lowercase()) {
                "monospace" -> return monospaceFont
                "serif" -> return serifFont
                "sans", "sans-serif" -> return sansFont
            }

            val types = getTypeInfoList(fontFamily)

            log { "Searching for type info for font '$fontFamily': found ${types.size} types" }

            for (type in types) {
                if (type.stealth > 0u) {
                    log { "Skipping stealth font: ${type.name?.toKStringFromUtf8() ?: "unknown"}" }
                    continue
                }

                if (type.family?.toKStringFromUtf8()?.isNullOrBlank() == true) {
                    log { "Skipping type with empty family name: ${type.name?.toKStringFromUtf8() ?: "unknown"}" }
                    continue
                }

                val family = type.family!!.toKStringFromUtf8()
                log { "Found type info for font '$family'" }
                return ResolvedFont.withFontFamily(family)
            }
        }
        return fallbackFont
    }

    private fun getTypeInfoList(fontFamily: String): List<ImageMagick.TypeInfo> {
        val pattern = fontFamily.replace(" ", "?")
        val exceptionInfoPtr = ImageMagick.AcquireExceptionInfo() ?: error("Failed to acquire exception info")
        var typeInfoPtr: CPointer<CPointerVarOf<CPointer<ImageMagick.TypeInfo>>>? = null
        try {
            memScoped {
                val typesCount: ULongVarOf<size_t> = alloc<size_tVar>()
                typeInfoPtr = ImageMagick.GetTypeInfoList(pattern, typesCount.ptr, exceptionInfoPtr)

                if (typeInfoPtr == null) {
                    println("Failed to get type info list for '$pattern': ${exceptionInfoPtr.pointed.description?.toKString() ?: "Unknown error"}")
                    return emptyList()
                }

                val typeInfoList = mutableListOf<ImageMagick.TypeInfo>()
                for (i in 0 until typesCount.value.toInt()) {
                    val typeInfo = typeInfoPtr?.get(i) ?: continue
                    typeInfoList.add(typeInfo.pointed)
                }

                return typeInfoList
            }
        } finally {
            ImageMagick.MagickRelinquishMemory(exceptionInfoPtr)

            if (typeInfoPtr != null) {
                ImageMagick.MagickRelinquishMemory(typeInfoPtr)
            }
        }
    }
}
