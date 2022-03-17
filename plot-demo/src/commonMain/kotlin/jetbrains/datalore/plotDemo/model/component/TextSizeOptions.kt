/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

abstract class TextFontOptions(
    val fontRatio: Double,
    val narrowChars: List<Char>,
    val wideChars: List<Char>,
) {
    //val normalChars: List<Char> = (0..255).map(Int::toChar) - narrowChars - wideChars
    val normalChars = (32..127).map(Int::toChar) - narrowChars - wideChars
    //val extended = (128..255).map(Int::toChar) - narrowChars - wideChars
}

class DefaultTextFontOptions : TextFontOptions(
    fontRatio = 0.67,
    narrowChars = emptyList(),
    wideChars = emptyList()
)

// Helvetica, Arial
class HelveticaTextFontOptions : TextFontOptions(
    fontRatio = 0.73,
    narrowChars = listOf(
        'I',
        'f', 'i', 'j', 'l', 'r', 't',
        '!', '"', '\'', '(', ')', '*', ',', '-', '.', '/',
        ':', ';', '[', '\\', ']', '`', '{', '|', '}'
    ),
    wideChars = listOf(
        'G', 'O', 'Q', 'M', 'W',
        'm',
        '%', '@'
    )
)

class LucidaGrandeOptions : TextFontOptions(
    fontRatio = 0.69,
    narrowChars = listOf(
        'I', 'J',
        'f', 'i', 'j', 'l', 'r', 't',
        '!', '"', '\'', '(', ')', ',', '.', ':', ';', '?',
        '[', ']', '{', '|', '}'
    ),
    wideChars = listOf(
        'D', 'G', 'H', 'M', 'N', 'O', 'Q', 'W',
        'm', 'w',
        '+', '<', '=', '>', '@'
    )
)

class VerdanaOptions : TextFontOptions(
    fontRatio = 0.77,
    narrowChars = listOf(
        'I', 'J',
        'f', 'i', 'j', 'l', 'r', 't',
        '!', '"', '\'', '(', ')', ',', '-', '.', '/', ':', ';', '[', '\\', ']', '|'
    ),
    wideChars = listOf(
        'M', 'O', 'Q', 'W',
        'm', 'w',
        '#', '%', '+', '@', '<', '=', '>', '^', '~'
    )
)

class GenevaOptions : TextFontOptions(
    fontRatio = 0.75,
    narrowChars = listOf(
        'I',
        'f', 'i', 'j', 'l', 'r', 't',
        '!', '\'', '(', ')', ',', '-', '.', ':', ';', '[', ']', '{', '|', '}'
    ),
    wideChars = listOf(
        'M', 'W',
        'm', 'w',
        '%', '@'
    )
)

class TimesOptions : TextFontOptions(
    fontRatio = 0.67,
    narrowChars = listOf(
        'I', 'J',
        'f', 'i', 'j', 'l', 'r', 't', 's',
        '!', '\'', '(', ')', ',', '-', '.', '/', ':', ';',
        '[', '\\', ']', '`', '|',
    ),
    wideChars = listOf(
        'A', 'D', 'G', 'H', 'K', 'M', 'N', 'O', 'Q', 'U', 'V', 'W', 'X', 'Y',
        'w', 'm',
        '%', '&', '@'
    )
)

class GeorgiaOptions : TextFontOptions(
    fontRatio = 0.77,
    narrowChars = listOf(
        'I',
        'f', 'i', 'j', 'l', 'r', 's', 't', 'z',
        '!', '"', '\'', '(', ')', ',', '-', '.', ':', ';',
        '[', ']', '{', '|', '}'
    ),
    wideChars = listOf(
        'H', 'M', 'W',
        'm',
        '%', '@'
    )
)

class MonospacedOptions : TextFontOptions(
    fontRatio = 0.61,
    narrowChars = emptyList(),
    wideChars = emptyList()
)

fun getOptionsForFont(font: String): TextFontOptions = when (font) {
    "Arial", "Helvetica", "sans-serif" -> HelveticaTextFontOptions()
    "Courier", "Courier New", "monospace" -> MonospacedOptions()
    "Lucida Grande" -> LucidaGrandeOptions()
    "Verdana" -> VerdanaOptions()
    "Geneva" -> GenevaOptions()
    "Times", "Times New Roman", "serif" -> TimesOptions()
    "Georgia" -> GeorgiaOptions()
    else -> DefaultTextFontOptions()
}

fun getFontRatio(font: String): Double {
    val options = getOptionsForFont(font)
    return options.fontRatio
}

enum class CharCategory(private val value: Double) {
    NARROW(0.6),
    NORMAL(1.0),
    WIDE(1.4);

    val nameWithRatio = "$name ($value)"

    companion object {

        fun getCharRatio(ch: Char, options: TextFontOptions): Double {
            val category = when (ch) {
                in options.narrowChars -> NARROW
                in options.wideChars -> WIDE
                else -> NORMAL
            }
            return category.value
        }

        private val extendedCharLists = mapOf(
            "All printable" to (32..126).map(Int::toChar),
            "Letters" to ('a'..'z') + ('A'..'Z'),
            "Digits" to ('0'..'9'),
            "Symbols" to (32..126).map(Int::toChar) -
                    (('0'..'9') + ('a'..'z') + ('A'..'Z')),
            "Extended chars" to (128..255).map(Int::toChar)
        )

        fun getCharCategoryNamesWithRatios(): List<String> =
            values().map(CharCategory::nameWithRatio) + extendedCharLists.keys

        private fun getCharListByCategory(category: CharCategory, font: String): List<Char> {
            val options = getOptionsForFont(font)
            return when (category) {
                NARROW -> options.narrowChars
                WIDE -> options.wideChars
                NORMAL -> options.normalChars
            }
        }

        fun getCharsForCategory(catName: String?, font: String): List<Char> {
            val category = values().find { it.name == catName || it.nameWithRatio == catName }
            return when {
                category != null -> getCharListByCategory(category, font)
                extendedCharLists.containsKey(catName) -> extendedCharLists[catName]!!.toList()
                else -> emptyList()
            }
        }
    }
}