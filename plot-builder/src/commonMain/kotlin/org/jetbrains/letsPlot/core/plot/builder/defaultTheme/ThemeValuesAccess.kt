/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem

internal open class ThemeValuesAccess(
    private val values: Map<String, Any>,
    private val fontFamilyRegistry: FontFamilyRegistry
) {
    private val mem: MutableMap<String, Any> = HashMap()

    protected operator fun get(key: String): Any? = values[key]
    protected fun getValue(key: String): Any = values.getValue(key)

    /**
     * @param key List of option names: the most specific - first.
     */
    private fun getValue(key: List<String>): Any {
        val specificOption = key.first()
        return mem.getOrPut(specificOption) {
            return key.firstNotNullOfOrNull { values[it] }
                ?: throw IllegalStateException("No theme value found. Key : $key.")
        }
    }

    protected fun getNumber(key: List<String>): Double {
        val value = getValue(key)
        return (value as? Number)?.toDouble()
            ?: throw IllegalStateException("Theme value is not a number: $value. Key : $key.")
    }

    protected fun getBoolean(key: List<String>): Boolean {
        val value = getValue(key)
        return (value as? Boolean)
            ?: throw IllegalStateException("Theme value is not boolean: $value. Key : $key.")
    }

    /**
     * @param key List of option names: the most specific - first.
     */
    protected fun getElemValue(key: List<String>): Map<String, Any> {
        val specificOption = key.first()
        @Suppress("UNCHECKED_CAST")
        return mem.getOrPut(specificOption) {
            return key.asReversed().map { values[it] }
                .fold(HashMap<String, Any>()) { acc, v ->
                    if (v != null) {
                        acc.putAll(v as Map<String, Any>)
                    }
                    acc
                }
        } as Map<String, Any>
    }

    protected fun isElemBlank(key: List<String>): Boolean {
        val blankValue = getElemValue(key)[Elem.BLANK]
        return blankValue != null && blankValue as Boolean
    }

    protected fun getNumber(elem: Map<String, Any>, key: String): Double {
        return (elem.getValue(key) as Number).toDouble()
    }

    protected fun getColor(elem: Map<String, Any>, key: String): Color {
        return when (val value = elem.getValue(key)) {
            is Color -> value
            else -> Colors.parseColor(value as String)
        }
    }

    protected fun getFontFace(elem: Map<String, Any>): FontFace {
        return when (val value = elem.getValue(Elem.FONT_FACE)) {
            is FontFace -> value
            is String -> FontFace.fromString(value)
            else -> FontFace.NORMAL
        }
    }

    private fun getFontFamily(elem: Map<String, Any>): FontFamily {
//        val monospaced = getMonospaced(elem)
        val value = elem.getValue(Elem.FONT_FAMILY) as? String
        return value?.let {
//            FontFamily(it, monospaced)
            fontFamilyRegistry.get(value)
        } ?: throw IllegalStateException("Theme value '${Elem.FONT_FAMILY}' is not a string. Elem : $elem.")
    }

//    private fun getMonospaced(elem: Map<String, Any>): Boolean {
//        val value = elem.getValue(Elem.FONT_MONOSPACED)
//        return (value as? Boolean)
//            ?: throw IllegalStateException("Theme value '${Elem.FONT_MONOSPACED}'  is not a boolean. Elem : $elem.")
//    }

    // element_text(family, face, size, color)
    protected fun getTextStyle(elem: Map<String, Any>): ThemeTextStyle {
        return ThemeTextStyle(
            family = getFontFamily(elem),
            face = getFontFace(elem),
            size = getNumber(elem, Elem.SIZE),
            color = getColor(elem, Elem.COLOR)
        )
    }

    protected fun getTextJustification(elem: Map<String, Any>): TextJustification {
        val hjust = getNumber(elem, Elem.HJUST)
        val vjust = getNumber(elem, Elem.VJUST)
        return TextJustification(hjust, vjust)
    }

    protected fun getMargins(elem: Map<String, Any>): Thickness {
        return Thickness(
            top = getNumber(elem, Elem.Margin.TOP),
            right = getNumber(elem, Elem.Margin.RIGHT),
            bottom = getNumber(elem, Elem.Margin.BOTTOM),
            left = getNumber(elem, Elem.Margin.LEFT),
        )
    }

    protected fun getPadding(elem: Map<String, Any>): Thickness {
        return Thickness(
            top = getNumber(elem, Elem.Padding.TOP),
            right = getNumber(elem, Elem.Padding.RIGHT),
            bottom = getNumber(elem, Elem.Padding.BOTTOM),
            left = getNumber(elem, Elem.Padding.LEFT),
        )
    }
}