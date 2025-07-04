/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalNativeApi::class)

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

class MagickFontManager {
    companion object {
        val DEFAULT = MagickFontManager()
    }

    private val fallbackFont: ResolvedFont
    private val winMonospaceFonts = listOf("Consolas", "Courier New", "Lucida Console", "Courier")
    private val winSerifFonts = listOf("Times New Roman", "Georgia", "Cambria", "Serif")
    private val winSansFonts = listOf("Segoe UI", "Arial", "Tahoma", "Verdana", "Sans")

    private val macMonospaceFonts = listOf("Menlo", "Monaco", "Courier", "Courier New")
    private val macSerifFonts = listOf("Times", "Georgia", "Palatino", "Serif")
    private val macSansFonts = listOf("Helvetica", "Arial", "San Francisco", "Sans")

    private val linuxMonospaceFonts = listOf("DejaVu Sans Mono", "FreeMono", "Noto Mono", "Nimbus Mono", "Liberation Mono", "Courier")
    private val linuxSerifFonts = listOf("DejaVu Serif", "FreeSerif", "Noto Serif", "Nimbus Roman", "Liberation Serif", "Times")
    private val linuxSansFonts =listOf("DejaVu Sans", "FreeSans", "Noto Sans", "Nimbus Sans", "Liberation Sans", "Ubuntu", "Cantarell", "Sans")

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
    private fun log(msg: () -> String) {
        if (logEnabled) {
            println(msg())
        }
    }

    private val cache = mutableMapOf<String, ResolvedFont>()

    init {
        val fonts = findFonts("*")
        if (fonts.isEmpty()) {
            error { "No fonts found." }
        }

        val sansFont = resolveBestMatchingFont(sansFonts)
            ?: resolveBestMatchingFont(findFonts("*sans*").map(FontInfo::family))
            ?: resolveBestMatchingFont(findFonts("*").map(FontInfo::family))!!

        val monospaceFont = resolveBestMatchingFont(monospaceFonts)
            ?: resolveBestMatchingFont(findFonts("*mono*").map(FontInfo::family))
            ?: sansFont


        val serifFont = resolveBestMatchingFont(serifFonts)
            ?: resolveBestMatchingFont(findFonts("*serif*").map(FontInfo::family))
            ?: resolveBestMatchingFont(findFonts("*roman*").map(FontInfo::family))
            ?: sansFont

        fallbackFont = sansFont

        cache["monospace"] = monospaceFont
        cache["mono"] = monospaceFont
        cache["sans"] = sansFont
        cache["sans-serif"] = sansFont
        cache["serif"] = serifFont

        log { "Monospace font: '${monospaceFont.repr}" }
        log { "Serif font: ${serifFont.repr}" }
        log { "Sans font: ${sansFont.repr}" }
        log { "------------------------\n\n" }

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

    fun resolveFont(fontFamily: String): ResolvedFont {
        cache[fontFamily]?.let {
            log { "resolveFont('$fontFamily') -> ${it.repr} (cache)" }
            return it
        }

        val familyFonts = findFamilyFontSet(fontFamily)
        if (familyFonts == null) {
            log { "resolveFont('$fontFamily') -> ${fallbackFont.repr} (not found)" }
            cache[fontFamily] = fallbackFont
            return fallbackFont
        }

        log { "resolveFont('$fontFamily') -> ${familyFonts.repr} (resolved)" }
        val resolvedFont = ResolvedFont.withFontFamily(familyFonts.familyName)

        cache[familyFonts.familyName] = resolvedFont
        return resolvedFont
    }

    private fun resolveBestMatchingFont(families: List<String>): ResolvedFont? {
        log { "resolveBestMatchingFont() - trying families: ${families.joinToString()}" }
        val fontSets = mutableMapOf<String, FamilyFontSet>()
        for (family in families) {
            val fontSet = findFamilyFontSet(family) ?: continue
            if (fontSet.isComplete || fontSet.isCompleteOblique) {
                log { "resolveBestMatchingFont() - found complete font set for family '${fontSet.familyName}'" }
                return ResolvedFont.withFontFamily(fontSet.familyName)
            } else {
                fontSets[family] = fontSet
            }
        }

        // If no complete set is found, return set with the highest score
        val matchingFontSet = fontSets.values.maxByOrNull(FamilyFontSet::score)
        if (matchingFontSet != null) {
            log { "resolveBestMatchingFont() - found best scored font set for family '${matchingFontSet.familyName}'" }
            return ResolvedFont.withFontFamily(matchingFontSet.familyName)
        }

        log { "resolveBestMatchingFont() - no suitable font set found" }
        return null
    }


    private fun findFamilyFontSet(family: String): FamilyFontSet? {
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
        return FamilyFontSet(
            familyName = family,
            normal = fonts.firstOrNull { it.style == FontStyle.NORMAL && it.weight == FontWeight.NORMAL },
            bold = fonts.firstOrNull { it.style == FontStyle.NORMAL && it.weight == FontWeight.BOLD },
            italic = fonts.firstOrNull { it.isItalic && it.weight == FontWeight.NORMAL },
            boldItalic = fonts.firstOrNull { it.isItalic && it.weight == FontWeight.BOLD },
            oblique = fonts.firstOrNull { it.isObliqueItalic && it.weight == FontWeight.NORMAL },
            boldOblique = fonts.firstOrNull { it.isObliqueItalic && it.weight == FontWeight.BOLD },
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

    class ResolvedFont private constructor(
        val fontFamily: String?,
        val fontFilePath: String?
    ) {
        val repr: String = if (fontFamily != null) "'$fontFamily'"
        else if (fontFilePath != null) "'$fontFilePath'"
        else "unknown"

        companion object {
            internal fun withFontFamily(fontFamily: String) = ResolvedFont(fontFamily, null)
            internal fun withFontFilePath(fontFilePath: String) = ResolvedFont(null, fontFilePath)
        }
    }

    private data class FamilyFontSet(
        val familyName: String,
        val normal: FontInfo?,
        val italic: FontInfo?,
        val bold: FontInfo?,
        val boldItalic: FontInfo?,
        val oblique: FontInfo?,
        val boldOblique: FontInfo?,
    ) {
        val isComplete = normal != null && italic != null && bold != null && boldItalic != null
        val isCompleteOblique = normal != null && oblique != null && bold != null && boldOblique != null
        val score = listOfNotNull(normal, italic, bold, boldItalic, oblique, boldOblique).size

        val repr: String
            get() {
                return "$familyName(" +
                        (normal?.let { "n" } ?: "") +
                        (bold?.let { "B" } ?: "") +
                        (italic?.let { "i" } ?: "") +
                        (boldItalic?.let { "I" } ?: "") +
                        (oblique?.let { "o" } ?: "") +
                        (boldOblique?.let { "O" } ?: "") +
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
