/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

enum class CharCategory(val value: Double) {
    NARROW(0.6),
    NORMAL(1.0),
    WIDE(1.4);

    companion object {

        fun getCharRatio(ch: Char, options: TextFontOptions): Double {
            val category = when (ch) {
                in options.narrowChars -> NARROW
                in options.wideChars -> WIDE
                else -> NORMAL
            }
            return category.value
        }

        fun getCharListByCategory(category: CharCategory, font: String): List<Char> {
            val options = getOptionsForFont(font)
            return when (category) {
                NARROW -> options.narrowChars
                WIDE -> options.wideChars
                NORMAL -> options.normalChars
            }
        }
    }
}

// setting for fonts...

abstract class TextFontOptions(
    val fontRatio: Double,
    val fontBoldRatio: Double = 1.075,
    val narrowChars: List<Char>,
    val wideChars: List<Char>,
) {
    val normalChars = (32..127).map(Int::toChar) - narrowChars - wideChars
}

// Lucida Grande
class LucidaGrandeOptions : TextFontOptions(
    fontRatio = 0.69,
    fontBoldRatio = 1.2,
    narrowChars = listOf(
        'I', 'J',
        'f', 'i', 'j', 'l', 'r', 't',
        ' ',
        '!', '"', '\'', '(', ')', ',', '.', ':', ';', '?',
        '[', ']', '{', '|', '}'
    ),
    wideChars = listOf(
        'D', 'G', 'H', 'M', 'N', 'O', 'Q', 'W',
        'm', 'w',
        '+', '<', '=', '>', '@'
    )
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

fun getOptionsForFont(font: String?): TextFontOptions = when (font) {
    "Arial", "Helvetica", "sans-serif" -> HelveticaTextFontOptions()
    "Courier", "Courier New", "monospace" -> MonospacedOptions()
    "Lucida Grande" -> LucidaGrandeOptions()
    "Verdana" -> VerdanaOptions()
    "Geneva" -> GenevaOptions()
    "Times", "Times New Roman", "serif" -> TimesOptions()
    "Georgia" -> GeorgiaOptions()
    else -> LucidaGrandeOptions() // used by default
}

