/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalNativeApi::class)

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import platform.posix.size_t
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

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

        private val winMonospaceFonts = listOf("Consolas", "Courier New", "Lucida Console", "Courier")
        private val winSerifFonts = listOf("Times New Roman", "Georgia", "Cambria", "Serif")
        private val winSansFonts = listOf("Segoe UI", "Arial", "Tahoma", "Verdana", "Sans")

        private val macMonospaceFonts = listOf("Menlo", "Monaco", "Courier", "Courier New")
        private val macSerifFontsOS = listOf("Times", "Georgia", "Palatino", "Serif")
        private val macSansFonts = listOf("Helvetica", "Arial", "San Francisco", "Sans")

        private val linuxMonospaceFonts = listOf("DejaVu Sans Mono", "Liberation Mono", "Noto Mono", "FreeMono", "Courier")
        private val linuxSerifFonts = listOf("DejaVu Serif", "Liberation Serif", "Noto Serif", "FreeSerif", "Times")
        private val linuxSansFonts = listOf("DejaVu Sans", "Liberation Sans", "Noto Sans", "FreeSans", "Ubuntu", "Cantarell", "Sans")

        private val isLinux = Platform.osFamily == OsFamily.LINUX
        private val isWindows = Platform.osFamily == OsFamily.WINDOWS
        private val isMac = Platform.osFamily == OsFamily.MACOSX
        
        private val preferredMonospaceFonts = when (Platform.osFamily) {
            OsFamily.WINDOWS -> winMonospaceFonts
            OsFamily.MACOSX -> macMonospaceFonts
            OsFamily.LINUX -> linuxMonospaceFonts
            else -> linuxMonospaceFonts + macMonospaceFonts + winMonospaceFonts
        }
        
        private val preferredSerifFonts = when (Platform.osFamily) {
            OsFamily.WINDOWS -> winSerifFonts
            OsFamily.MACOSX -> macSerifFontsOS
            OsFamily.LINUX -> linuxSerifFonts
            else -> linuxSerifFonts + macSerifFontsOS + winSerifFonts
        }
        
        private val preferredSansFonts = when (Platform.osFamily) {
            OsFamily.WINDOWS -> winSansFonts
            OsFamily.MACOSX -> macSansFonts
            OsFamily.LINUX -> linuxSansFonts
            else -> linuxSansFonts + macSansFonts + winSansFonts
        }

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
        log { "Monospace font: ${monospaceFont.fontFamily}" }
        log { "Serif font: ${serifFont.fontFamily}" }
        log { "Sans font: ${sansFont.fontFamily}" }
        log { "------------------------\n\n" }

        if (logEnabled) {
            val families = allFamilies()
            log { "Found ${families.size} families" }
            families.forEach { (family, fonts) ->
                log { "Family: $family${fonts.joinToString(prefix = "\n\t")}" }
            }
        }
    }

    fun resolveFont(fontFamily: String): ResolvedFont {
        return resolveFont(listOf(fontFamily))
    }

    fun allFamilies(): Map<String, List<String>> {
        val types = getTypeInfoList("*")
        val families = types
            .filter { it.stealth == 0u }
            .groupBy { it.family!!.toKString() }
            .mapValues { (_, fonts) -> fonts.map { it.name?.toKStringFromUtf8() ?: "unknown" } }

        return families
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
