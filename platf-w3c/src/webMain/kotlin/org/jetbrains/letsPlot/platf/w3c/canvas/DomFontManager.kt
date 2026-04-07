/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.canvas

import kotlinx.browser.document
import org.jetbrains.letsPlot.core.canvas.Font
import org.w3c.dom.HTMLStyleElement
import kotlin.js.ExperimentalWasmJsInterop

class DomFontManager private constructor(
    private val fontSets: Map<String, FontSet>
) {
    data class FontSet(
        val familyName: String,
        val regularFontPath: String? = null,
        val boldFontPath: String? = null,
        val italicFontPath: String? = null,
        val boldItalicFontPath: String? = null
    ) {
        internal fun faceSpecs(): List<FontFaceSpec> = buildList {
            regularFontPath?.let { add(FontFaceSpec(familyName, it, "400", "normal")) }
            boldFontPath?.let { add(FontFaceSpec(familyName, it, "700", "normal")) }
            italicFontPath?.let { add(FontFaceSpec(familyName, it, "400", "italic")) }
            boldItalicFontPath?.let { add(FontFaceSpec(familyName, it, "700", "italic")) }
        }
    }

    internal data class FontFaceSpec(
        val familyName: String,
        val fontPath: String,
        val fontWeight: String,
        val fontStyle: String
    ) {
        val key: String = "$familyName|$fontWeight|$fontStyle|$fontPath"

        fun toCssRule(): String {
            return """
                @font-face {
                    font-family: "$familyName";
                    src: url("$fontPath") format("truetype");
                    font-weight: $fontWeight;
                    font-style: $fontStyle;
                }
            """.trimIndent()
        }

        fun toProbe(): String {
            val styleToken = if (fontStyle == "italic") "italic " else ""
            val weightToken = if (fontWeight == "700") "bold " else ""
            return "${styleToken}${weightToken}16px \"${familyName}\""
        }
    }

    private val registeredFaces = linkedMapOf<String, FontFaceSpec>()
    private var styleElement: HTMLStyleElement? = null

    fun resolveFont(font: Font): Font {
        val fontSet = fontSets[font.fontFamily] ?: return font
        register(fontSet)
        ensureStyleInstalled()
        return font.copy(fontFamily = fontSet.familyName.toCssFamily())
    }

    private fun register(fontSet: FontSet) {
        fontSet.faceSpecs().forEach { face ->
            if (!registeredFaces.containsKey(face.key)) {
                registeredFaces[face.key] = face
            }
        }
    }

    private fun ensureStyleInstalled() {
        if (registeredFaces.isEmpty()) {
            return
        }

        val style = styleElement ?: (document.createElement("style") as HTMLStyleElement).also {
            document.head?.appendChild(it)
            styleElement = it
        }
        style.textContent = registeredFaces.values.joinToString(separator = "\n") { it.toCssRule() }
    }

    private fun String.toCssFamily(): String {
        if (startsWith("\"") || startsWith("'") || contains(",")) {
            return this
        }
        return if (any(Char::isWhitespace)) "\"$this\"" else this
    }

    fun installAllFontFaces() {
        fontSets.values.forEach { register(it) }
        ensureStyleInstalled()
    }

    companion object {
        val DEFAULT = DomFontManager(emptyMap())

        fun configured(vararg fontMappings: Pair<String, FontSet>): DomFontManager {
            return DomFontManager(fontMappings.toMap())
        }
    }
}
