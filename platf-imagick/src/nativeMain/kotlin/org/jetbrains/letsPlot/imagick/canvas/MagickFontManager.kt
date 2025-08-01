/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalNativeApi::class)

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

class MagickFontManager private constructor(
    private val cache: MutableMap<String, FontSet>
) {
    companion object {
        fun default(): MagickFontManager {
            return MagickFontManager(mutableMapOf())
        }

        fun configured(fonts: Map<String, FontSet>): MagickFontManager {
            return MagickFontManager(fonts.toMutableMap())
        }

        fun configured(vararg fonts: Pair<String, FontSet>): MagickFontManager {
            return MagickFontManager(mapOf(*fonts).toMutableMap())
        }
    }

    private val fallbackFont: FontSet
    private val winMonospaceFonts = listOf("Consolas", "Courier New", "Lucida Console", "Courier")
    private val winSerifFonts = listOf("Times New Roman", "Georgia", "Cambria", "Serif")
    private val winSansFonts = listOf("Segoe UI", "Arial", "Tahoma", "Verdana", "Sans")

    private val macMonospaceFonts = listOf("Menlo", "Monaco", "Courier", "Courier New")
    private val macSerifFonts = listOf("Times", "Georgia", "Palatino", "Serif")
    private val macSansFonts = listOf("Helvetica", "Arial", "San Francisco", "Sans")

    private val linuxMonospaceFonts =
        listOf("DejaVu Sans Mono", "FreeMono", "Noto Mono", "Nimbus Mono", "Liberation Mono", "Courier")
    private val linuxSerifFonts =
        listOf("DejaVu Serif", "FreeSerif", "Noto Serif", "Nimbus Roman", "Liberation Serif", "Times")
    private val linuxSansFonts =
        listOf("DejaVu Sans", "FreeSans", "Noto Sans", "Nimbus Sans", "Liberation Sans", "Ubuntu", "Cantarell", "Sans")

    private val monospaceFonts = when (Platform.osFamily) {
        OsFamily.WINDOWS -> winMonospaceFonts
        OsFamily.MACOSX -> macMonospaceFonts
        OsFamily.LINUX -> linuxMonospaceFonts
        else -> linuxMonospaceFonts + macMonospaceFonts + winMonospaceFonts
    }

    private val serifFonts = when (Platform.osFamily) {
        OsFamily.WINDOWS -> winSerifFonts
        OsFamily.MACOSX -> macSerifFonts
        OsFamily.LINUX -> linuxSerifFonts
        else -> linuxSerifFonts + macSerifFonts + winSerifFonts
    }

    private val sansFonts = when (Platform.osFamily) {
        OsFamily.WINDOWS -> winSansFonts
        OsFamily.MACOSX -> macSansFonts
        OsFamily.LINUX -> linuxSansFonts
        else -> linuxSansFonts + macSansFonts + winSansFonts
    }

    private val logEnabled = false
    private val pseudoFamilyLogEnabled = false
    private fun log(msg: () -> String) {
        if (logEnabled) {
            println(msg())
        }
    }

    init {
        if (cache.isEmpty()) {
            val fonts = findFonts("*")
            if (fonts.isEmpty()) {
                error { "No fonts found." }
            }

            val sansFont = resolveFont(sansFonts)
                ?: resolveFont(findFonts("*sans*").map(FontInfo::family))
                ?: resolveFont(findFonts("*").map(FontInfo::family))!!

            val monospaceFont = resolveFont(monospaceFonts)
                ?: resolveFont(findFonts("*mono*").map(FontInfo::family))
                ?: sansFont


            val serifFont = resolveFont(serifFonts)
                ?: resolveFont(findFonts("*serif*").map(FontInfo::family))
                ?: resolveFont(findFonts("*roman*").map(FontInfo::family))
                ?: sansFont

            cache["monospace"] = monospaceFont
            cache["mono"] = monospaceFont
            cache["sans"] = sansFont
            cache["sans-serif"] = sansFont
            cache["serif"] = serifFont

            if (pseudoFamilyLogEnabled) {
                println("Monospace font: ${monospaceFont.repr}")
                println("Serif font: ${serifFont.repr}")
                println("Sans font: ${sansFont.repr}")
                println("------------------------\n\n")
            }

            if (logEnabled) {
                val families = fonts
                    .groupBy { it.family }
                    .mapValues { (_, fonts) -> fonts.map { it.name } }

                log { "Found ${families.size} families" }

                families.forEach { (familyName, fonts) ->
                    log { "Family: $familyName${fonts.joinToString(prefix = "\n\t", separator = "\n\t")}" }
                }
            }
        }

        fallbackFont = cache["sans"] ?: cache.values.firstOrNull() ?: error("No fonts found")
    }

    fun registerFont(font: Font, filePath: String) {
        log { "registerFont('$font', '$filePath')" }

        val current = cache[font.fontFamily] ?: FontSet(familyName = font.fontFamily)

        cache[font.fontFamily] = current.copy(
            regularFontPath = if (font.isNormal) filePath else current.regularFontPath,
            italicFontPath = if (font.isItalic) filePath else current.italicFontPath,
            boldFontPath = if (font.isBold) filePath else current.boldFontPath,
            boldItalicFontPath = if (font.isBoldItalic) filePath else current.boldItalicFontPath,
        )
    }

    fun resolveFont(fontFamily: String): FontSet {
        log { "resolveFont('$fontFamily')" }
        val cachedFontSet = cache[fontFamily]
        if (cachedFontSet != null) {
            log { "resolveFont('$fontFamily') -> ${cachedFontSet.repr} (fontFile cache)" }
            return cachedFontSet
        }

        val fontSet = findFamilyFontSet(fontFamily)
        if (fontSet != null) {
            log { "resolveFont('$fontFamily') -> ${fontSet.repr} (resolved)" }
            cache[fontSet.familyName] = fontSet
            return fontSet
        }

        log { "resolveFont('$fontFamily') -> ${fallbackFont.repr} (fallback)" }
        cache[fontFamily] = fallbackFont
        return fallbackFont
    }

    private fun resolveFont(families: List<String>): FontSet? {
        log { "resolveBestMatchingFont() - trying families: ${families.joinToString()}" }
        for (family in families) {
            val fontSet = findFamilyFontSet(family) ?: continue
            log { "resolveBestMatchingFont() - found font set for family '${fontSet.familyName}'" }
            return fontSet
        }

        log { "resolveBestMatchingFont() - no suitable font set found" }
        return null
    }


    private fun findFamilyFontSet(family: String): FontSet? {
        // The * wildcard is used to match all fonts in the family,
        // e.g., "DejaVu Sans Bold", "DejaVu Sans Italic" for "DejaVu Sans"
        val fonts = findFonts(family.replace(" ", "?") + "*")
            // Remove unwanted families that might match the pattern, e.g., "DejaVu Sans Mono"
            .filter { it.family == family }

        if (fonts.isEmpty()) {
            log { "findFamilyFontSet('$family') - No fonts found" }
            return null
        }

        log { "findFamilyFontSet('$family') - found ${fonts.size} fonts: ${fonts.joinToString { it.name }}" }
        return FontSet(
            familyName = family,
            regularFontPath = fonts.firstOrNull { it.style == FontStyle.NORMAL && it.weight == FontWeight.NORMAL }?.filePath,
            boldFontPath = fonts.firstOrNull { it.style == FontStyle.NORMAL && it.weight == FontWeight.BOLD }?.filePath,
            italicFontPath = fonts.firstOrNull { it.isItalic && it.weight == FontWeight.NORMAL }?.filePath,
            boldItalicFontPath = fonts.firstOrNull { it.isItalic && it.weight == FontWeight.BOLD }?.filePath,
            obliqueFontPath = fonts.firstOrNull { it.isObliqueItalic && it.weight == FontWeight.NORMAL }?.filePath,
            boldObliqueFontPath = fonts.firstOrNull { it.isObliqueItalic && it.weight == FontWeight.BOLD }?.filePath,
        )
    }

    private fun findFonts(pattern: String): List<FontInfo> {
        val exceptionInfoPtr = ImageMagick.AcquireExceptionInfo() ?: error("Failed to acquire exception info")
        var typeInfoPtr: CPointer<CPointerVarOf<CPointer<ImageMagick.TypeInfo>>>? = null
        try {
            memScoped {
                val typesCount: ULongVarOf<size_t> = alloc<size_tVar>()
                typeInfoPtr = ImageMagick.GetTypeInfoList(pattern, typesCount.ptr, exceptionInfoPtr)

                if (typeInfoPtr == null) {
                    log { "Failed to get type info list for '$pattern': ${exceptionInfoPtr.pointed.description?.toKString() ?: "Unknown error"}" }
                    return emptyList()
                }

                val typeInfoList = mutableListOf<FontInfo>()
                for (i in 0 until typesCount.value.toInt()) {
                    val typeInfo = typeInfoPtr?.get(i)?.pointed ?: continue
                    if (typeInfo.family == null || typeInfo.name == null) {
                        log { "Skipping type info with null family or name: $typeInfo" }
                        continue
                    }

                    if (typeInfo.stealth > 0u) {
                        log { "Skipping stealth type info: ${typeInfo.name?.toKStringFromUtf8() ?: "unknown"}" }
                        continue
                    }

                    typeInfoList += FontInfo(
                        family = typeInfo.family!!.toKStringFromUtf8(),
                        name = typeInfo.name!!.toKStringFromUtf8(),
                        style = when (typeInfo.style) {
                            ImageMagick.StyleType.ItalicStyle, ImageMagick.StyleType.ObliqueStyle -> FontStyle.ITALIC
                            else -> FontStyle.NORMAL
                        },
                        weight = when {
                            typeInfo.style == ImageMagick.StyleType.BoldStyle -> FontWeight.BOLD
                            typeInfo.weight >= 700u -> FontWeight.BOLD
                            else -> FontWeight.NORMAL
                        },
                        oblique = typeInfo.style == ImageMagick.StyleType.ObliqueStyle,
                        filePath = typeInfo.glyphs?.toKStringFromUtf8() ?: ""
                    )
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

    data class FontSet(
        val familyName: String,
        val regularFontPath: String? = null,
        val italicFontPath: String? = null,
        val boldFontPath: String? = null,
        val boldItalicFontPath: String? = null,
        val obliqueFontPath: String? = null,
        val boldObliqueFontPath: String? = null,
    ) {
        val repr: String
            get() {
                return "$familyName(" +
                        (regularFontPath?.let { "n" } ?: "") +
                        (boldFontPath?.let { "B" } ?: "") +
                        (italicFontPath?.let { "i" } ?: "") +
                        (boldItalicFontPath?.let { "I" } ?: "") +
                        (obliqueFontPath?.let { "o" } ?: "") +
                        (boldObliqueFontPath?.let { "O" } ?: "") +
                        ")"
            }
    }

    private data class FontInfo(
        val family: String,
        val name: String,
        val style: FontStyle,
        val weight: FontWeight,
        val oblique: Boolean,
        val filePath: String,
    ) {
        val isObliqueItalic = style == FontStyle.ITALIC && oblique
        val isItalic = style == FontStyle.ITALIC && !oblique
    }
}
